package org.yun.service.impl;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.common.dto.RealEstateResultQueryRequest;
import org.yun.service.RealEstateService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class RealEstateServiceImpl implements RealEstateService {
    
    @Value("${real.estate.api.url:http://8.137.70.50:81}")
    private String apiUrl;
    
    @Value("${real.estate.access.key:accessKey}")
    private String accessKey;
    
    @Value("${real.estate.secret.key:secretKey}")
    private String secretKey;
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public RealEstateQueryResponse submitRealEstateQuery(RealEstateQueryRequest request) {
        try {
            // 构建请求参数
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("name", request.getName());
            requestBody.put("idCard", request.getIdCard());
            requestBody.put("callBackUrl", request.getCallBackUrl());
            requestBody.put("files", request.getFiles());
            
            // 添加公共参数
            addCommonParams(requestBody);
            
            // 生成签名
            String signature = generateSignature(requestBody);
            requestBody.put("signature", signature);
            
            // 发送请求
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String url = apiUrl + "/api/open/applyRealProperty";
            
            RealEstateQueryResponse response = restTemplate.postForObject(url, entity, RealEstateQueryResponse.class);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("提交不动产查询请求失败", e);
        }
    }
    
    @Override
    public RealEstateQueryResponse queryRealEstateResult(RealEstateResultQueryRequest request) {
        try {
            // 构建请求参数
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("reqNo", request.getReqNo());
            
            // 添加公共参数
            addCommonParams(requestBody);
            
            // 生成签名
            String signature = generateSignature(requestBody);
            requestBody.put("signature", signature);
            
            // 发送请求
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String url = apiUrl + "/api/open/queryRealProperty";
            
            RealEstateQueryResponse response = restTemplate.postForObject(url, entity, RealEstateQueryResponse.class);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("查询不动产结果失败", e);
        }
    }
    
    /**
     * 添加公共参数
     */
    private void addCommonParams(Map<String, Object> params) {
        params.put("accessKey", accessKey);
        params.put("requestNo", UUID.randomUUID().toString().replace("-", ""));
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
    }
    
    /**
     * 生成签名
     */
    private String generateSignature(Map<String, Object> params) {
        // 按字母顺序排序参数
        StringBuilder sb = new StringBuilder();
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if ("signature".equals(entry.getKey())) {
                        return; // signature字段不参与签名
                    }
                    sb.append(entry.getKey()).append(entry.getValue());
                });
        
        // 添加secretKey
        sb.append(secretKey);
        
        // 生成SHA256签名
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        return digester.digestHex(sb.toString());
    }
}