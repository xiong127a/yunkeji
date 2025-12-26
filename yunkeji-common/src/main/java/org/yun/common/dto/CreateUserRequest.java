package org.yun.common.dto;

public class CreateUserRequest {
    
    private String username;
    
    private String email;
    
    private String password;

    /**
     * 上级用户ID（可选）。
     * - 为空：表示顶级用户（例如你自己或平台创建的一级代理）
     * - 不为空：表示该用户的直属上级，用于多级分销
     */
    private Long parentId;
    
    // Getters and setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}