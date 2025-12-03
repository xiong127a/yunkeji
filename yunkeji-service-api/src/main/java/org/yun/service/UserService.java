package org.yun.service;

import org.yun.common.dto.CreateUserRequest;
import org.yun.common.dto.RechargeBalanceRequest;
import org.yun.common.dto.UpdateUserPriceRequest;
import org.yun.common.dto.UserResponse;

import java.util.List;

public interface UserService {
    
    UserResponse createUser(CreateUserRequest request);
    
    UserResponse getUserById(Long id);
    
    List<UserResponse> getAllUsers();
    
    void deleteUser(Long id);
    
    UserResponse updateUserPrice(Long id, UpdateUserPriceRequest request);
    
    UserResponse rechargeBalance(Long id, RechargeBalanceRequest request);
}