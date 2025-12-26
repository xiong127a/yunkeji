package org.yun.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.math.BigDecimal;
import java.util.Date;

@Table(value = "pay_order")
public class PayOrder {
    
    @Id(keyType = KeyType.Auto)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long queryRecordId;
    
    private BigDecimal amount;
    
    private String payChannel;
    
    private String status;
    
    private String qrContent;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getQueryRecordId() {
        return queryRecordId;
    }
    
    public void setQueryRecordId(Long queryRecordId) {
        this.queryRecordId = queryRecordId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPayChannel() {
        return payChannel;
    }
    
    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getQrContent() {
        return qrContent;
    }
    
    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}





























