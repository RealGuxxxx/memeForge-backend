package com.memeforge.tokenservice.model.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;

public enum TokenStatus {
    ACTIVE("已激活", 1),
    FREEZE("已冻结", 1);
    
    private final String description;
    private final int  code;


    
    TokenStatus(String description, int code) {
        this.description = description;
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }
}