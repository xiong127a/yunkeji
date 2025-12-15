package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yun.service.UserManagementService;

import java.util.Map;

/**
 * 不动产查询回调接口
 * 第三方系统查询完成后会调用此接口通知结果
 */
@RestController
@RequestMapping("/api/callback/real-estate")
@Tag(name = "不动产查询回调", description = "接收第三方系统的不动产查询结果回调")
public class RealEstateCallbackController {
    
    private static final Logger log = LoggerFactory.getLogger(RealEstateCallbackController.class);
    
    @Autowired
    private UserManagementService userManagementService;
    
    /**
     * 接收不动产查询结果回调
     * 第三方系统查询完成后会调用此接口
     */
    @PostMapping
    @Operation(summary = "接收不动产查询结果回调")
    public Map<String, Object> handleCallback(@RequestBody Map<String, Object> callbackData) {
        log.info("收到不动产查询结果回调: {}", callbackData);
        
        try {
            // 处理回调数据
            userManagementService.handleRealEstateCallback(callbackData);
            
            // 返回成功响应（第三方系统需要）
            return Map.of(
                "success", true,
                "code", 200,
                "message", "回调处理成功"
            );
        } catch (Exception e) {
            log.error("处理不动产查询回调失败", e);
            return Map.of(
                "success", false,
                "code", 500,
                "message", "回调处理失败: " + e.getMessage()
            );
        }
    }
}









