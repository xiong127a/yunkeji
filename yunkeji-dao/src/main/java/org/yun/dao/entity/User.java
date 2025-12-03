package org.yun.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

@Table(value = "user")
public class User {
    
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private String username;
    
    private String email;
    
    private String password;
    
    private String role;
    
    private java.math.BigDecimal queryPrice;
    
    private java.math.BigDecimal balance;
    
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public java.math.BigDecimal getQueryPrice() {
        return queryPrice;
    }
    
    public void setQueryPrice(java.math.BigDecimal queryPrice) {
        this.queryPrice = queryPrice;
    }
    
    public java.math.BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(java.math.BigDecimal balance) {
        this.balance = balance;
    }
}