package com.memeforge.userservice.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable {
    private Long id;
    private String walletAddress;
    private String nickname;
    private String avatar;
    private String bio;
    private Date lastLoginTime;
    private Date createdAt;
    private Date updatedAt;
}