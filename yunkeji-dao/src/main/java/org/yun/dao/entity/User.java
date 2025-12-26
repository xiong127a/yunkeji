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

    /** 上级用户ID（支持多级分销），顶级为空 */
    private Long parentId;

    /** 层级深度：1=顶级，2=一级代理，3=二级代理（末级） */
    private Integer depth;

    /** 状态：ACTIVE / DISABLED */
    private String status;

    /** 实名/KYC状态：UNSUBMITTED / PENDING / APPROVED / REJECTED */
    private String kycStatus;

    /** 是否信任用户：1=信任，0=默认 */
    private Boolean trusted;

    /** 证件图片地址（可选） */
    private String idCardFrontUrl;
    private String idCardBackUrl;
    private String businessLicenseUrl;

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

    public Boolean getTrusted() {
        return trusted;
    }

    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
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