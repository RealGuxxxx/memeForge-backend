package com.memeforge.tokenservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memeforge.common.response.BaseResponse;
import com.memeforge.common.response.ResultUtils;
import com.memeforge.tokenservice.model.entity.Token;
import com.memeforge.tokenservice.model.enums.TokenStatus;
import com.memeforge.tokenservice.model.request.TokenCreateRequest;
import com.memeforge.tokenservice.model.vo.TokenVO;
import com.memeforge.tokenservice.service.TokenService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 代币控制器
 * 提供代币基础信息相关接口
 */
@RestController
@RequestMapping("/api/v1/tokens")
@Slf4j
public class TokenController {

    @Resource
    private TokenService tokenService;

    /**
     * 创建代币
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping
    public BaseResponse<Token> createToken(@Valid @RequestBody TokenCreateRequest request) {
        try {
            Token token = tokenService.createToken(request);
            return ResultUtils.success(token);
        } catch (Exception e) {
            return ResultUtils.error("保存代币信息失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询代币列表
     * @param page 页码(从0开始)
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param status 代币状态
     * @param sort 排序参数
     * @return 代币列表
     */
    @GetMapping("/page")
    public BaseResponse<Page<TokenVO>> pageTokens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TokenStatus status,
            @RequestParam(required = false, defaultValue = "createdAt,desc") String sort) {
        try {
            log.info("分页查询请求 - Page: {} (0-based), Size: {}, Sort: {}, Keyword: {}, Status: {}",
                page, size, sort, keyword, status);
            
            // 创建Pageable对象
            String[] sortParams = sort.split(",");
            Sort.Direction direction = Sort.Direction.DESC;
            String property = "createdAt";
            
            if (sortParams.length > 0) {
                property = sortParams[0];
                if (sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1])) {
                    direction = Sort.Direction.ASC;
                }
            }
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
            log.info("构建的分页参数: {}", pageable);
            
            Page<TokenVO> result = tokenService.pageTokens(pageable, keyword, status);
            log.info("分页查询结果 - 总记录: {}, 总页数: {}, 当前页数据量: {}", 
                result.getTotal(), result.getPages(), result.getRecords().size());
            
            // 直接返回分页对象，确保前端能正确解析
            return ResultUtils.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("分页查询代币列表失败: {}", e.getMessage(), e);
            return ResultUtils.error("分页查询代币列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据代币地址获取详情
     * @param address 代币地址
     * @return 代币详情
     */
    @GetMapping("/address/{address}")
    public BaseResponse<TokenVO> getTokenByAddress(@PathVariable String address) {
        try {
            TokenVO token = tokenService.getByAddress(address);
            if (token == null) {
                return ResultUtils.error(404, "代币不存在");
            }
            return ResultUtils.success(token);
        } catch (Exception e) {
            return ResultUtils.error("获取代币详情失败：" + e.getMessage());
        }
    }

    /**
     * 刷新代币缓存
     * @param address 代币地址
     * @return 结果
     */
    @PostMapping("/refresh/{address}")
    public BaseResponse<Void> refreshTokenCache(@PathVariable String address) {
        try {
            tokenService.refreshCache(address);
            return ResultUtils.success(null);
        } catch (Exception e) {
            return ResultUtils.error("刷新代币缓存失败：" + e.getMessage());
        }
    }

}