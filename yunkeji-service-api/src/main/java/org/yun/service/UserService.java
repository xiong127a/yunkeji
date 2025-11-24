package org.yun.service;

import org.yun.common.dto.CreateUserRequest;
import org.yun.common.dto.UserResponse;

import java.util.List;

public interface UserService {
    
    UserResponse createUser(CreateUserRequest request);
    
    UserResponse getUserById(Long id);
    
    List<UserResponse> getAllUsers();
    
    void deleteUser(Long id);
}