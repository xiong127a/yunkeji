package org.yun.common.dto;

import java.math.BigDecimal;

public class DirectPayOrderResponse {
    
    private Long recordId;
    
    private String orderNo;
    
    private BigDecimal amount;
    
    private String qrContent;
    
    private String status;
    
    private String payChannel;
    
    public Long getRecordId() {
        return recordId;
    }
    
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
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
    
    public String getQrContent() {
        return qrContent;
    }
    
    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPayChannel() {
        return payChannel;
    }
    
    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }
}



