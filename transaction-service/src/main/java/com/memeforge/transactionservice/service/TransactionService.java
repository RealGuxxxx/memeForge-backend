package com.memeforge.transactionservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memeforge.transactionservice.model.dto.TransactionCreateDTO;
import com.memeforge.transactionservice.model.entity.Transaction;
import com.memeforge.transactionservice.model.vo.TransactionVO;
import com.memeforge.transactionservice.model.vo.UserTradedTokenVO;

import java.util.List;

/**
 * 交易服务接口
 */
public interface TransactionService {
    
    /**
     * 创建交易
     * @param createDTO 创建交易DTO
     * @return 交易VO
     */
    TransactionVO createTransaction(TransactionCreateDTO createDTO);
    
    /**
     * 根据用户地址查询交易列表
     * @param userAddress 用户地址
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 交易列表
     */
    Page<TransactionVO> listTransactionsByUser(String userAddress, Integer pageNum, Integer pageSize);
    
    /**
     * 获取用户交易过的代币地址列表
     * @param userAddress 用户地址
     * @return 代币地址列表
     */
    UserTradedTokenVO getUserTradedTokens(String userAddress);
}
