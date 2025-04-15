package com.memeforge.tokenservice.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 代币表
 * @TableName token
 */
@TableName(value ="token")
@Data
public class Token implements Serializable {
    /**
     * 代币ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代币名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 代币符号
     */
    @TableField(value = "symbol")
    private String symbol;

    /**
     * 代币描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 总供应量
     */
    @TableField(value = "total_supply")
    private Long totalSupply;

    /**
     * 代币元数据在IPFS上的存储地址（URI格式）
     */
    @TableField(value = "ipfs_CID")
    private String ipfsCid;

    /**
     * 创建者ID（关联user表）
     */
    @TableField(value = "creator_id")
    private Long creatorId;

    /**
     * 智能合约地址
     */
    @TableField(value = "token_address")
    private String tokenAddress;

    /**
     * 状态（0已激活，1已冻结）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    private Date createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}