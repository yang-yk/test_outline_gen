package com.iscas.testoutline.service;

import com.iscas.testoutline.dto.UserLoginDTO;
import com.iscas.testoutline.dto.UserRegisterDTO;
import com.iscas.testoutline.dto.UserResponseDTO;
import com.iscas.testoutline.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponseDTO register(UserRegisterDTO registerDTO);
    UserResponseDTO login(UserLoginDTO loginDTO);
    UserResponseDTO getCurrentUser(String email);
    UserResponseDTO updateUser(String email, UserRegisterDTO updateDTO);
    void updateAvatar(String email, String avatarUrl);
    User getUserByEmail(String email);

    /**
     * 获取用户信息
     */
    User getUserProfile(Long userId);

    /**
     * 更新用户信息
     */
    User updateUserProfile(Long userId, User user);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 上传头像
     */
    String uploadAvatar(Long userId, MultipartFile file);
} 