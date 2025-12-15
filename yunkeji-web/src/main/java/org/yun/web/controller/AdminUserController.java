package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yun.common.dto.RechargeBalanceRequest;
import org.yun.common.dto.UpdateUserPriceRequest;
import org.yun.common.dto.UserResponse;
import org.yun.common.util.JwtUtil;
import org.yun.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "管理员-用户管理", description = "管理员管理用户价格与余额的API")
public class AdminUserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    @Operation(summary = "管理员获取所有用户")
    public List<UserResponse> getAllUsers(@RequestHeader("Authorization") String token) {
        ensureAdmin(token);
        return userService.getAllUsers();
    }
    
    @PutMapping("/{id}/price")
    @Operation(summary = "管理员更新用户查询单价")
    public UserResponse updateUserPrice(@RequestHeader("Authorization") String token,
                                        @PathVariable Long id,
                                        @RequestBody UpdateUserPriceRequest request) {
        ensureAdmin(token);
        UserResponse updated = userService.updateUserPrice(id, request);
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return updated;
    }
    
    @PutMapping("/{id}/recharge")
    @Operation(summary = "管理员为用户充值余额")
    public UserResponse rechargeBalance(@RequestHeader("Authorization") String token,
                                        @PathVariable Long id,
                                        @RequestBody RechargeBalanceRequest request) {
        ensureAdmin(token);
        UserResponse updated = userService.rechargeBalance(id, request);
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return updated;
    }
    
    private void ensureAdmin(String authorizationHeader) {
        String token = authorizationHeader;
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未授权访问");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String role = jwtUtil.extractRole(token);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "仅管理员可访问该接口");
        }
    }
}


















