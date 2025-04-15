package com.memeforge.tokenservice.model.vo;
import com.memeforge.tokenservice.model.enums.TokenStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TokenVO {
    private Long id;
    private String tokenAddress;
    private String name;
    private String symbol;
    private String description;
    private Long totalSupply;
    private String ipfsCid;
    private TokenStatus status;
    private Date createdAt;
    
}