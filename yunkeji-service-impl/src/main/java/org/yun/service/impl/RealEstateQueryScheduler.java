package org.yun.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.dao.entity.RealEstateQueryRecord;
import org.yun.dao.mapper.RealEstateQueryRecordMapper;

import java.util.*;

import static org.yun.dao.table.RealEstateQueryRecordTableDef.REAL_ESTATE_QUERY_RECORD;

/**
 * 大数据查询定时任务
 * 定时查询所有待查询的记录，避免线程阻塞
 */
@Component
public class RealEstateQueryScheduler {
    
    private static final Logger log = LoggerFactory.getLogger(RealEstateQueryScheduler.class);
    
    @Autowired
    private RealEstateQueryRecordMapper recordMapper;
    
    @Value("${real.estate.api.url:http://8.137.70.50:81}")
    private String apiUrl;
    
    @Value("${real.estate.access.key:accessKey}")
    private String accessKey;
    
    @Value("${real.estate.secret.key:secretKey}")
    private String secretKey;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * 定时查询所有待查询的记录（兜底方案）
     * 每10分钟执行一次，只查询那些超过30分钟还没完成的记录
     * 这样可以避免回调丢失的情况
     */
    @Scheduled(fixedDelay = 600000) // 10分钟 = 600000毫秒
    public void queryPendingRecords() {
        log.info("开始执行大数据查询定时任务（兜底方案）");
        
        try {
            // 计算30分钟前的时间
            Date thirtyMinutesAgo = new Date(System.currentTimeMillis() - 30 * 60 * 1000);
            
            // 查询所有待查询的记录（状态为 SUBMITTED 或 PROCESSING，且有 requestNo，且超过30分钟未更新）
            // 这样可以避免回调丢失的情况，定时任务作为兜底
            QueryWrapper queryWrapper = QueryWrapper.create()
                .where(REAL_ESTATE_QUERY_RECORD.REQUEST_NO.isNotNull())
                .and(REAL_ESTATE_QUERY_RECORD.REQUEST_NO.ne(""))
                .and(REAL_ESTATE_QUERY_RECORD.STATUS.in("SUBMITTED", "PROCESSING"))
                .and(REAL_ESTATE_QUERY_RECORD.UPDATED_AT.le(thirtyMinutesAgo)) // 超过30分钟未更新
                .orderBy(REAL_ESTATE_QUERY_RECORD.CREATED_AT.asc());
            
            List<RealEstateQueryRecord> records = recordMapper.selectListByQuery(queryWrapper);
            
            if (records.isEmpty()) {
                log.debug("没有待查询的记录");
                return;
            }
            
            log.info("找到 {} 条待查询记录，开始查询", records.size());
            
            int successCount = 0;
            int failCount = 0;
            
            for (RealEstateQueryRecord record : records) {
                try {
                    querySingleRecord(record);
                    successCount++;
                } catch (Exception e) {
                    log.error("查询记录失败，ID={}, requestNo={}", record.getId(), record.getRequestNo(), e);
                    failCount++;
                }
            }
            
            log.info("定时任务执行完成，成功: {}, 失败: {}", successCount, failCount);
            
        } catch (Exception e) {
            log.error("定时任务执行异常", e);
        }
    }
    
    /**
     * 查询单条记录
     */
    private void querySingleRecord(RealEstateQueryRecord record) {
        String requestNo = record.getRequestNo();
        if (requestNo == null || requestNo.isEmpty()) {
            log.warn("记录 ID={} 的 requestNo 为空，跳过", record.getId());
            return;
        }
        
        try {
            // 构建查询请求参数
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("reqNo", requestNo);
            
            // 添加公共参数
            addCommonParams(requestBody);
            
            // 生成签名（使用ApiSignUtil，符合文档要求）
            String signature = org.yun.common.util.ApiSignUtil.sign(requestBody, secretKey);
            requestBody.put("signature", signature);
            
            // 发送请求
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            
            org.springframework.http.HttpEntity<Map<String, Object>> entity = 
                new org.springframework.http.HttpEntity<>(requestBody, headers);
            String url = apiUrl + "/api/open/queryRealProperty";
            
            RealEstateQueryResponse response = restTemplate.postForObject(url, entity, RealEstateQueryResponse.class);
            
            if (response == null) {
                log.warn("查询结果为空，requestNo={}", requestNo);
                return;
            }
            
            // 处理查询结果
            if (response.getSuccess() && response.getResult() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = response.getResult() instanceof Map 
                    ? (Map<String, Object>) response.getResult() 
                    : null;
                
                if (result == null) {
                    log.warn("查询结果格式不正确，requestNo={}", requestNo);
                    return;
                }
                
                // 更新记录状态
                String approvalStatus = extractString(result, "approvalStatus");
                if ("APPROVED".equals(approvalStatus)) {
                    // 检查 authResults 中的 authState
                    List<Map<String, Object>> authResults = extractList(result, "authResults");
                    if (authResults != null && !authResults.isEmpty()) {
                        Map<String, Object> firstResult = authResults.get(0);
                        String authState = extractString(firstResult, "authState");
                        if ("30".equals(authState)) {
                            // 核查成功
                            record.setStatus("COMPLETED");
                            // 保存完整结果到 result 字段
                            try {
                                record.setResult(objectMapper.writeValueAsString(result));
                            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                                log.error("序列化结果失败", e);
                            }
                        } else if ("40".equals(authState)) {
                            // 核查异常
                            record.setStatus("FAILED");
                        } else {
                            // 审核中或核查中
                            record.setStatus("PROCESSING");
                        }
                    } else {
                        record.setStatus("PROCESSING");
                    }
                } else if ("UN_APPROVED".equals(approvalStatus)) {
                    // 审核不通过
                    record.setStatus("REJECTED");
                    String rejectReason = extractString(result, "rejectReason");
                    if (rejectReason != null) {
                        record.setResult("{\"rejectReason\":\"" + rejectReason + "\"}");
                    }
                } else if ("IN_APPROVAL".equals(approvalStatus)) {
                    // 待审核
                    record.setStatus("PROCESSING");
                } else {
                    record.setStatus("PROCESSING");
                }
            } else {
                log.warn("查询失败，requestNo={}, message={}", requestNo, response.getMessage());
                // 保持原状态，下次继续查询
            }
            
            record.setUpdatedAt(new Date());
            recordMapper.update(record);
            
            log.info("成功更新查询记录，ID={}, requestNo={}, status={}", 
                record.getId(), requestNo, record.getStatus());
            
        } catch (Exception e) {
            log.error("查询记录异常，ID={}, requestNo={}", record.getId(), requestNo, e);
            throw e;
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
    
    
    private String extractString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? String.valueOf(value) : null;
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractList(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof List) {
            return (List<Map<String, Object>>) value;
        }
        return null;
    }
}

