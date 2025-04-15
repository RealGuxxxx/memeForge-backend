package com.memeforge.tokenservice.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenListRequest {
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum;
    
    @NotNull(message = "每页大小不能为空")
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize;
    
    private String searchKeyword;
    private String creatorAddress;
    private Boolean verified;
    private String sortBy;
    private String sortOrder;
    
    /**
     * 获取当前页码
     * @return 当前页码
     */
    public Integer getCurrent() {
        return pageNum;
    }
    
    /**
     * 获取每页大小
     * @return 每页大小
     */
    public Integer getSize() {
        return pageSize;
    }
}