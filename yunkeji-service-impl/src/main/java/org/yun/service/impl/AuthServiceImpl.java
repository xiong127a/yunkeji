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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        
        // 先根据用户名查找用户
        User user = userMapper.selectOneByQuery(
            com.mybatisflex.core.query.QueryWrapper.create()
                .where(org.yun.dao.table.UserTableDef.USER.USERNAME.eq(request.getUsername()))
        );
        
        // 验证用户是否存在以及密码是否匹配
        if (user != null && user.getPassword().equals(hashPassword(request.getPassword()))) {
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
        
        // 检查邮箱是否已存在
        User existingEmailUser = userMapper.selectOneByQuery(
            com.mybatisflex.core.query.QueryWrapper.create()
                .where(org.yun.dao.table.UserTableDef.USER.EMAIL.eq(request.getEmail()))
        );
        
        if (existingEmailUser != null) {
            response.setSuccess(false);
            response.setMessage("邮箱已被注册");
            return response;
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // 密码加密存储
        user.setPassword(hashPassword(request.getPassword()));
        
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
    
    /**
     * 使用MD5对密码进行简单哈希处理
     * 注意：实际生产环境中应该使用更安全的加密方式如BCrypt
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(no.toString(16));
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }
            return hashText.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}