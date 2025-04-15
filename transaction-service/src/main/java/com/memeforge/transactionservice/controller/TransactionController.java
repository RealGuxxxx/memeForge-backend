package com.memeforge.transactionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memeforge.common.response.BaseResponse;
import com.memeforge.common.response.ResultUtils;
import com.memeforge.transactionservice.model.dto.TransactionCreateDTO;
import com.memeforge.transactionservice.model.vo.TransactionVO;
import com.memeforge.transactionservice.model.vo.UserTradedTokenVO;
import com.memeforge.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * 交易控制器
 * 提供交易相关接口
 */
@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 创建交易
     * @param createDTO 创建交易DTO
     * @return 创建结果
     */
    @PostMapping
    public BaseResponse<TransactionVO> createTransaction(@Valid @RequestBody TransactionCreateDTO createDTO) {
        try {
            TransactionVO transaction = transactionService.createTransaction(createDTO);
            return ResultUtils.success(transaction);
        } catch (Exception e) {
            log.error("创建交易失败: {}", e.getMessage(), e);
            return ResultUtils.error("创建交易失败: " + e.getMessage());
        }
    }


    /**
     * 根据用户地址查询交易列表
     * @param userAddress 用户地址
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 交易列表
     */
    @GetMapping("/user/{userAddress}")
    public BaseResponse<Page<TransactionVO>> listTransactionsByUser(
            @PathVariable String userAddress,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<TransactionVO> transactions = transactionService.listTransactionsByUser(userAddress, pageNum, pageSize);
            return ResultUtils.success(transactions);
        } catch (Exception e) {
            log.error("查询用户交易列表失败: {}", e.getMessage(), e);
            return ResultUtils.error("查询用户交易列表失败: " + e.getMessage());
        }
    }


    /**
     * 获取用户交易过的代币地址列表
     * @param userAddress 用户地址
     * @return 代币地址列表
     */
    @GetMapping("/user/{userAddress}/traded-tokens")
    public BaseResponse<UserTradedTokenVO> getUserTradedTokens(@PathVariable String userAddress) {
        try {
            UserTradedTokenVO tradedTokens = transactionService.getUserTradedTokens(userAddress);
            return ResultUtils.success(tradedTokens);
        } catch (Exception e) {
            log.error("获取用户交易过的代币地址列表失败: {}", e.getMessage(), e);
            return ResultUtils.error("获取用户交易过的代币地址列表失败: " + e.getMessage());
        }
    }
} 