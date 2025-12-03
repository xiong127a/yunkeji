package org.yun.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayProperties {
    
    private String mchid;
    private String appid;
    private String apiV3Key;
    private String privateKeyPath;
    private String serialNo;
    private String notifyUrl;
    private String description;
    
    public String getMchid() {
        return mchid;
    }
    
    public void setMchid(String mchid) {
        this.mchid = mchid;
    }
    
    public String getAppid() {
        return appid;
    }
    
    public void setAppid(String appid) {
        this.appid = appid;
    }
    
    public String getApiV3Key() {
        return apiV3Key;
    }
    
    public void setApiV3Key(String apiV3Key) {
        this.apiV3Key = apiV3Key;
    }
    
    public String getPrivateKeyPath() {
        return privateKeyPath;
    }
    
    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }
    
    public String getSerialNo() {
        return serialNo;
    }
    
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
    
    public String getNotifyUrl() {
        return notifyUrl;
    }
    
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}




