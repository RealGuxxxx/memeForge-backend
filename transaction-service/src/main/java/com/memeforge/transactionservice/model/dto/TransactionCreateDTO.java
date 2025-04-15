package com.memeforge.transactionservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * AMM交易创建DTO
 */
@Data
public class TransactionCreateDTO {
    /**
     * 代币地址
     */
    @NotBlank(message = "代币地址不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "代币地址格式不正确")
    private String tokenAddress;
    
    /**
     * 用户钱包地址
     */
    @NotBlank(message = "用户地址不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$", message = "钱包地址格式不正确")
    private String userAddress;
    
    /**
     * 交易数量
     */
    @NotNull(message = "交易数量不能为空")
    @Positive(message = "交易数量必须为正数")
    private BigDecimal amount;
    
    /**
     * 交易价格
     */
    @NotNull(message = "交易价格不能为空")
    @Positive(message = "交易价格必须为正数")
    private BigDecimal price;
    
    /**
     * 交易类型: BUY, SELL
     */
    @NotBlank(message = "交易类型不能为空")
    @Pattern(regexp = "^(BUY|SELL)$", message = "交易类型必须为BUY或SELL")
    private String transactionType;
    
    /**
     * 交易哈希
     */
    @NotBlank(message = "交易哈希不能为空")
    @Pattern(regexp = "^0x[a-fA-F0-9]{64}$", message = "交易哈希格式不正确")
    private String transactionHash;
    
    private String priceText;
    
    private Boolean skipStatsUpdate;
}