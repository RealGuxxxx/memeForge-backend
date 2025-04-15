package com.memeforge.userservice.model.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {
    @Size(min = 2, max = 20, message = "昵称长度应在2-20个字符之间")
    private String nickname;

    @Pattern(regexp = "^(http|https)://.*", message = "头像必须是有效的URL")
    private String avatar;

    @Size(max = 200, message = "个人简介长度不能超过200个字符")
    private String bio;
}