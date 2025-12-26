package org.yun.common.dto;

/**
 * 客服联系方式响应DTO
 */
public class ContactInfoResponse {
    
    /**
     * 客服微信
     */
    private String wechat;
    
    /**
     * 客服手机号
     */
    private String phone;
    
    /**
     * 客服工作时间（可选）
     */
    private String workHours;
    
    public ContactInfoResponse() {
    }
    
    public ContactInfoResponse(String wechat, String phone, String workHours) {
        this.wechat = wechat;
        this.phone = phone;
        this.workHours = workHours;
    }
    
    public String getWechat() {
        return wechat;
    }
    
    public void setWechat(String wechat) {
        this.wechat = wechat;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getWorkHours() {
        return workHours;
    }
    
    public void setWorkHours(String workHours) {
        this.workHours = workHours;
    }
}





















