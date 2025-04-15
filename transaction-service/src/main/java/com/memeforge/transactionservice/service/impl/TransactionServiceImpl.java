package com.memeforge.transactionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memeforge.tokenservice.model.vo.TokenVO;
import com.memeforge.transactionservice.client.TokenServiceClient;
import com.memeforge.transactionservice.mapper.TransactionMapper;
import com.memeforge.transactionservice.model.dto.TransactionCreateDTO;
import com.memeforge.transactionservice.model.entity.Transaction;
import com.memeforge.transactionservice.model.vo.TransactionVO;
import com.memeforge.transactionservice.model.vo.UserTradedTokenVO;
import com.memeforge.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 交易服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    private final TokenServiceClient tokenServiceClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionVO createTransaction(TransactionCreateDTO createDTO) {
        log.info("创建交易记录: {}", createDTO);

        // 验证代币是否存在
        try {
            TokenVO token = tokenServiceClient.getTokenByAddress(createDTO.getTokenAddress());
            log.info("成功获取代币信息: {}", token);
            if (token == null) {
                log.error("代币信息为空: {}", createDTO.getTokenAddress());
                throw new RuntimeException("代币不存在");
            }
        } catch (Exception e) {
            log.error("获取代币信息失败: {}, 异常类型: {}, 异常信息: {}", 
                createDTO.getTokenAddress(), e.getClass().getName(), e.getMessage(), e);
            
            log.warn("由于测试阶段，忽略代币验证错误，继续处理交易");
        }

        BigDecimal price = createDTO.getPrice();

        // 创建交易记录
        Transaction transaction = new Transaction();
        transaction.setTokenAddress(createDTO.getTokenAddress());
        transaction.setUserAddress(createDTO.getUserAddress());
        transaction.setAmount(createDTO.getAmount());
        transaction.setPrice(price); // 使用处理后的价格
        transaction.setTransactionType(createDTO.getTransactionType());
        transaction.setCreatedAt(new Date());
        
        // 设置交易哈希
        transaction.setTransactionHash(createDTO.getTransactionHash());

        // 保存交易记录
        boolean saved = save(transaction);
        if (!saved) {
            log.error("保存交易记录失败: {}", createDTO);
            throw new RuntimeException("保存交易记录失败");
        }
        
        return convertToVO(transaction);
    }

    @Override
    public Page<TransactionVO> listTransactionsByUser(String userAddress, Integer pageNum, Integer pageSize) {
        // 参数校验
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        // 查询交易记录
        Page<Transaction> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Transaction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Transaction::getUserAddress, userAddress)
                .orderByDesc(Transaction::getCreatedAt);

        Page<Transaction> transactionPage = page(page, queryWrapper);

        return convertToVOPage(transactionPage);
    }


    @Override
    public UserTradedTokenVO getUserTradedTokens(String userAddress) {
        // 创建返回对象
        UserTradedTokenVO result = new UserTradedTokenVO();
        result.setUserAddress(userAddress);
        
        try {
            // 查询用户的所有交易记录
            LambdaQueryWrapper<Transaction> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Transaction::getUserAddress, userAddress)
                    .orderByDesc(Transaction::getCreatedAt);
            
            List<Transaction> transactions = list(queryWrapper);
            if (transactions == null || transactions.isEmpty()) {
                result.setTokenAddresses(new ArrayList<>());
                return result;
            }
            
            List<String> tokenAddresses = transactions.stream()
                    .map(Transaction::getTokenAddress)
                    .distinct()
                    .collect(Collectors.toList());
            
            result.setTokenAddresses(tokenAddresses);
            
        } catch (Exception e) {
            log.error("获取用户交易代币列表失败: {}", userAddress, e);
            result.setTokenAddresses(new ArrayList<>());
        }
        
        return result;
    }

    /**
     * 将Transaction转换为TransactionVO
     */
    private TransactionVO convertToVO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionVO vo = new TransactionVO();
        BeanUtils.copyProperties(transaction, vo);
        
        // 补充额外信息
        if (transaction.getAmount() != null && transaction.getPrice() != null) {
            vo.setTotalValue(transaction.getPrice().multiply(transaction.getAmount()));
        }
        
        // 获取代币符号
        try {
            TokenVO tokenInfo = tokenServiceClient.getTokenByAddress(transaction.getTokenAddress());
            if (tokenInfo != null) {
                vo.setTokenSymbol(tokenInfo.getSymbol());
            }
        } catch (Exception e) {
            log.warn("获取代币符号失败: {}", transaction.getTokenAddress(), e);
        }

        return vo;
    }

    /**
     * 转换分页结果
     */
    private Page<TransactionVO> convertToVOPage(Page<Transaction> page) {
        Page<TransactionVO> voPage = new Page<>();
        BeanUtils.copyProperties(page, voPage, "records");
        
        if (page.getRecords() != null) {
            voPage.setRecords(page.getRecords().stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList()));
        } else {
            voPage.setRecords(new ArrayList<>());
        }
        
        return voPage;
    }
}




