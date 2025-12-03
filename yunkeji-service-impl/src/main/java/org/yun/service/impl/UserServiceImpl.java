package org.yun.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yun.common.dto.CreateUserRequest;
import org.yun.common.dto.RechargeBalanceRequest;
import org.yun.common.dto.UpdateUserPriceRequest;
import org.yun.common.dto.UserResponse;
import org.yun.dao.entity.User;
import org.yun.dao.entity.UserBalanceRecord;
import org.yun.dao.mapper.UserBalanceRecordMapper;
import org.yun.dao.mapper.UserMapper;
import org.yun.service.UserService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserBalanceRecordMapper userBalanceRecordMapper;
    
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        // 默认普通用户，查询单价默认为500（可在后台调整）
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getQueryPrice() == null) {
            user.setQueryPrice(BigDecimal.valueOf(500));
        }
        if (user.getBalance() == null) {
            user.setBalance(BigDecimal.ZERO);
        }
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
    
    @Override
    public UserResponse updateUserPrice(Long id, UpdateUserPriceRequest request) {
        User user = userMapper.selectOneById(id);
        if (user == null) {
            return null;
        }
        BigDecimal price = request.getQueryPrice() != null ? request.getQueryPrice() : BigDecimal.ZERO;
        user.setQueryPrice(price);
        userMapper.update(user);
        
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
    
    @Override
    public UserResponse rechargeBalance(Long id, RechargeBalanceRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        User user = userMapper.selectOneById(id);
        if (user == null) {
            return null;
        }
        BigDecimal before = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal after = before.add(request.getAmount());
        user.setBalance(after);
        userMapper.update(user);
        
        UserBalanceRecord record = new UserBalanceRecord();
        record.setUserId(user.getId());
        record.setChangeAmount(request.getAmount());
        record.setChangeType("RECHARGE");
        record.setRemark(request.getRemark());
        record.setCreatedAt(new Date());
        userBalanceRecordMapper.insert(record);
        
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}