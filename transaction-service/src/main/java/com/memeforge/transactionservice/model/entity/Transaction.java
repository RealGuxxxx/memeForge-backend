package com.memeforge.transactionservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 交易表
 * @TableName transaction
 */
@TableName(value ="transaction")
@Data
public class Transaction implements Serializable {
    /**
     * 交易ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代币地址
     */
    @TableField(value = "token_address")
    private String tokenAddress;

    /**
     * 用户钱包地址
     */
    @TableField(value = "user_address")
    private String userAddress;

    /**
     * 交易数量
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 单价
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 交易类型：BUY/SELL
     */
    @TableField(value = "transaction_type")
    private String transactionType;

    /**
     * 交易哈希
     */
    @TableField(value = "transaction_hash")
    private String transactionHash;

    /**
     * 交易时间
     */
    @TableField(value = "created_at")
    private Date createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}