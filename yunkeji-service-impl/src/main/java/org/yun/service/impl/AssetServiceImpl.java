package org.yun.service.impl;


import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.AssetResponse;
import org.yun.common.dto.CreateAssetRequest;
import org.yun.dao.entity.Asset;
import org.yun.dao.mapper.AssetMapper;
import org.yun.dao.table.AssetTableDef;
import org.yun.service.AssetService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {
    
    @Autowired
    private AssetMapper assetMapper;
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Override
    public AssetResponse createAsset(CreateAssetRequest request, MultipartFile file) {
        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // 保存文件
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadPath, fileName);
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败", e);
        }
        
        // 保存资产信息到数据库
        Asset asset = new Asset();
        BeanUtils.copyProperties(request, asset);
        asset.setFileName(file.getOriginalFilename());
        asset.setFileSize(file.getSize());
        asset.setFilePath(filePath.toString());
        assetMapper.insert(asset);
        
        AssetResponse response = new AssetResponse();
        BeanUtils.copyProperties(asset, response);
        return response;
    }
    
    @Override
    public AssetResponse getAssetById(Long id) {
        Asset asset = assetMapper.selectOneById(id);
        if (asset == null) {
            return null;
        }
        
        AssetResponse response = new AssetResponse();
        BeanUtils.copyProperties(asset, response);
        return response;
    }
    
    @Override
    public List<AssetResponse> getAssetsByUserId(Long userId) {
        List<Asset> assets = assetMapper.selectListByQuery(
            QueryWrapper.create()
                .where(AssetTableDef.ASSET.USER_ID.eq(userId))
        );
        
        return assets.stream().map(asset -> {
            AssetResponse response = new AssetResponse();
            BeanUtils.copyProperties(asset, response);
            return response;
        }).collect(Collectors.toList());
    }
    
    @Override
    public void deleteAsset(Long id) {
        Asset asset = assetMapper.selectOneById(id);
        if (asset != null) {
            // 删除文件
            File file = new File(asset.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            
            // 删除数据库记录
            assetMapper.deleteById(id);
        }
    }
    
    @Override
    public byte[] downloadFile(Long assetId) {
        Asset asset = assetMapper.selectOneById(assetId);
        if (asset == null) {
            throw new RuntimeException("资产不存在");
        }
        
        try {
            Path filePath = Paths.get(asset.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败", e);
        }
    }
}