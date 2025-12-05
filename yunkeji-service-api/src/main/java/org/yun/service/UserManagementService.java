package org.yun.service;

import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.DirectPayOrderResponse;
import org.yun.common.dto.PayOrderDTO;
import org.yun.common.dto.RealEstateFileDTO;
import org.yun.common.dto.RealEstateQueryRecordDTO;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;

import java.math.BigDecimal;
import java.util.List;

public interface UserManagementService {
    
    /**
     * 提交不动产查询请求
     */
    RealEstateQueryRecordDTO submitRealEstateQuery(Long userId, RealEstateQueryRequest request);
    
    /**
     * 提交不动产查询请求（带文件）
     */
    RealEstateQueryRecordDTO submitRealEstateQueryWithFiles(Long userId, RealEstateQueryRequest request, MultipartFile[] files);
    
    /**
     * 查询不动产结果
     */
    RealEstateQueryResponse queryRealEstateResult(String requestNo);
    
    /**
     * 获取用户的查询记录
     */
    List<RealEstateQueryRecordDTO> getUserQueryRecords(Long userId);
    
    /**
     * 获取查询记录详情
     */
    RealEstateQueryRecordDTO getQueryRecordDetail(Long recordId);
    
    /**
     * 获取查询记录关联的文件
     */
    List<RealEstateFileDTO> getQueryRecordFiles(Long recordId);
    
    /**
     * 管理员获取所有查询记录
     */
    List<RealEstateQueryRecordDTO> getAllQueryRecords();
    
    /**
     * 更新单条记录的查询费用
     */
    RealEstateQueryRecordDTO updateQueryFee(Long recordId, BigDecimal queryFee);
    
    /**
     * 直付模式：创建查询记录并生成支付订单（不扣余额）
     */
    DirectPayOrderResponse createDirectPayQuery(Long userId, RealEstateQueryRequest request, String payChannel);
    
    DirectPayOrderResponse createDirectPayQueryWithFiles(Long userId, RealEstateQueryRequest request, MultipartFile[] files, String payChannel);
    
    List<PayOrderDTO> getPendingPayOrders(Long userId);
    
    DirectPayOrderResponse regeneratePayOrder(Long userId, Long payOrderId, String payChannel);
    
    /**
     * 处理不动产查询结果回调
     */
    void handleRealEstateCallback(java.util.Map<String, Object> callbackData);
    
    /**
     * 手动刷新查询结果（主动查询）
     * @param recordId 查询记录ID
     * @return 更新后的查询记录
     */
    RealEstateQueryRecordDTO refreshQueryResult(Long recordId);
    
    /**
     * 下载文件
     * @param fileId 文件ID
     * @return 文件字节数组
     */
    byte[] downloadFile(Long fileId);
    
    /**
     * 根据ID获取文件信息
     * @param fileId 文件ID
     * @return 文件DTO
     */
    RealEstateFileDTO getFileById(Long fileId);

    /**
     * 删除待支付的查询记录（仅限未支付）
     */
    void deletePendingRecord(Long userId, Long recordId);

    /**
     * 删除待支付订单（仅限未支付）
     */
    void deletePendingOrder(Long userId, Long orderId);
}