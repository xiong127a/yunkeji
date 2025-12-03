package org.yun.common.dto;

import java.math.BigDecimal;

public class UserResponse {
    
    private Long id;
    
    private String username;
    
    private String email;
    
    private String role;
    
    private BigDecimal queryPrice;
    
    private BigDecimal balance;
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public BigDecimal getQueryPrice() {
        return queryPrice;
    }
    
    public void setQueryPrice(BigDecimal queryPrice) {
        this.queryPrice = queryPrice;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}