package org.yun.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yun.common.dto.RealEstateFileDTO;
import org.yun.common.dto.RealEstateQueryRecordDTO;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.dao.entity.RealEstateFile;
import org.yun.dao.entity.RealEstateQueryRecord;
import org.yun.dao.mapper.RealEstateFileMapper;
import org.yun.dao.mapper.RealEstateQueryRecordMapper;
import org.yun.service.RealEstateService;
import org.yun.service.UserManagementService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.yun.dao.table.RealEstateFileTableDef.REAL_ESTATE_FILE;
import static org.yun.dao.table.RealEstateQueryRecordTableDef.REAL_ESTATE_QUERY_RECORD;

@Service
public class UserManagementServiceImpl implements UserManagementService {
    
    @Autowired
    private RealEstateQueryRecordMapper recordMapper;
    
    @Autowired
    private RealEstateFileMapper fileMapper;
    
    @Autowired
    private RealEstateService realEstateService;
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Override
    public RealEstateQueryRecordDTO submitRealEstateQuery(Long userId, RealEstateQueryRequest request) {
        // 保存查询记录
        RealEstateQueryRecord record = new RealEstateQueryRecord();
        record.setUserId(userId);
        record.setName(request.getName());
        record.setIdCard(request.getIdCard());
        record.setStatus("SUBMITTED");
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());
        recordMapper.insert(record);
        
        // 调用不动产查询服务
        RealEstateQueryResponse response = realEstateService.submitRealEstateQuery(request);
        
        // 更新记录中的请求编号
        if (response.getSuccess() && response.getResult() != null) {
            record.setRequestNo(response.getResult().toString());
            recordMapper.update(record);
        }
        
        // 转换为DTO
        RealEstateQueryRecordDTO dto = new RealEstateQueryRecordDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
    
    @Override
    public RealEstateQueryRecordDTO submitRealEstateQueryWithFiles(Long userId, RealEstateQueryRequest request, MultipartFile[] files) {
        // 保存查询记录
        RealEstateQueryRecord record = new RealEstateQueryRecord();
        record.setUserId(userId);
        record.setName(request.getName());
        record.setIdCard(request.getIdCard());
        record.setStatus("SUBMITTED");
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());
        recordMapper.insert(record);
        
        // 处理上传的文件
        if (files != null && files.length > 0) {
            // 创建上传目录
            File uploadDir = new File(uploadPath, "real-estate");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        // 保存文件
                        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        Path filePath = Paths.get(uploadPath, "real-estate", fileName);
                        Files.write(filePath, file.getBytes());
                        
                        // 保存文件记录到数据库
                        RealEstateFile realEstateFile = new RealEstateFile();
                        realEstateFile.setQueryRecordId(record.getId());
                        realEstateFile.setFileName(file.getOriginalFilename());
                        realEstateFile.setFilePath(filePath.toString());
                        realEstateFile.setFileSize(file.getSize());
                        realEstateFile.setCreatedAt(new Date());
                        // 简单根据文件名判断文件类型
                        String originalName = file.getOriginalFilename();
                        if (originalName != null) {
                            if (originalName.toLowerCase().endsWith(".pdf")) {
                                realEstateFile.setFileType("PDF");
                            } else if (originalName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                                realEstateFile.setFileType("IMAGE");
                            } else {
                                realEstateFile.setFileType("OTHER");
                            }
                        }
                        fileMapper.insert(realEstateFile);
                    } catch (IOException e) {
                        throw new RuntimeException("文件保存失败: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }
        
        // 调用不动产查询服务
        RealEstateQueryResponse response = realEstateService.submitRealEstateQuery(request);
        
        // 更新记录中的请求编号
        if (response.getSuccess() && response.getResult() != null) {
            record.setRequestNo(response.getResult().toString());
            recordMapper.update(record);
        }
        
        // 转换为DTO
        RealEstateQueryRecordDTO dto = new RealEstateQueryRecordDTO();
        BeanUtils.copyProperties(record, dto);
        return dto;
    }
    
    @Override
    public RealEstateQueryResponse queryRealEstateResult(String requestNo) {
        // 查询不动产结果
        org.yun.common.dto.RealEstateResultQueryRequest request = 
            new org.yun.common.dto.RealEstateResultQueryRequest();
        request.setReqNo(requestNo);
        return realEstateService.queryRealEstateResult(request);
    }
    
    @Override
    public List<RealEstateQueryRecordDTO> getUserQueryRecords(Long userId) {
        List<RealEstateQueryRecord> records = recordMapper.selectListByQuery(
            QueryWrapper.create()
                .where(REAL_ESTATE_QUERY_RECORD.USER_ID.eq(userId))
                .orderBy(REAL_ESTATE_QUERY_RECORD.CREATED_AT.desc())
        );
        
        return records.stream().map(record -> {
            RealEstateQueryRecordDTO dto = new RealEstateQueryRecordDTO();
            BeanUtils.copyProperties(record, dto);
            // 获取关联的文件
            dto.setFiles(getQueryRecordFiles(record.getId()));
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    public RealEstateQueryRecordDTO getQueryRecordDetail(Long recordId) {
        RealEstateQueryRecord record = recordMapper.selectOneById(recordId);
        if (record == null) {
            return null;
        }
        
        RealEstateQueryRecordDTO dto = new RealEstateQueryRecordDTO();
        BeanUtils.copyProperties(record, dto);
        // 获取关联的文件
        dto.setFiles(getQueryRecordFiles(recordId));
        return dto;
    }
    
    @Override
    public List<RealEstateFileDTO> getQueryRecordFiles(Long recordId) {
        List<RealEstateFile> files = fileMapper.selectListByQuery(
            QueryWrapper.create()
                .where(REAL_ESTATE_FILE.QUERY_RECORD_ID.eq(recordId))
        );
        
        return files.stream().map(file -> {
            RealEstateFileDTO dto = new RealEstateFileDTO();
            BeanUtils.copyProperties(file, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}