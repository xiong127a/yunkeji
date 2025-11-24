package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.RealEstateFileDTO;
import org.yun.common.dto.RealEstateQueryRecordDTO;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.common.util.JwtUtil;
import org.yun.service.UserManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户不动产查询管理相关API")
public class UserManagementController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/real-estate/query")
    @Operation(summary = "提交不动产查询请求")
    public RealEstateQueryRecordDTO submitRealEstateQuery(
            @RequestHeader("Authorization") String token,
            @RequestBody RealEstateQueryRequest request) {
        // 从token中解析用户ID
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userManagementService.submitRealEstateQuery(userId, request);
    }
    
    @PostMapping("/real-estate/query-with-files")
    @Operation(summary = "提交不动产查询请求（带文件）")
    public RealEstateQueryRecordDTO submitRealEstateQueryWithFiles(
            @RequestHeader("Authorization") String token,
            @RequestPart("request") RealEstateQueryRequest request,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        // 从token中解析用户ID
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userManagementService.submitRealEstateQueryWithFiles(userId, request, files);
    }
    
    @GetMapping("/real-estate/result/{requestNo}")
    @Operation(summary = "查询不动产结果")
    public RealEstateQueryResponse queryRealEstateResult(@PathVariable String requestNo) {
        return userManagementService.queryRealEstateResult(requestNo);
    }
    
    @GetMapping("/real-estate/records")
    @Operation(summary = "获取用户的查询记录")
    public List<RealEstateQueryRecordDTO> getUserQueryRecords(
            @RequestHeader("Authorization") String token) {
        // 从token中解析用户ID
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userManagementService.getUserQueryRecords(userId);
    }
    
    @GetMapping("/real-estate/records/{recordId}")
    @Operation(summary = "获取查询记录详情")
    public RealEstateQueryRecordDTO getQueryRecordDetail(@PathVariable Long recordId) {
        return userManagementService.getQueryRecordDetail(recordId);
    }
    
    @GetMapping("/real-estate/records/{recordId}/files")
    @Operation(summary = "获取查询记录关联的文件")
    public List<RealEstateFileDTO> getQueryRecordFiles(@PathVariable Long recordId) {
        return userManagementService.getQueryRecordFiles(recordId);
    }
}