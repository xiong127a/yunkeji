package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.PayOrderDTO;
import org.yun.common.dto.RealEstateFileDTO;
import org.yun.common.dto.RealEstateQueryRecordDTO;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.common.dto.UserResponse;
import org.yun.common.util.JwtUtil;
import org.yun.service.UserManagementService;
import org.yun.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户不动产查询管理相关API")
public class UserManagementController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
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
    
    @PostMapping("/real-estate/query-direct-pay")
    @Operation(summary = "提交不动产查询请求（扫码直付）")
    public org.yun.common.dto.DirectPayOrderResponse submitRealEstateQueryDirectPay(
            @RequestHeader("Authorization") String token,
            @RequestBody RealEstateQueryRequest request,
            @RequestParam(defaultValue = "WECHAT") String payChannel) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userManagementService.createDirectPayQuery(userId, request, payChannel);
    }
    
    @PostMapping("/real-estate/query-with-files-direct-pay")
    @Operation(summary = "提交不动产查询请求（带文件，扫码直付）")
    public org.yun.common.dto.DirectPayOrderResponse submitRealEstateQueryWithFilesDirectPay(
            @RequestHeader("Authorization") String token,
            @RequestPart("request") RealEstateQueryRequest request,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestParam(defaultValue = "WECHAT") String payChannel) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userManagementService.createDirectPayQueryWithFiles(userId, request, files, payChannel);
    }
    
    @GetMapping("/pay/orders/pending")
    @Operation(summary = "获取当前用户待支付的订单")
    public List<PayOrderDTO> getPendingPayOrders(@RequestHeader("Authorization") String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userManagementService.getPendingPayOrders(userId);
    }
    
    @PostMapping("/pay/orders/{orderId}/regenerate")
    @Operation(summary = "重新生成扫码支付二维码")
    public org.yun.common.dto.DirectPayOrderResponse regeneratePayOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId,
            @RequestParam(required = false) String payChannel) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        return userManagementService.regeneratePayOrder(userId, orderId, payChannel);
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

    @DeleteMapping("/real-estate/records/{recordId}")
    @Operation(summary = "删除待支付的查询记录")
    public void deletePendingRecord(@RequestHeader("Authorization") String token,
                                    @PathVariable Long recordId) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        userManagementService.deletePendingRecord(userId, recordId);
    }

    @DeleteMapping("/pay/orders/{orderId}")
    @Operation(summary = "删除待支付订单")
    public void deletePendingOrder(@RequestHeader("Authorization") String token,
                                   @PathVariable Long orderId) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Long userId = jwtUtil.extractUserId(actualToken);
        userManagementService.deletePendingOrder(userId, orderId);
    }
    
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
}