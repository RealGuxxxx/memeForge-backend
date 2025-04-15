package com.memeforge.tokenservice.controller;

import com.memeforge.common.response.BaseResponse;
import com.memeforge.common.response.ResultUtils;
import com.memeforge.tokenservice.model.vo.TokenBasicInfoVO;
import com.memeforge.tokenservice.service.TokenHoldingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代币持仓控制器
 * 提供代币持仓、持有人等相关接口
 */
@RestController
@RequestMapping("/api/v1/token-holdings")
@RequiredArgsConstructor
@Slf4j
public class TokenHoldingController {

    private final TokenHoldingService tokenHoldingService;


    /**
     * 获取用户交易过的代币列表
     * @param holderAddress 用户地址
     * @return 代币列表
     */
    @GetMapping("/holder/{holderAddress}/traded-tokens")
    public BaseResponse<List<TokenBasicInfoVO>> getUserTradedTokens(@PathVariable String holderAddress) {
        try {
            List<TokenBasicInfoVO> tradedTokens = tokenHoldingService.getUserTradedTokens(holderAddress);
            return ResultUtils.success(tradedTokens);
        } catch (Exception e) {
            log.error("获取用户交易过的代币列表失败: {}", e.getMessage(), e);
            return ResultUtils.error("获取用户交易过的代币列表失败: " + e.getMessage());
        }
    }
}