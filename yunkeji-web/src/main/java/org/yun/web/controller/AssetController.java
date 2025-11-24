package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.AssetResponse;
import org.yun.common.dto.CreateAssetRequest;
import org.yun.service.AssetService;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Tag(name = "资产管理", description = "资产相关的API")
public class AssetController {
    
    @Autowired
    private AssetService assetService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建资产并上传文件")
    public AssetResponse createAsset(@RequestPart("asset") CreateAssetRequest request,
                                     @RequestPart("file") MultipartFile file) {
        return assetService.createAsset(request, file);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取资产")
    public AssetResponse getAssetById(@PathVariable Long id) {
        return assetService.getAssetById(id);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID获取资产列表")
    public List<AssetResponse> getAssetsByUserId(@PathVariable Long userId) {
        return assetService.getAssetsByUserId(userId);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除资产")
    public void deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
    }
    
    @GetMapping("/download/{id}")
    @Operation(summary = "下载资产文件")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        byte[] data = assetService.downloadFile(id);
        AssetResponse asset = assetService.getAssetById(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", asset.getFileName());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
}