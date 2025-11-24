package org.yun.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yun.common.dto.CreateUserRequest;
import org.yun.common.dto.UserResponse;
import org.yun.dao.entity.User;
import org.yun.dao.mapper.UserMapper;
import org.yun.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        userMapper.insert(user);
        
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
    
    @Override
    public UserResponse getUserById(Long id) {
        User user = userMapper.selectOneById(id);
        if (user == null) {
            return null;
        }
        
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
    
    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userMapper.selectAll();
        return users.stream().map(user -> {
            UserResponse response = new UserResponse();
            BeanUtils.copyProperties(user, response);
            return response;
        }).collect(Collectors.toList());
    }
    
    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
}