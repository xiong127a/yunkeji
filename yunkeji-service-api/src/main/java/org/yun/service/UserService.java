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

    /**
     * 管理员更新用户状态（审批/启用/禁用）
     */
    UserResponse updateUserStatus(Long userId, String status);

    /**
     * 管理员更新用户信任标记
     */
    UserResponse updateUserTrust(Long userId, Boolean trusted);
    
    UserResponse updateUserPrice(Long id, UpdateUserPriceRequest request);
    
    UserResponse rechargeBalance(Long id, RechargeBalanceRequest request);

    /**
     * 父级为子级分配余额（先扣父级，再加子级）
     */
    UserResponse transferToChild(Long parentId, Long childId, RechargeBalanceRequest request);
}