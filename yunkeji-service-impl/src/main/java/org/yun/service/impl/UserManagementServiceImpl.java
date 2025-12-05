package org.yun.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.DirectPayOrderResponse;
import org.yun.common.dto.PayOrderDTO;
import org.yun.common.dto.RealEstateFileDTO;
import org.yun.common.dto.RealEstateQueryRecordDTO;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.dao.entity.PayOrder;
import org.yun.dao.entity.RealEstateFile;
import org.yun.dao.entity.RealEstateQueryRecord;
import org.yun.dao.entity.User;
import org.yun.dao.entity.UserBalanceRecord;
import org.yun.service.exception.BalanceInsufficientException;
import org.yun.dao.mapper.PayOrderMapper;
import org.yun.dao.mapper.RealEstateFileMapper;
import org.yun.dao.mapper.RealEstateQueryRecordMapper;
import org.yun.dao.mapper.UserBalanceRecordMapper;
import org.yun.dao.mapper.UserMapper;
import org.yun.service.RealEstateService;
import org.yun.service.UserManagementService;
import org.yun.service.pay.AlipayPayService;
import org.yun.service.pay.WechatPayService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.yun.dao.table.RealEstateFileTableDef.REAL_ESTATE_FILE;
import static org.yun.dao.table.RealEstateQueryRecordTableDef.REAL_ESTATE_QUERY_RECORD;
import static org.yun.dao.table.PayOrderTableDef.PAY_ORDER;

@Service
public class UserManagementServiceImpl implements UserManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(UserManagementServiceImpl.class);
    
    @Autowired
    private RealEstateQueryRecordMapper recordMapper;
    
    @Autowired
    private RealEstateFileMapper fileMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserBalanceRecordMapper userBalanceRecordMapper;
    
    @Autowired
    private PayOrderMapper payOrderMapper;
    
    @Autowired
    private RealEstateService realEstateService;
    
    @Autowired(required = false)
    private WechatPayService wechatPayService;
    
    @Autowired(required = false)
    private AlipayPayService alipayPayService;
    
    @Value("${real.estate.callback.url:}")
    private String callbackUrl;
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public RealEstateQueryRecordDTO submitRealEstateQuery(Long userId, RealEstateQueryRequest request) {
        // 计算并扣减用户余额
        BigDecimal fee = handleUserBalance(userId);
        
        // 保存查询记录
        RealEstateQueryRecord record = new RealEstateQueryRecord();
        record.setUserId(userId);
        record.setName(request.getName());
        record.setIdCard(request.getIdCard());
        record.setStatus("SUBMITTED");
        record.setPayMode("STORED_VALUE");
        record.setPayStatus("PAID");
        record.setQueryFee(fee);
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());
        recordMapper.insert(record);
        
        // 设置回调地址（如果配置了的话）
        if (callbackUrl != null && !callbackUrl.isEmpty()) {
            request.setCallBackUrl(callbackUrl);
            log.info("设置回调地址: {}", callbackUrl);
        } else {
            log.warn("回调地址未配置，第三方系统将无法主动回调结果");
        }
        
        // 调用不动产查询服务
        RealEstateQueryResponse response = realEstateService.submitRealEstateQuery(request);
        
        // 更新记录中的请求编号
        if (response.getSuccess() && response.getResult() != null) {
            record.setRequestNo(response.getResult().toString());
            recordMapper.update(record);
        }
        
        // 转换为DTO
        RealEstateQueryRecordDTO dto = new RealEstateQueryRecordDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
    
    @Override
    public RealEstateQueryRecordDTO submitRealEstateQueryWithFiles(Long userId, RealEstateQueryRequest request, MultipartFile[] files) {
        // 计算并扣减用户余额
        BigDecimal fee = handleUserBalance(userId);
        
        // 保存查询记录
        RealEstateQueryRecord record = new RealEstateQueryRecord();
        record.setUserId(userId);
        record.setName(request.getName());
        record.setIdCard(request.getIdCard());
        record.setStatus("SUBMITTED");
        record.setPayMode("STORED_VALUE");
        record.setPayStatus("PAID");
        record.setQueryFee(fee);
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());
        recordMapper.insert(record);
        
        // 处理上传的文件
        if (files != null && files.length > 0) {
            // 获取实际上传路径（项目平级目录下的upload）
            // 创建上传目录
            String realEstatePath = org.yun.common.util.FileUploadUtil.ensureUploadDir("real-estate");
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        // 保存文件
                        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(realEstatePath, fileName);
                        Files.write(filePath, file.getBytes());
                        
                        // 保存文件记录到数据库
                        RealEstateFile realEstateFile = new RealEstateFile();
                        realEstateFile.setQueryRecordId(record.getId());
                        realEstateFile.setFileName(file.getOriginalFilename());
                        realEstateFile.setFilePath(filePath.toString());
                        realEstateFile.setFileSize(file.getSize());
                        realEstateFile.setCreatedAt(new Date());
                        // 简单根据文件名判断文件类型
                        String originalName = file.getOriginalFilename();
                        if (originalName != null) {
                            if (originalName.toLowerCase().endsWith(".pdf")) {
                                realEstateFile.setFileType("PDF");
                            } else if (originalName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                                realEstateFile.setFileType("IMAGE");
                            } else {
                                realEstateFile.setFileType("OTHER");
                            }
                        }
                        fileMapper.insert(realEstateFile);
                    } catch (IOException e) {
                        throw new RuntimeException("文件保存失败: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }
        
        // 设置回调地址（如果配置了的话）
        if (callbackUrl != null && !callbackUrl.isEmpty()) {
            request.setCallBackUrl(callbackUrl);
            log.info("设置回调地址: {}", callbackUrl);
        } else {
            log.warn("回调地址未配置，第三方系统将无法主动回调结果");
        }
        
        // 调用不动产查询服务
        RealEstateQueryResponse response = realEstateService.submitRealEstateQuery(request);
        
        // 更新记录中的请求编号
        if (response.getSuccess() && response.getResult() != null) {
            record.setRequestNo(response.getResult().toString());
            recordMapper.update(record);
        }
        
        // 转换为DTO
        RealEstateQueryRecordDTO dto = new RealEstateQueryRecordDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
    
    @Override
    public RealEstateQueryResponse queryRealEstateResult(String requestNo) {
        // 查询不动产结果
        org.yun.common.dto.RealEstateResultQueryRequest request = 
            new org.yun.common.dto.RealEstateResultQueryRequest();
        request.setReqNo(requestNo);
        return realEstateService.queryRealEstateResult(request);
    }
    
    @Override
    public List<RealEstateQueryRecordDTO> getUserQueryRecords(Long userId) {
        List<RealEstateQueryRecord> records = recordMapper.selectListByQuery(
            QueryWrapper.create()
                .where(REAL_ESTATE_QUERY_RECORD.USER_ID.eq(userId))
                .orderBy(REAL_ESTATE_QUERY_RECORD.CREATED_AT.desc())
        );
        
        return records.stream()
                .map(record -> convertToDTO(record, true))
                .collect(Collectors.toList());
    }
    
    @Override
    public RealEstateQueryRecordDTO getQueryRecordDetail(Long recordId) {
        RealEstateQueryRecord record = recordMapper.selectOneById(recordId);
        if (record == null) {
            return null;
        }
        
        return convertToDTO(record, true);
    }
    
    @Override
    public List<RealEstateFileDTO> getQueryRecordFiles(Long recordId) {
        List<RealEstateFile> files = fileMapper.selectListByQuery(
            QueryWrapper.create()
                .where(REAL_ESTATE_FILE.QUERY_RECORD_ID.eq(recordId))
        );
        
        return files.stream().map(file -> {
            RealEstateFileDTO dto = new RealEstateFileDTO();
            BeanUtils.copyProperties(file, dto);
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public List<RealEstateQueryRecordDTO> getAllQueryRecords() {
        List<RealEstateQueryRecord> records = recordMapper.selectListByQuery(
                QueryWrapper.create()
                        .orderBy(REAL_ESTATE_QUERY_RECORD.CREATED_AT.desc())
        );
        return records.stream()
                .map(record -> convertToDTO(record, true))
                .collect(Collectors.toList());
    }
    
    @Override
    public RealEstateQueryRecordDTO updateQueryFee(Long recordId, BigDecimal queryFee) {
        RealEstateQueryRecord record = recordMapper.selectOneById(recordId);
        if (record == null) {
            return null;
        }
        record.setQueryFee(queryFee != null ? queryFee : BigDecimal.ZERO);
        record.setUpdatedAt(new Date());
        recordMapper.update(record);
        if ("DIRECT_PAY".equalsIgnoreCase(record.getPayMode())
                && !"PAID".equalsIgnoreCase(record.getPayStatus())) {
            List<PayOrder> pendingOrders = payOrderMapper.selectListByQuery(
                    QueryWrapper.create()
                            .where(PAY_ORDER.QUERY_RECORD_ID.eq(recordId))
                            .and(PAY_ORDER.STATUS.eq("UNPAID"))
            );
            for (PayOrder order : pendingOrders) {
                order.setStatus("CLOSED");
                order.setUpdatedAt(new Date());
                payOrderMapper.update(order);
            }
        }
        return convertToDTO(record, true);
    }
    
    @Override
    public DirectPayOrderResponse createDirectPayQuery(Long userId, RealEstateQueryRequest request, String payChannel) {
        User user = userMapper.selectOneById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        BigDecimal price = user.getQueryPrice() != null ? user.getQueryPrice() : BigDecimal.ZERO;
        String channel = normalizePayChannel(payChannel);
        
        RealEstateQueryRecord record = new RealEstateQueryRecord();
        record.setUserId(userId);
        record.setName(request.getName());
        record.setIdCard(request.getIdCard());
        record.setStatus("PENDING_PAY");
        record.setPayMode("DIRECT_PAY");
        record.setPayStatus("UNPAID");
        record.setQueryFee(price);
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());
        recordMapper.insert(record);
        
        return createPayOrderForRecord(user, record, channel);
    }
    
    @Override
    public DirectPayOrderResponse createDirectPayQueryWithFiles(Long userId, RealEstateQueryRequest request, MultipartFile[] files, String payChannel) {
        // 先创建查询记录（未支付）
        User user = userMapper.selectOneById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        BigDecimal price = user.getQueryPrice() != null ? user.getQueryPrice() : BigDecimal.ZERO;
        String channel = normalizePayChannel(payChannel);
        
        RealEstateQueryRecord record = new RealEstateQueryRecord();
        record.setUserId(userId);
        record.setName(request.getName());
        record.setIdCard(request.getIdCard());
        record.setStatus("PENDING_PAY");
        record.setPayMode("DIRECT_PAY");
        record.setPayStatus("UNPAID");
        record.setQueryFee(price);
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());
        recordMapper.insert(record);
        
        // 保存文件（与储值模式相同）
        if (files != null && files.length > 0) {
            // 获取实际上传路径（项目平级目录下的upload）
            String realEstatePath = org.yun.common.util.FileUploadUtil.ensureUploadDir("real-estate");
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(realEstatePath, fileName);
                        Files.write(filePath, file.getBytes());
                        
                        RealEstateFile realEstateFile = new RealEstateFile();
                        realEstateFile.setQueryRecordId(record.getId());
                        realEstateFile.setFileName(file.getOriginalFilename());
                        realEstateFile.setFilePath(filePath.toString());
                        realEstateFile.setFileSize(file.getSize());
                        realEstateFile.setCreatedAt(new Date());
                        String originalName = file.getOriginalFilename();
                        if (originalName != null) {
                            if (originalName.toLowerCase().endsWith(".pdf")) {
                                realEstateFile.setFileType("PDF");
                            } else if (originalName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                                realEstateFile.setFileType("IMAGE");
                            } else {
                                realEstateFile.setFileType("OTHER");
                            }
                        }
                        fileMapper.insert(realEstateFile);
                    } catch (IOException e) {
                        throw new RuntimeException("文件保存失败: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }
        
        return createPayOrderForRecord(user, record, channel);
    }
    
    @Override
    public List<PayOrderDTO> getPendingPayOrders(Long userId) {
        List<PayOrder> orders = payOrderMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(PAY_ORDER.USER_ID.eq(userId))
                        .and(PAY_ORDER.STATUS.eq("UNPAID"))
                        .orderBy(PAY_ORDER.CREATED_AT.desc())
        );
        return orders.stream().map(order -> {
            PayOrderDTO dto = new PayOrderDTO();
            dto.setId(order.getId());
            dto.setOrderNo(order.getOrderNo());
            dto.setAmount(order.getAmount());
            dto.setPayChannel(order.getPayChannel());
            dto.setStatus(order.getStatus());
            dto.setRecordId(order.getQueryRecordId());
            dto.setCreatedAt(formatDate(order.getCreatedAt()));
            dto.setUpdatedAt(formatDate(order.getUpdatedAt()));
            RealEstateQueryRecord record = recordMapper.selectOneById(order.getQueryRecordId());
            if (record != null) {
                dto.setRecordName(record.getName());
            }
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public DirectPayOrderResponse regeneratePayOrder(Long userId, Long payOrderId, String payChannel) {
        PayOrder existing = payOrderMapper.selectOneById(payOrderId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new RuntimeException("支付订单不存在");
        }
        if (!"UNPAID".equalsIgnoreCase(existing.getStatus())) {
            throw new RuntimeException("仅能对待支付订单重新支付");
        }
        existing.setStatus("CLOSED");
        existing.setUpdatedAt(new Date());
        payOrderMapper.update(existing);
        
        RealEstateQueryRecord record = recordMapper.selectOneById(existing.getQueryRecordId());
        if (record == null) {
            throw new RuntimeException("关联的查询记录不存在");
        }
        User user = userMapper.selectOneById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String channel = payChannel != null ? payChannel : existing.getPayChannel();
        return createPayOrderForRecord(user, record, channel);
    }
    
    private String generateQrContent(String channel, String orderNo, BigDecimal price) {
        if ("ALIPAY".equals(channel) && alipayPayService != null) {
            return alipayPayService.createPrecreateOrder(orderNo, price, "不动产查询");
        } else if ("WECHAT".equals(channel) && wechatPayService != null) {
            return wechatPayService.createNativeOrder(orderNo, price, "不动产查询");
        }
        return "pay:" + channel + ":" + orderNo;
    }
    
    private String normalizePayChannel(String payChannel) {
        if (payChannel == null) {
            return "WECHAT";
        }
        String upper = payChannel.trim().toUpperCase();
        return ("ALIPAY".equals(upper) || "WECHAT".equals(upper)) ? upper : "WECHAT";
    }
    
    private DirectPayOrderResponse createPayOrderForRecord(User user, RealEstateQueryRecord record, String payChannel) {
        BigDecimal price = record.getQueryFee() != null ? record.getQueryFee() : BigDecimal.ZERO;
        String channel = normalizePayChannel(payChannel);
        String orderNo = "PO" + System.currentTimeMillis() + user.getId();
        String qrContent = generateQrContent(channel, orderNo, price);
        
        PayOrder payOrder = new PayOrder();
        payOrder.setOrderNo(orderNo);
        payOrder.setUserId(user.getId());
        payOrder.setQueryRecordId(record.getId());
        payOrder.setAmount(price);
        payOrder.setPayChannel(channel);
        payOrder.setStatus("UNPAID");
        payOrder.setQrContent(qrContent);
        payOrder.setCreatedAt(new Date());
        payOrder.setUpdatedAt(new Date());
        payOrderMapper.insert(payOrder);
        
        DirectPayOrderResponse resp = new DirectPayOrderResponse();
        resp.setRecordId(record.getId());
        resp.setOrderNo(orderNo);
        resp.setAmount(price);
        resp.setQrContent(qrContent);
        resp.setStatus("UNPAID");
        resp.setPayChannel(channel);
        return resp;
    }
    
    private String formatDate(Date date) {
        return date == null ? null : dateFormat.format(date);
    }
    
    private RealEstateQueryRecordDTO convertToDTO(RealEstateQueryRecord record, boolean includeFiles) {
        if (record == null) {
            return null;
        }
        RealEstateQueryRecordDTO dto = new RealEstateQueryRecordDTO();
        BeanUtils.copyProperties(record, dto);
        if (includeFiles) {
            dto.setFiles(getQueryRecordFiles(record.getId()));
        }
        return dto;
    }
    
    /**
     * 根据用户的查询单价扣减余额，并记录余额变动
     */
    private BigDecimal handleUserBalance(Long userId) {
        User user = userMapper.selectOneById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        BigDecimal price = user.getQueryPrice() != null ? user.getQueryPrice() : BigDecimal.ZERO;
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            // 单价为0则不扣费
            return BigDecimal.ZERO;
        }
        BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        if (balance.compareTo(price) < 0) {
            throw new BalanceInsufficientException("余额不足，请先充值");
        }
        BigDecimal after = balance.subtract(price);
        user.setBalance(after);
        userMapper.update(user);
        
        UserBalanceRecord record = new UserBalanceRecord();
        record.setUserId(userId);
        record.setChangeAmount(price.negate());
        record.setChangeType("DEDUCT");
        record.setRelatedRecordId(null);
        record.setRemark("不动产查询扣费");
        record.setCreatedAt(new Date());
        userBalanceRecordMapper.insert(record);
        
        return price;
    }
    
    @Override
    public void handleRealEstateCallback(Map<String, Object> callbackData) {
        log.info("处理不动产查询回调: {}", callbackData);
        
        try {
            // 从回调数据中提取 requestNo
            String requestNo = null;
            if (callbackData.containsKey("reqNo")) {
                requestNo = String.valueOf(callbackData.get("reqNo"));
            } else if (callbackData.containsKey("requestNo")) {
                requestNo = String.valueOf(callbackData.get("requestNo"));
            }
            
            if (requestNo == null || requestNo.isEmpty()) {
                log.warn("回调数据中缺少 requestNo: {}", callbackData);
                return;
            }
            
            // 根据 requestNo 查找查询记录
            QueryWrapper queryWrapper = QueryWrapper.create()
                .where(REAL_ESTATE_QUERY_RECORD.REQUEST_NO.eq(requestNo));
            RealEstateQueryRecord record = recordMapper.selectOneByQuery(queryWrapper);
            
            if (record == null) {
                log.warn("未找到 requestNo={} 的查询记录", requestNo);
                return;
            }
            
            // 解析回调数据，更新查询记录状态和结果
            // 根据第三方系统的回调格式解析数据（与定时任务逻辑保持一致）
            String approvalStatus = extractString(callbackData, "approvalStatus");
            if ("APPROVED".equals(approvalStatus)) {
                // 检查 authResults 中的 authState
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> authResults = callbackData.get("authResults") instanceof List
                    ? (List<Map<String, Object>>) callbackData.get("authResults")
                    : null;
                if (authResults != null && !authResults.isEmpty()) {
                    Map<String, Object> firstResult = authResults.get(0);
                    String authState = extractString(firstResult, "authState");
                    if ("30".equals(authState)) {
                        // 核查成功
                        record.setStatus("COMPLETED");
                        // 保存完整结果到 result 字段
                        try {
                            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                            record.setResult(objectMapper.writeValueAsString(callbackData));
                        } catch (Exception e) {
                            log.error("序列化回调结果失败", e);
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
                String rejectReason = extractString(callbackData, "rejectReason");
                if (rejectReason != null) {
                    record.setResult("{\"rejectReason\":\"" + rejectReason + "\"}");
                }
            } else if ("IN_APPROVAL".equals(approvalStatus)) {
                // 待审核
                record.setStatus("PROCESSING");
            } else {
                record.setStatus("PROCESSING");
            }
            
            record.setUpdatedAt(new Date());
            recordMapper.update(record);
            
            log.info("成功处理回调，更新查询记录 ID={}, requestNo={}, status={}", 
                record.getId(), requestNo, record.getStatus());
            
        } catch (Exception e) {
            log.error("处理不动产查询回调失败", e);
            throw new RuntimeException("处理回调失败: " + e.getMessage(), e);
        }
    }
    
    private String extractString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? String.valueOf(value) : null;
    }
    
    @Override
    public RealEstateQueryRecordDTO refreshQueryResult(Long recordId) {
        RealEstateQueryRecord record = recordMapper.selectOneById(recordId);
        if (record == null) {
            throw new RuntimeException("查询记录不存在");
        }
        
        String requestNo = record.getRequestNo();
        if (requestNo == null || requestNo.isEmpty()) {
            log.warn("查询记录尚未取得请求编号，无法刷新结果，recordId={}", recordId);
            // 返回当前数据，避免抛出异常
            return convertToDTO(record, true);
        }
        
        // 只有待查询或处理中的记录才需要刷新
        if (!"SUBMITTED".equals(record.getStatus()) && !"PROCESSING".equals(record.getStatus())) {
            log.info("查询记录 ID={} 状态为 {}，无需刷新", recordId, record.getStatus());
            return convertToDTO(record, true);
        }
        
        try {
            // 构建查询请求
            org.yun.common.dto.RealEstateResultQueryRequest queryRequest = 
                new org.yun.common.dto.RealEstateResultQueryRequest();
            queryRequest.setReqNo(requestNo);
            
            // 调用查询服务获取最新结果
            RealEstateQueryResponse response = realEstateService.queryRealEstateResult(queryRequest);
            
            if (response == null || !response.getSuccess()) {
                log.warn("刷新查询结果失败，requestNo={}, message={}", requestNo, 
                    response != null ? response.getMessage() : "响应为空");
                return convertToDTO(record, true);
            }
            
            // 处理查询结果（复用回调处理的逻辑）
            if (response.getResult() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = response.getResult() instanceof Map 
                    ? (Map<String, Object>) response.getResult() 
                    : null;
                
                if (result != null) {
                    // 更新记录状态
                    String approvalStatus = extractString(result, "approvalStatus");
                    if ("APPROVED".equals(approvalStatus)) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> authResults = (List<Map<String, Object>>) result.get("authResults");
                        if (authResults != null && !authResults.isEmpty()) {
                            Map<String, Object> firstResult = authResults.get(0);
                            String authState = extractString(firstResult, "authState");
                            if ("30".equals(authState)) {
                                record.setStatus("COMPLETED");
                                try {
                                    com.fasterxml.jackson.databind.ObjectMapper objectMapper = 
                                        new com.fasterxml.jackson.databind.ObjectMapper();
                                    record.setResult(objectMapper.writeValueAsString(result));
                                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                                    log.error("序列化结果失败", e);
                                }
                            } else if ("40".equals(authState)) {
                                record.setStatus("FAILED");
                            } else {
                                record.setStatus("PROCESSING");
                            }
                        } else {
                            record.setStatus("PROCESSING");
                        }
                    } else if ("UN_APPROVED".equals(approvalStatus)) {
                        record.setStatus("REJECTED");
                        String rejectReason = extractString(result, "rejectReason");
                        if (rejectReason != null) {
                            record.setResult("{\"rejectReason\":\"" + rejectReason + "\"}");
                        }
                    } else if ("IN_APPROVAL".equals(approvalStatus)) {
                        record.setStatus("PROCESSING");
                    } else {
                        record.setStatus("PROCESSING");
                    }
                }
            }
            
            record.setUpdatedAt(new Date());
            recordMapper.update(record);
            
            log.info("成功刷新查询记录，ID={}, requestNo={}, status={}", 
                record.getId(), requestNo, record.getStatus());
            
            return convertToDTO(record, true);
            
        } catch (Exception e) {
            log.error("刷新查询结果异常，ID={}, requestNo={}", record.getId(), requestNo, e);
            throw new RuntimeException("刷新查询结果失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] downloadFile(Long fileId) {
        RealEstateFile file = fileMapper.selectOneById(fileId);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }
        
        try {
            Path filePath = Paths.get(file.getFilePath());
            if (!Files.exists(filePath)) {
                throw new RuntimeException("文件不存在: " + file.getFilePath());
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("读取文件失败，fileId={}, path={}", fileId, file.getFilePath(), e);
            throw new RuntimeException("读取文件失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public RealEstateFileDTO getFileById(Long fileId) {
        RealEstateFile file = fileMapper.selectOneById(fileId);
        if (file == null) {
            throw new RuntimeException("文件不存在");
        }
        
        RealEstateFileDTO dto = new RealEstateFileDTO();
        BeanUtils.copyProperties(file, dto);
        return dto;
    }

    @Override
    public void deletePendingRecord(Long userId, Long recordId) {
        RealEstateQueryRecord record = recordMapper.selectOneById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new RuntimeException("查询记录不存在");
        }
        if (!"PENDING_PAY".equalsIgnoreCase(record.getStatus())) {
            throw new RuntimeException("仅未支付的查询记录可以删除");
        }

        // 删除关联的未支付订单
        List<PayOrder> orders = payOrderMapper.selectListByQuery(
            QueryWrapper.create()
                .where(PAY_ORDER.QUERY_RECORD_ID.eq(recordId))
                .and(PAY_ORDER.STATUS.eq("UNPAID"))
        );
        for (PayOrder order : orders) {
            payOrderMapper.deleteById(order.getId());
        }

        // 删除关联的文件及磁盘文件
        List<RealEstateFile> files = fileMapper.selectListByQuery(
            QueryWrapper.create()
                .where(REAL_ESTATE_FILE.QUERY_RECORD_ID.eq(recordId))
        );
        for (RealEstateFile file : files) {
            if (file.getFilePath() != null) {
                try {
                    Path path = Paths.get(file.getFilePath());
                    if (Files.exists(path)) {
                        Files.delete(path);
                    }
                } catch (IOException e) {
                    log.warn("删除文件失败: {}", file.getFilePath(), e);
                }
            }
            fileMapper.deleteById(file.getId());
        }

        // 删除查询记录
        recordMapper.deleteById(recordId);
    }

    @Override
    public void deletePendingOrder(Long userId, Long orderId) {
        PayOrder order = payOrderMapper.selectOneById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (!"UNPAID".equalsIgnoreCase(order.getStatus())) {
            throw new RuntimeException("仅未支付的订单可以删除");
        }
        payOrderMapper.deleteById(orderId);
    }
}