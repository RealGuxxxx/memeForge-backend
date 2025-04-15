package com.memeforge.tokenservice.service;

import com.memeforge.tokenservice.model.vo.TokenBasicInfoVO;

import java.util.List;

/**
 * 代币持仓服务接口
 * 负责代币持仓、转账等相关功能
 */
public interface TokenHoldingService {


    /**
     * 获取用户交易过的代币列表
     * 从交易服务获取用户交易过的代币地址，然后查询代币详情
     * @param holderAddress 用户地址
     * @return 代币基本信息列表
     */
    List<TokenBasicInfoVO> getUserTradedTokens(String holderAddress);
}