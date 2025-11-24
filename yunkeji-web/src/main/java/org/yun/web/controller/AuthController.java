package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yun.common.dto.LoginRequest;
import org.yun.common.dto.LoginResponse;
import org.yun.common.dto.RegisterRequest;
import org.yun.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户认证相关API")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
    
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}