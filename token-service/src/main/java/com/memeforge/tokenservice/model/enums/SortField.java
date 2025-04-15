package com.memeforge.tokenservice.model.enums;

import lombok.Getter;

@Getter
public enum SortField {
    LIQUIDITY("流动性"),
    VOLUME("交易量"),
    CREATE_TIME("创建时间");

    private final String description;

    SortField(String description) {
        this.description = description;
    }
} 