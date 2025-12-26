package org.yun.service;

import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.common.dto.RealEstateResultQueryRequest;

public interface RealEstateService {
    
    /**
     * 提交大数据查询请求
     */
    RealEstateQueryResponse submitRealEstateQuery(RealEstateQueryRequest request);
    
    /**
     * 查询大数据结果
     */
    RealEstateQueryResponse queryRealEstateResult(RealEstateResultQueryRequest request);
}