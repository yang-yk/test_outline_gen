package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.dto.UserLoginDTO;
import com.iscas.testoutline.dto.UserRegisterDTO;
import com.iscas.testoutline.dto.UserResponseDTO;
import com.iscas.testoutline.entity.User;
import com.iscas.testoutline.exception.EntityNotFoundException;
import com.iscas.testoutline.exception.InvalidPasswordException;
import com.iscas.testoutline.repository.UserRepository;
import com.iscas.testoutline.service.UserService;
import com.iscas.testoutline.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private static final String AVATAR_UPLOAD_DIR = "uploads/avatars";

    private UserResponseDTO createUserResponse(User user) {
        String token = jwtUtils.generateToken(user.getEmail());
        UserResponseDTO response = new UserResponseDTO();
        BeanUtils.copyProperties(user, response);
        response.setToken(token);
        return response;
    }

    @Override
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
    }

    @Override
    public User updateUserProfile(Long userId, User updatedUser) {
        User user = getUserProfile(userId);
        
        // 只更新允许修改的字段
        BeanUtils.copyProperties(updatedUser, user, "id", "password", "username", "createTime", "updateTime", "apiKeys");
        
        return userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserProfile(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("原密码不正确");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        User user = getUserProfile(userId);
        
        try {
            // 创建上传目录
            Path uploadDir = Paths.get(AVATAR_UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 生成文件名
            String filename = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
            Path filePath = uploadDir.resolve(filename);
            
            // 保存文件
            Files.copy(file.getInputStream(), filePath);
            
            // 更新用户头像路径
            String avatarUrl = "/avatars/" + filename;
            user.setAvatar(avatarUrl);
            userRepository.save(user);
            
            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("上传头像失败", e);
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }

    @Override
    @Transactional
    public UserResponseDTO register(UserRegisterDTO registerDTO) {
        System.out.println("🍎----🍎---🍎---🍎---registerDTO: " + registerDTO);
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("该邮箱已被注册");
        }

        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setAvatar(registerDTO.getAvatar());

        user = userRepository.save(user);
        return createUserResponse(user);
    }

    @Override
    public UserResponseDTO login(UserLoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return createUserResponse(user);
    }

    @Override
    public UserResponseDTO getCurrentUser(String email) {
        User user = getUserByEmail(email);
        return createUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(String email, UserRegisterDTO updateDTO) {
        User user = getUserByEmail(email);

        if (!email.equals(updateDTO.getEmail()) && userRepository.existsByEmail(updateDTO.getEmail())) {
            throw new RuntimeException("该邮箱已被注册");
        }

        user.setUsername(updateDTO.getUsername());
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        user.setEmail(updateDTO.getEmail());

        user = userRepository.save(user);
        return createUserResponse(user);
    }

    @Override
    @Transactional
    public void updateAvatar(String email, String avatarUrl) {
        User user = getUserByEmail(email);
        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
} 