package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yun.common.dto.RealEstateQueryRecordDTO;
import org.yun.common.dto.UpdateQueryFeeRequest;
import org.yun.common.util.JwtUtil;
import org.yun.service.UserManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/real-estate")
@Tag(name = "管理员-大数据管理", description = "管理员管理大数据查询记录的API")
public class AdminRealEstateController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/records")
    @Operation(summary = "管理员获取所有查询记录")
    public List<RealEstateQueryRecordDTO> getAllRecords(@RequestHeader("Authorization") String token) {
        ensureAdmin(token);
        return userManagementService.getAllQueryRecords();
    }
    
    @PutMapping("/records/{recordId}/fee")
    @Operation(summary = "管理员更新查询费用")
    public RealEstateQueryRecordDTO updateQueryFee(@RequestHeader("Authorization") String token,
                                                   @PathVariable Long recordId,
                                                   @RequestBody UpdateQueryFeeRequest request) {
        ensureAdmin(token);
        RealEstateQueryRecordDTO updated = userManagementService.updateQueryFee(recordId, request.getQueryFee());
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "查询记录不存在");
        }
        return updated;
    }
    
    private void ensureAdmin(String authorizationHeader) {
        String token = authorizationHeader;
        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未授权访问");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String role = jwtUtil.extractRole(token);
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "仅管理员可访问该接口");
        }
    }
}

