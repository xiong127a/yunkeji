package org.yun.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.common.dto.RealEstateResultQueryRequest;
import org.yun.service.RealEstateService;

import java.util.*;

@Service
public class RealEstateServiceImpl implements RealEstateService {
    
    private static final Logger log = LoggerFactory.getLogger(RealEstateServiceImpl.class);
    
    @Value("${real.estate.api.url:http://8.137.70.50:81}")
    private String apiUrl;
    
    @Value("${real.estate.access.key:accessKey}")
    private String accessKey;
    
    @Value("${real.estate.secret.key:secretKey}")
    private String secretKey;
    
    @Override
    public RealEstateQueryResponse submitRealEstateQuery(RealEstateQueryRequest request) {
        try {
            // 构建请求参数
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("name", request.getName());
            requestBody.put("idCard", request.getIdCard());
            String callBackUrl = request.getCallBackUrl();
            if (callBackUrl != null && !callBackUrl.isEmpty()) {
                requestBody.put("callBackUrl", callBackUrl);
                log.info("提交查询请求，回调地址: {}", callBackUrl);
            } else {
                log.warn("回调地址为空，未设置到请求参数中");
            }
            requestBody.put("files", request.getFiles());
            
            // 添加公共参数
            addCommonParams(requestBody);
            
            // 生成签名（使用ApiSignUtil，符合文档要求）
            String signature = org.yun.common.util.ApiSignUtil.sign(requestBody, secretKey);
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
            
            // 生成签名（使用ApiSignUtil，符合文档要求）
            String signature = org.yun.common.util.ApiSignUtil.sign(requestBody, secretKey);
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
    
}