package org.yun.service;

import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.RealEstateFileDTO;
import org.yun.common.dto.RealEstateQueryRecordDTO;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;

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
}