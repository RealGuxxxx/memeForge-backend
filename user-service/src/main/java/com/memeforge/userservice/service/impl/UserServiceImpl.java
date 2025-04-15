package com.memeforge.userservice.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memeforge.common.exception.BusinessException;
import com.memeforge.userservice.mapper.UserMapper;
import com.memeforge.userservice.model.dto.UpdateUserDTO;
import com.memeforge.userservice.model.dto.Web3LoginDTO;
import com.memeforge.userservice.model.entity.User;
import com.memeforge.userservice.model.vo.UserVO;
import com.memeforge.userservice.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final HttpSession session;
    private final Map<String, String> nonceMap = new HashMap<>();

    private final UserMapper userMapper;

    @Override
    public String getNonce(String walletAddress) {
        // 检查用户是否存在，不存在则创建
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getWalletAddress, walletAddress));
        
        if (user == null) {
            user = new User();
            user.setWalletAddress(walletAddress);
            user.setNickname("User_" + walletAddress.substring(0, 6));
            user.setAvatar("https://api.dicebear.com/7.x/pixel-art/svg?seed=" + walletAddress);
            user.setBio("这是一个新用户");
            user.setStatus(1);
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            save(user);
        }

        // 生成包含时间戳的消息
        String message = String.format("Welcome to MemeForge! Please sign this message to login.\nTimestamp: %d", System.currentTimeMillis());
        nonceMap.put(walletAddress, message);
        return message;
    }

    @Override
    public UserVO web3Login(Web3LoginDTO loginDTO) {
        try {
            String message = nonceMap.get(loginDTO.getWalletAddress());
            if (message == null) {
                throw new BusinessException(400, "Nonce not found");
            }

            // 验证签名
            if (!verifySignature(loginDTO.getWalletAddress(), message, loginDTO.getSignature())) {
                throw new BusinessException(400, "Invalid signature");
            }

            // 获取或创建用户
            User user = getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getWalletAddress, loginDTO.getWalletAddress()));
            
            if (user == null) {
                throw new BusinessException(404, "User not found");
            }

            // 更新登录时间
            user.setLastLoginTime(new Date());
            updateById(user);

            // 转换为VO并设置token
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);

            // 保存到session
            session.setAttribute("user", userVO);

            return userVO;
        } finally {
            nonceMap.remove(loginDTO.getWalletAddress());
        }
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        // 先从session获取
        UserVO sessionUser = (UserVO) session.getAttribute("user");
        if (sessionUser != null && sessionUser.getId().equals(userId)) {
            return sessionUser;
        }

        // session中没有，从数据库获取
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "User not found");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public UserVO updateUserInfo(Long userId, UpdateUserDTO updateDTO) {

        // 获取用户信息
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 验证昵称
        if (updateDTO.getNickname() != null) {
            if (updateDTO.getNickname().length() < 2 || updateDTO.getNickname().length() > 20) {
                throw new BusinessException(400, "昵称长度应在2-20个字符之间");
            }
            user.setNickname(updateDTO.getNickname());
        }

        // 验证头像
        if (updateDTO.getAvatar() != null) {
            if (!updateDTO.getAvatar().startsWith("http")) {
                throw new BusinessException(400, "头像URL格式不正确");
            }
            user.setAvatar(updateDTO.getAvatar());
        }

        // 验证简介
        if (updateDTO.getBio() != null) {
            if (updateDTO.getBio().length() > 200) {
                throw new BusinessException(400, "简介长度不能超过200个字符");
            }
            user.setBio(updateDTO.getBio());
        }

        user.setUpdatedAt(new Date());
        
        try {
            updateById(user);
            
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            
            try {
                if (session != null && session.getAttribute("user") != null) {
                    session.setAttribute("user", userVO);
                }
            } catch (Exception e) {
                log.warn("更新会话信息失败，但用户更新成功", e);
            }
            
            return userVO;
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            throw new BusinessException(500, "更新用户信息失败");
        }
    }

    @Override
    public UserVO getUserByWalletAddress(String walletAddress) {
        if (walletAddress == null || walletAddress.isEmpty()) {
            throw new BusinessException(400, "钱包地址不能为空");
        }

        UserVO sessionUser = (UserVO) session.getAttribute("user");
        if (sessionUser != null && sessionUser.getWalletAddress().equalsIgnoreCase(walletAddress)) {
            return sessionUser;
        }

        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getWalletAddress, walletAddress)
                .eq(User::getStatus, 1));  // 只获取状态正常的用户
                
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public void logout() {
        try {
            session.invalidate();
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("注销失败", e);
            throw new BusinessException(500, "注销失败");
        }
    }

    private boolean verifySignature(String address, String message, String signature) {
        try {
            log.info("Verifying signature for address: {}", address);
            log.info("Original message: {}", message);
            log.info("Signature: {}", signature);

            String cleanSignature = signature.startsWith("0x") ? signature.substring(2) : signature;
            
            String prefix = "\u0019Ethereum Signed Message:\n" + message.length();
            byte[] prefixBytes = prefix.getBytes(StandardCharsets.UTF_8);
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            
            byte[] fullMessage = new byte[prefixBytes.length + messageBytes.length];
            System.arraycopy(prefixBytes, 0, fullMessage, 0, prefixBytes.length);
            System.arraycopy(messageBytes, 0, fullMessage, prefixBytes.length, messageBytes.length);

            log.info("Full message (hex): {}", Numeric.toHexString(fullMessage));
            
            // 解析签名
            byte[] signatureBytes = Numeric.hexStringToByteArray(cleanSignature);
            if (signatureBytes.length != 65) {
                log.error("Invalid signature length: {}", signatureBytes.length);
                return false;
            }

            byte v = signatureBytes[64];
            if (v < 27) {
                v += 27;
            }

            Sign.SignatureData signatureData = new Sign.SignatureData(
                v,
                Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64)
            );

            // 恢复公钥并验证地址
            BigInteger publicKey = Sign.signedMessageToKey(fullMessage, signatureData);
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            log.info("Original address: {}", address);
            log.info("Recovered address: {}", recoveredAddress);
            log.info("Addresses match: {}", address.equalsIgnoreCase(recoveredAddress));

            return address.equalsIgnoreCase(recoveredAddress);
        } catch (SignatureException e) {
            log.error("Signature verification failed", e);
            log.error("Exception details: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during signature verification", e);
            log.error("Exception details: {}", e.getMessage());
            return false;
        }
    }

    @Override
public Long getUserIdByWalletAddress(String walletAddress) {
    if (StringUtils.isBlank(walletAddress)) {
        return null;
    }
    
    // 查询用户
    User user = userMapper.selectOne(
        new LambdaQueryWrapper<User>()
            .eq(User::getWalletAddress, walletAddress)
    );
    
    return user != null ? user.getId() : null;
}
}




