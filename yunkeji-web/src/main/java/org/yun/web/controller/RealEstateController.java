package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.common.dto.RealEstateResultQueryRequest;
import org.yun.service.RealEstateService;

@RestController
@RequestMapping("/api/real-estate")
@Tag(name = "大数据查询", description = "大数据信息查询相关API")
public class RealEstateController {
    
    @Autowired
    private RealEstateService realEstateService;
    
    @PostMapping("/query")
    @Operation(summary = "提交大数据查询请求")
    public RealEstateQueryResponse submitRealEstateQuery(@RequestBody RealEstateQueryRequest request) {
        return realEstateService.submitRealEstateQuery(request);
    }
    
    @PostMapping("/query-with-files")
    @Operation(summary = "提交大数据查询请求（带文件）")
    public RealEstateQueryResponse submitRealEstateQueryWithFiles(
            @RequestPart("request") RealEstateQueryRequest request,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        return realEstateService.submitRealEstateQuery(request);
    }
    
    @PostMapping("/result")
    @Operation(summary = "查询大数据结果")
    public RealEstateQueryResponse queryRealEstateResult(@RequestBody RealEstateResultQueryRequest request) {
        return realEstateService.queryRealEstateResult(request);
    }
}