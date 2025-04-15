package com.memeforge.transactionservice.client;

import com.memeforge.common.response.BaseResponse;
import com.memeforge.tokenservice.model.vo.TokenVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

/**
 * Token服务客户端
 * 用于调用token-service服务
 */
@FeignClient(
        name = "token-service",
        configuration = FeignConfig.class
)
public interface TokenServiceClient {

    /**
     * 根据代币地址获取代币详情
     *
     * @param address 代币地址
     * @return 代币详情
     */
    @GetMapping("/api/v1/tokens/address/{address}")
    TokenVO getTokenByAddress(@PathVariable("address") String address);

    /**
     * 根据代币ID获取代币详情
     *
     * @param tokenId 代币ID
     * @return 代币详情
     */
    @GetMapping("/api/v1/tokens/id/{tokenId}")
    BaseResponse<TokenVO> getTokenById(@PathVariable("tokenId") Long tokenId);

    @PutMapping("/api/v1/token-holdings/token/{tokenAddress}/holder/{holderAddress}")
    BaseResponse<Void> updateTokenHolding(
            @PathVariable("tokenAddress") String tokenAddress,
            @PathVariable("holderAddress") String holderAddress,
            @RequestParam("amount") Double amount
    );
}

@Configuration
class FeignConfig {
    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}