package org.yun.common.dto;

import java.math.BigDecimal;
import java.util.List;

public class RealEstateQueryRecordDTO {
    
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String idCard;
    
    private String requestNo;
    
    private String status;
    
    private BigDecimal queryFee;
    
    private String payMode;
    
    private String payStatus;
    
    private String result;
    
    private String createdAt;
    
    private List<RealEstateFileDTO> files;
    
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
    
    public BigDecimal getQueryFee() {
        return queryFee;
    }
    
    public void setQueryFee(BigDecimal queryFee) {
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<RealEstateFileDTO> getFiles() {
        return files;
    }
    
    public void setFiles(List<RealEstateFileDTO> files) {
        this.files = files;
    }
}