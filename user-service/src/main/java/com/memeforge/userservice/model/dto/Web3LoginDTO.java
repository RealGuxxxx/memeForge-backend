package com.memeforge.userservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Web3LoginDTO {
    @NotBlank(message = "钱包地址不能为空")
    private String walletAddress;
    
    @NotBlank(message = "签名不能为空")
    private String signature;
}