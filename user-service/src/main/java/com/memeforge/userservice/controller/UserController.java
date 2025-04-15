package com.memeforge.userservice.controller;

import com.memeforge.common.response.BaseResponse;
import com.memeforge.common.response.ResultUtils;
import com.memeforge.userservice.model.dto.UpdateUserDTO;
import com.memeforge.userservice.model.dto.Web3LoginDTO;
import com.memeforge.userservice.model.vo.UserVO;
import com.memeforge.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/nonce")
    public String getNonce(@RequestParam String walletAddress) {
        return userService.getNonce(walletAddress);
    }

    @PostMapping("/web3-login")
    public UserVO web3Login(@Valid @RequestBody Web3LoginDTO loginDTO) {
        return userService.web3Login(loginDTO);
    }

    @GetMapping("/{userId}")
    public UserVO getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

    @PutMapping("/{userId}")
    public UserVO updateUserInfo(@PathVariable Long userId, @RequestBody UpdateUserDTO updateUserDTO) {
        // 打印详细的请求信息
        log.info("接收到更新用户请求 - 用户ID: {}, 请求体: {}", userId, updateUserDTO);
        
        try {
            UserVO result = userService.updateUserInfo(userId, updateUserDTO);
            log.info("用户更新成功: {}", result);
            return result;
        } catch (Exception e) {
            log.error("用户更新失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/wallet/{walletAddress}")
    public UserVO getUserByWalletAddress(@PathVariable String walletAddress) {
        return userService.getUserByWalletAddress(walletAddress);
    }

    @GetMapping("/wallet/{walletAddress}/id")
    public BaseResponse<Long> getUserIdByWalletAddress(@PathVariable String walletAddress) {
    try {
        Long userId = userService.getUserIdByWalletAddress(walletAddress);
        return ResultUtils.success(userId);
    } catch (Exception e) {
        log.error("根据钱包地址获取用户ID失败: {}", e.getMessage(), e);
            return ResultUtils.error("根据钱包地址获取用户ID失败: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public void logout() {
        userService.logout();
    }
    
    // 添加测试端点
    @GetMapping("/test")
    public BaseResponse<Map<String, Object>> testEndpoint() {
        log.info("测试端点被调用");
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("message", "API服务正常运行");
        result.put("timestamp", System.currentTimeMillis());
        return ResultUtils.success(result);
    }
    
}