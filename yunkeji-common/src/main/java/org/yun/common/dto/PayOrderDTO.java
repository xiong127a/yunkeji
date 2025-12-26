package org.yun.common.dto;

import java.math.BigDecimal;

public class PayOrderDTO {
    
    private Long id;
    private String orderNo;
    private BigDecimal amount;
    private String payChannel;
    private String status;
    private Long recordId;
    private String recordName;
    private String createdAt;
    private String updatedAt;
    
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
    
    public Long getRecordId() {
        return recordId;
    }
    
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
    
    public String getRecordName() {
        return recordName;
    }
    
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

























