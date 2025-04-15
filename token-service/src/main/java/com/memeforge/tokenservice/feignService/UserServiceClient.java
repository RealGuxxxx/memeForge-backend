package com.memeforge.tokenservice.feignService;

import com.memeforge.common.response.BaseResponse;
import com.memeforge.tokenservice.feignService.dto.UserTradedTokenVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 交易服务Feign客户端
 */
@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserServiceClient {

    @GetMapping("/wallet/{walletAddress}/id")
    BaseResponse<Long> getUserIdByWalletAddress(@PathVariable("walletAddress") String walletAddress);
} 