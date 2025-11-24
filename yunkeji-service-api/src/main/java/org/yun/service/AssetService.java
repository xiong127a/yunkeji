package org.yun.service;

import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.AssetResponse;
import org.yun.common.dto.CreateAssetRequest;

import java.util.List;

public interface AssetService {
    
    AssetResponse createAsset(CreateAssetRequest request, MultipartFile file);
    
    AssetResponse getAssetById(Long id);
    
    List<AssetResponse> getAssetsByUserId(Long userId);
    
    void deleteAsset(Long id);
    
    byte[] downloadFile(Long assetId);
}