package org.yun.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

@Table(value = "real_estate_query_record")
public class RealEstateQueryRecord {
    
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String idCard;
    
    private String requestNo;
    
    private String status;
    
    private java.math.BigDecimal queryFee;
    
    private String payMode;
    
    private String payStatus;
    
    private String result;
    
    private java.util.Date createdAt;
    
    private java.util.Date updatedAt;
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getIdCard() {
        return idCard;
    }
    
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    
    public String getRequestNo() {
        return requestNo;
    }
    
    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public java.math.BigDecimal getQueryFee() {
        return queryFee;
    }
    
    public void setQueryFee(java.math.BigDecimal queryFee) {
        this.queryFee = queryFee;
    }
    
    public String getPayMode() {
        return payMode;
    }
    
    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }
    
    public String getPayStatus() {
        return payStatus;
    }
    
    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public java.util.Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public java.util.Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(java.util.Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}