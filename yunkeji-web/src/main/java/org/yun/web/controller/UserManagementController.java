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
import org.yun.common.dto.RechargeBalanceRequest;
import org.yun.common.dto.UserResponse;
import org.yun.common.util.JwtUtil;
import org.yun.service.UserManagementService;
import org.yun.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户大数据查询管理相关API")
public class UserManagementController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/real-estate/query")
    @Operation(summary = "提交大数据查询请求")
    public RealEstateQueryRecordDTO submitRealEstateQuery(
            @RequestHeader("Authorization") String token,
            @RequestBody RealEstateQueryRequest request) {
        // 从token中解析用户ID
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userManagementService.submitRealEstateQuery(userId, request);
    }
    
    @PostMapping("/real-estate/query-with-files")
    @Operation(summary = "提交大数据查询请求（带文件）")
    public RealEstateQueryRecordDTO submitRealEstateQueryWithFiles(
            @RequestHeader("Authorization") String token,
            @RequestPart("request") RealEstateQueryRequest request,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        // 从token中解析用户ID
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userManagementService.submitRealEstateQueryWithFiles(userId, request, files);
    }
    
    // 支付相关接口已关闭，系统仅支持余额扣费模式
    
    @GetMapping("/real-estate/result/{requestNo}")
    @Operation(summary = "查询大数据结果")
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
    
    @GetMapping("/profile")
    @Operation(summary = "获取当前登录用户的资料（含余额和查询单价）")
    public UserResponse getCurrentUserProfile(@RequestHeader("Authorization") String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userService.getUserById(userId);
    }
    
    @PostMapping("/real-estate/records/{recordId}/refresh")
    @Operation(summary = "手动刷新查询结果")
    public RealEstateQueryRecordDTO refreshQueryResult(@PathVariable Long recordId) {
        return userManagementService.refreshQueryResult(recordId);
    }

    // 删除待支付记录 / 订单的接口已关闭（余额模式下不会产生待支付状态）
    
    @GetMapping("/real-estate/files/{fileId}/download")
    @Operation(summary = "下载查询记录关联的文件")
    public org.springframework.http.ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        byte[] data = userManagementService.downloadFile(fileId);
        org.yun.common.dto.RealEstateFileDTO file = userManagementService.getFileById(fileId);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getFileName());
        
        return org.springframework.http.ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @PostMapping("/subordinates/{childId}/transfer")
    @Operation(summary = "父级为子级分配余额（先扣父级）")
    public UserResponse transferToChild(@RequestHeader("Authorization") String token,
                                        @PathVariable Long childId,
                                        @RequestBody RechargeBalanceRequest request) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long parentId = jwtUtil.extractUserId(actualToken);
        return userService.transferToChild(parentId, childId, request);
    }

}