package com.memeforge.tokenservice.feignService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户交易过的代币VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTradedTokenVO {
    
    /**
     * 用户地址
     */
    private String userAddress;
    
    /**
     * 交易过的代币地址列表
     */
    private List<String> tokenAddresses;
} 