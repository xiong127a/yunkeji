package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yun.common.dto.CreateUserRequest;
import org.yun.common.dto.UserResponse;
import org.yun.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关的API")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "创建用户")
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    
    @GetMapping
    @Operation(summary = "获取所有用户")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
    
    // 单价和充值操作仅允许管理员通过 AdminUserController 进行
}