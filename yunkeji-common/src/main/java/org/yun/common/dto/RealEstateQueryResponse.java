package org.yun.common.dto;

import java.util.List;

public class RealEstateQueryResponse {
    
    private Boolean success;
    
    private Boolean fail;
    
    private String message;
    
    private Integer code;
    
    private Object result;
    
    private Long timestamp;
    
    // Getters and setters
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public Boolean getFail() {
        return fail;
    }
    
    public void setFail(Boolean fail) {
        this.fail = fail;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}