package com.memeforge.tokenservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TokenCreateRequest {
    @NotBlank(message = "代币地址不能为空")
    private String tokenAddress;

    @NotBlank(message = "代币名称不能为空")
    private String name;

    @NotBlank(message = "代币符号不能为空")
    private String symbol;

    private String description;

    @NotBlank(message = "IPFS URI不能为空")
    private String ipfsCid;

    @NotBlank(message = "总供应量不能为空")
    private String totalSupply;

    @NotBlank(message = "合约地址不能为空")
    private String creatorAddress;
}