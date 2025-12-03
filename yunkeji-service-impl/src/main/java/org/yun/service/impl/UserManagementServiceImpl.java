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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.yun.dao.table.RealEstateFileTableDef.REAL_ESTATE_FILE;
import static org.yun.dao.table.RealEstateQueryRecordTableDef.REAL_ESTATE_QUERY_RECORD;
import static org.yun.dao.table.PayOrderTableDef.PAY_ORDER;

@Service
public class UserManagementServiceImpl implements UserManagementService {
    
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
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
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
            // 创建上传目录
            File uploadDir = new File(uploadPath, "real-estate");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        // 保存文件
                        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadPath, "real-estate", fileName);
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
            File uploadDir = new File(uploadPath, "real-estate");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadPath, "real-estate", fileName);
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
}