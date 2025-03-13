package com.iscas.testoutline.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String token;
} 