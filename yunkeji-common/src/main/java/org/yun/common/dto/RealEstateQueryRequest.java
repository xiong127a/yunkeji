package org.yun.common.dto;

import java.util.List;

public class RealEstateQueryRequest {
    
    private String name;
    
    private String idCard;
    
    private String callBackUrl;
    
    private List<FileInfo> files;
    
    // Getters and setters
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
    
    public String getCallBackUrl() {
        return callBackUrl;
    }
    
    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }
    
    public List<FileInfo> getFiles() {
        return files;
    }
    
    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }
    
    public static class FileInfo {
        private String type;
        private String fileName;
        private String fileBase64;
        
        // Getters and setters
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        public String getFileBase64() {
            return fileBase64;
        }
        
        public void setFileBase64(String fileBase64) {
            this.fileBase64 = fileBase64;
        }
    }
}