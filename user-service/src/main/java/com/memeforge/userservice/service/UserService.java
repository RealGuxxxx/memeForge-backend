package com.memeforge.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.memeforge.userservice.model.entity.User;
import com.memeforge.userservice.model.dto.UpdateUserDTO;
import com.memeforge.userservice.model.dto.Web3LoginDTO;
import com.memeforge.userservice.model.vo.UserVO;

public interface UserService extends IService<User> {
    String getNonce(String walletAddress);
    UserVO web3Login(Web3LoginDTO loginDTO);
    UserVO getUserInfo(Long userId);
    UserVO updateUserInfo(Long userId, UpdateUserDTO updateUserDTO);
    UserVO getUserByWalletAddress(String walletAddress);
    Long getUserIdByWalletAddress(String walletAddress);
    void logout();
}
