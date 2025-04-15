package com.memeforge.tokenservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.memeforge.tokenservice.service.TokenHoldingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.memeforge.common.response.BaseResponse;
import com.memeforge.tokenservice.feignService.TransactionServiceClient;
import com.memeforge.tokenservice.feignService.dto.UserTradedTokenVO;
import com.memeforge.tokenservice.mapper.TokenMapper;
import com.memeforge.tokenservice.model.entity.Token;
import com.memeforge.tokenservice.model.vo.TokenBasicInfoVO;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenHoldingServiceImpl implements TokenHoldingService {

    private final TokenMapper tokenMapper;
    private final TransactionServiceClient transactionServiceClient;


    @Override
    public List<TokenBasicInfoVO> getUserTradedTokens(String holderAddress) {
        try {
            log.info("获取用户 {} 交易过的代币列表", holderAddress);
            
            BaseResponse<UserTradedTokenVO> response = transactionServiceClient.getUserTradedTokens(holderAddress);
            if (response == null || response.getData() == null || response.getData().getTokenAddresses() == null) {
                log.warn("获取用户交易过的代币地址失败：响应为空");
                return Collections.emptyList();
            }
            
            List<String> tokenAddresses = response.getData().getTokenAddresses();
            log.info("获取到用户交易过的代币地址数量: {}", tokenAddresses.size());
            
            // 根据代币地址查询代币详情
            return tokenAddresses.stream()
                    .map(tokenAddress -> {
                        try {
                            // 查询代币信息
                            Token token = tokenMapper.selectOne(
                                    new LambdaQueryWrapper<Token>()
                                            .eq(Token::getTokenAddress, tokenAddress)
                            );
                            
                            if (token != null) {
                                return TokenBasicInfoVO.builder()
                                        .tokenAddress(token.getTokenAddress())
                                        .name(token.getName())
                                        .symbol(token.getSymbol())
                                        .ipfsCid(token.getIpfsCid())
                                        .build();
                            } else {
                                log.warn("代币 {} 不存在于数据库中", tokenAddress);
                                return TokenBasicInfoVO.builder()
                                        .tokenAddress(tokenAddress)
                                        .name("Unknown Token")
                                        .symbol("???")
                                        .decimals(18)
                                        .build();
                            }
                        } catch (Exception e) {
                            log.error("获取代币 {} 信息失败", tokenAddress, e);
                            return null;
                        }
                    })
                    .filter(token -> token != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户交易过的代币列表失败", e);
            throw new RuntimeException("获取用户交易过的代币列表失败: " + e.getMessage());
        }
    }

}