package org.yun.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yun.common.dto.LoginRequest;
import org.yun.common.dto.LoginResponse;
import org.yun.common.dto.RegisterRequest;
import org.yun.common.dto.UserResponse;
import org.yun.common.util.JwtUtil;
import org.yun.dao.entity.User;
import org.yun.dao.mapper.UserMapper;
import org.yun.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        
        // 简单的用户名密码验证（实际项目中应该使用加密存储的密码）
        User user = userMapper.selectOneByQuery(
            com.mybatisflex.core.query.QueryWrapper.create()
                .where(org.yun.dao.table.UserTableDef.USER.USERNAME.eq(request.getUsername()))
                .and(org.yun.dao.table.UserTableDef.USER.PASSWORD.eq(request.getPassword()))
        );
        
        if (user != null) {
            response.setSuccess(true);
            response.setMessage("登录成功");
            // 生成JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            response.setToken(token);
            
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);
            response.setUser(userResponse);
        } else {
            response.setSuccess(false);
            response.setMessage("用户名或密码错误");
        }
        
        return response;
    }
    
    @Override
    public LoginResponse register(RegisterRequest request) {
        LoginResponse response = new LoginResponse();
        
        // 检查用户名是否已存在
        User existingUser = userMapper.selectOneByQuery(
            com.mybatisflex.core.query.QueryWrapper.create()
                .where(org.yun.dao.table.UserTableDef.USER.USERNAME.eq(request.getUsername()))
        );
        
        if (existingUser != null) {
            response.setSuccess(false);
            response.setMessage("用户名已存在");
            return response;
        }
        
        // 创建新用户
        User user = new User();
        BeanUtils.copyProperties(request, user);
        userMapper.insert(user);
        
        response.setSuccess(true);
        response.setMessage("注册成功");
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        response.setToken(token);
        
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        response.setUser(userResponse);
        
        return response;
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            return username != null && !username.isEmpty() && !jwtUtil.isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}