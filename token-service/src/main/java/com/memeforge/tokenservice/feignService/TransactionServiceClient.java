package com.memeforge.tokenservice.feignService;

import com.memeforge.common.response.BaseResponse;
import com.memeforge.tokenservice.feignService.dto.UserTradedTokenVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 交易服务Feign客户端
 */
@FeignClient(name = "transaction-service", path = "/api/v1/transactions")
public interface TransactionServiceClient {

    /**
     * 获取用户交易过的代币地址列表
     * @param userAddress 用户地址
     * @return 代币地址列表
     */
    @GetMapping("/user/{userAddress}/traded-tokens")
    BaseResponse<UserTradedTokenVO> getUserTradedTokens(@PathVariable("userAddress") String userAddress);
} 