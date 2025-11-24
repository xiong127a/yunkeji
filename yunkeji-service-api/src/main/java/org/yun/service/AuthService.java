package org.yun.service;

import org.yun.common.dto.LoginRequest;
import org.yun.common.dto.LoginResponse;
import org.yun.common.dto.RegisterRequest;

public interface AuthService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 用户注册
     */
    LoginResponse register(RegisterRequest request);
    
    /**
     * 验证JWT token
     */
    boolean validateToken(String token);
}