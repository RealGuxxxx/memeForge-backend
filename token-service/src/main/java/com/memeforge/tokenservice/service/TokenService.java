package com.memeforge.tokenservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memeforge.tokenservice.model.entity.Token;
import com.memeforge.tokenservice.model.enums.SortField;
import com.memeforge.tokenservice.model.enums.TokenStatus;
import com.memeforge.tokenservice.model.request.TokenCreateRequest;
import com.memeforge.tokenservice.model.vo.TokenVO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TokenService {

    /**
     * 创建代币（需校验合约地址唯一性）
     * @param request 创建请求
     */
    Token createToken(@Valid TokenCreateRequest request);

    /**
     * 分页查询代币列表（支持状态过滤）
     * @param pageable 分页参数
     * @param keyword 搜索关键词
     * @param status 代币状态（可选）
     */
    Page<TokenVO> pageTokens(Pageable pageable, String keyword, TokenStatus status);

    /**
     * 根据地址获取代币详情
     * @param tokenAddress 代币合约地址
     */
    TokenVO getByAddress(String tokenAddress);

    void refreshCache(String address);
}