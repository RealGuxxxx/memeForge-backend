package com.memeforge.transactionservice.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户交易代币信息VO - 简化版
 */
@Data
public class UserTradedTokenVO {

    private String userAddress;

    private List<String> tokenAddresses;
} 