package com.memeforge.transactionservice.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * AMM交易VO
 */
@Data
public class TransactionVO {
    /**
     * 交易ID
     */
    private Long id;
    
    /**
     * 代币地址
     */
    private String tokenAddress;
    
    /**
     * 代币符号
     */
    private String tokenSymbol;
    
    /**
     * 用户钱包地址
     */
    private String userAddress;
    
    /**
     * 交易数量
     */
    private BigDecimal amount;
    
    /**
     * 单价
     */
    private BigDecimal price;
    
    /**
     * 总价值
     */
    private BigDecimal totalValue;
    
    /**
     * 交易类型: BUY, SELL
     */
    private String transactionType;
    
    /**
     * 交易哈希
     */
    private String transactionHash;
    
    /**
     * 交易时间
     */
    private Date createdAt;
}