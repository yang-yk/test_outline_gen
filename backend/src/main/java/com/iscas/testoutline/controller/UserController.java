package com.iscas.testoutline.controller;

import com.iscas.testoutline.dto.UserLoginDTO;
import com.iscas.testoutline.dto.UserRegisterDTO;
import com.iscas.testoutline.dto.UserResponseDTO;
import com.iscas.testoutline.entity.User;
import com.iscas.testoutline.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        return ResponseEntity.ok(userService.register(registerDTO));
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        return ResponseEntity.ok(userService.login(loginDTO));
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户信息")
    public ResponseEntity<UserResponseDTO> updateUser(
            Authentication authentication,
            @Valid @RequestBody UserRegisterDTO updateDTO
    ) {
        return ResponseEntity.ok(userService.updateUser(authentication.getName(), updateDTO));
    }

    @GetMapping("/profile")
    @Operation(summary = "获取用户个人信息")
    public ResponseEntity<User> getUserProfile(@AuthenticationPrincipal User user) {
        System.out.println("用户角色: " + user.getRole());
        return ResponseEntity.ok(userService.getUserProfile(user.getId()));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户个人信息")
    public ResponseEntity<User> updateUserProfile(
            @AuthenticationPrincipal User user,
            @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUserProfile(user.getId(), updatedUser));
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal User user,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(user.getId(), oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/avatar")
    @Operation(summary = "上传头像")
    public ResponseEntity<String> uploadAvatar(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(user.getId(), file);
        return ResponseEntity.ok(avatarUrl);
    }
} 