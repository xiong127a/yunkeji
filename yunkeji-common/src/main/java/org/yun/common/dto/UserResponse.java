package org.yun.common.dto;

import java.math.BigDecimal;

public class UserResponse {
    
    private Long id;
    
    private String username;
    
    private String email;
    
    private String role;

    /** 上级用户ID（多级分销用） */
    private Long parentId;

    /** 层级深度：1=顶级，2=一级代理，3=二级代理 */
    private Integer depth;

    /** 状态：ACTIVE / DISABLED */
    private String status;

    /** 实名/KYC状态：UNSUBMITTED / PENDING / APPROVED / REJECTED */
    private String kycStatus;

    private String idCardFrontUrl;
    private String idCardBackUrl;
    private String businessLicenseUrl;

    /** 是否信任用户：true=信任，false/null=默认 */
    private Boolean trusted;
    
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public String getIdCardFrontUrl() {
        return idCardFrontUrl;
    }

    public void setIdCardFrontUrl(String idCardFrontUrl) {
        this.idCardFrontUrl = idCardFrontUrl;
    }

    public String getIdCardBackUrl() {
        return idCardBackUrl;
    }

    public void setIdCardBackUrl(String idCardBackUrl) {
        this.idCardBackUrl = idCardBackUrl;
    }

    public String getBusinessLicenseUrl() {
        return businessLicenseUrl;
    }

    public void setBusinessLicenseUrl(String businessLicenseUrl) {
        this.businessLicenseUrl = businessLicenseUrl;
    }
    
    public Boolean getTrusted() {
        return trusted;
    }

    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
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