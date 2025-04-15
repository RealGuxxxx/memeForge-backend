package com.memeforge.tokenservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memeforge.common.response.BaseResponse;
import com.memeforge.tokenservice.feignService.UserServiceClient;
import com.memeforge.tokenservice.model.enums.SortField;
import com.memeforge.tokenservice.model.enums.TokenStatus;
import com.memeforge.tokenservice.mapper.TokenMapper;
import com.memeforge.tokenservice.model.entity.Token;
import com.memeforge.tokenservice.model.request.TokenCreateRequest;
import com.memeforge.tokenservice.model.vo.TokenVO;
import com.memeforge.tokenservice.service.TokenService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenMapper tokenMapper;

    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    @Transactional
    public Token createToken(TokenCreateRequest request) {
        try {
            log.info("开始创建代币记录: {}", request);

            if (request.getCreatorAddress() == null || request.getCreatorAddress().isEmpty()) {
                throw new RuntimeException("创建者地址不能为空");
            }
            
            BaseResponse<Long> response = userServiceClient.getUserIdByWalletAddress(request.getCreatorAddress());
            if (response == null || response.getCode() != 0 || response.getData() == null) {
                throw new RuntimeException("获取创建者ID失败，请确认用户已注册");
            }
            
            Long creatorId = response.getData();
            log.info("获取创建者ID成功: address={}, id={}", request.getCreatorAddress(), creatorId);

            Token token = new Token();
            BeanUtils.copyProperties(request, token);
            
            token.setCreatorId(creatorId);

            if(request.getTotalSupply() != null && !request.getTotalSupply().isEmpty()) {
                try {
                    token.setTotalSupply(Long.parseLong(request.getTotalSupply()));
                } catch (NumberFormatException e) {
                    log.warn("总供应量转换错误，使用默认值 0: {}", e.getMessage());
                    token.setTotalSupply(0L);
                }
            } else {
                token.setTotalSupply(0L);
            }

            token.setStatus(TokenStatus.ACTIVE.getCode());
            token.setCreatedAt(new Date());

            // 保存到数据库
            tokenMapper.insert(token);
            log.info("代币记录创建成功: {}", token);
            return token;
        } catch (FeignException e) {
            log.error("调用用户服务失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取用户信息失败，请稍后再试");
        } catch (Exception e) {
            log.error("创建代币记录失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建代币记录失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "tokenPage", key = "'page:'+#pageable.pageNumber+':size:'+#pageable.pageSize+':keyword:'+(#keyword==null?'':#keyword)")
    public Page<TokenVO> pageTokens(Pageable pageable, String keyword, TokenStatus status) {
        try {
            log.info("开始分页查询代币列表，关键词: {}, 状态: {}, 页码: {}, 每页大小: {}, 排序: {}",
                    keyword, status, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            log.info("使用页码参数: {}，页大小: {}", pageNumber, pageSize);

            Page<Token> page = new Page<>(pageNumber, pageSize);
            LambdaQueryWrapper<Token> wrapper = new LambdaQueryWrapper<>();

            // 添加搜索条件
            if (StringUtils.hasText(keyword)) {
                wrapper.and(w -> w
                        .like(Token::getName, keyword)
                        .or()
                        .like(Token::getSymbol, keyword)
                        .or()
                        .like(Token::getTokenAddress, keyword)
                );
            }

            // 添加状态筛选
            if (status != null) {
                wrapper.eq(Token::getStatus, status);
            }

            // 添加排序
            wrapper.orderByDesc(Token::getCreatedAt);

            // 执行分页查询
            Page<Token> tokenPage = tokenMapper.selectPage(page, wrapper);
            log.info("分页查询结果: 总记录数: {}, 总页数: {}, 当前页记录数: {}",
                    tokenPage.getTotal(), tokenPage.getPages(), tokenPage.getRecords().size());

            Page<TokenVO> result = new Page<>();
            BeanUtils.copyProperties(tokenPage, result, "records");
            result.setRecords(tokenPage.getRecords().stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList()));

            log.info("分页查询完成，总记录数: {}, 当前页记录数: {}", result.getTotal(), result.getRecords().size());
            
            log.info("分页返回数据第一条: {}", result.getRecords().isEmpty() ? "空" :
                 result.getRecords().get(0).getName() + "," + result.getRecords().get(0).getSymbol());
            log.info("当前页码: {}, 总页数: {}", result.getCurrent(), result.getPages());
            
            return result;
        } catch (Exception e) {
            log.error("分页查询代币列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("分页查询代币列表失败: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "tokenDetail", key = "#tokenAddress")
    public TokenVO getByAddress(String tokenAddress) {
        try {
            log.info("开始查询代币详情，地址: {}", tokenAddress);

            Token token = tokenMapper.selectOne(new LambdaQueryWrapper<Token>()
                    .eq(Token::getTokenAddress, tokenAddress));

            if (token == null) {
                return null;
            }

            TokenVO tokenVO = convertToVO(token);

            log.info("查询代币详情完成");
            return tokenVO;
        } catch (Exception e) {
            log.error("查询代币详情失败: {}", e.getMessage(), e);
            throw new RuntimeException("查询代币详情失败: " + e.getMessage());
        }
    }


    @Override
    @CacheEvict(value = {"tokenDetail", "tokenBasicInfo", "tokenPage", "topTokens"}, key = "#address")
    public void refreshCache(String address) {
        log.info("开始刷新代币缓存，地址: {}", address);
        // 由于使用了 @CacheEvict 注解，Spring 会自动清除与指定地址相关的缓存
        log.info("代币缓存刷新完成，地址: {}", address);
    }

    private TokenVO convertToVO(Token token) {
        TokenVO vo = new TokenVO();
        BeanUtils.copyProperties(token, vo);
        return vo;
    }

}




