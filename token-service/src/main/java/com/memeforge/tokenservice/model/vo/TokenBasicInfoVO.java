package com.memeforge.tokenservice.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代币基本信息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenBasicInfoVO {
    
    /**
     * 代币地址
     */
    private String tokenAddress;
    
    /**
     * 代币名称
     */
    private String name;
    
    /**
     * 代币符号
     */
    private String symbol;
    
    /**
     * 代币图标IPFS CID
     */
    private String ipfsCid;
    
    /**
     * 代币精度
     */
    private Integer decimals;
} 