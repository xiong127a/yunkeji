package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授权书下载控制器
 */
@RestController
@RequestMapping("/api/authorization")
@Tag(name = "授权书", description = "授权书下载相关API")
public class AuthorizationController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthorizationController.class);
    
    /**
     * 下载个人信息查询及使用授权书
     */
    @GetMapping("/download")
    @Operation(summary = "下载授权书PDF")
    public ResponseEntity<Resource> downloadAuthorization() {
        try {
            // 从resources目录加载授权书PDF
            Resource resource = new ClassPathResource("authorization/云科技个人信息查询及使用授权书.pdf");
            
            if (!resource.exists()) {
                log.warn("授权书PDF文件不存在");
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"云科技个人信息查询及使用授权书.pdf\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("下载授权书PDF时发生异常", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

