package org.yun.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    
    /**
     * 处理前端路由，所有非API请求都返回index.html
     * 用于支持Vue Router的history模式
     */
    @GetMapping(value = {
        "/",
        ""
    })
    public ResponseEntity<Resource> index() {
        Resource indexHtml = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(indexHtml);
    }
    
    /**
     * 处理前端路由（SPA支持）
     * 所有 /yunkeji/ 下的非API请求都返回 index.html
     */
    @GetMapping("/**")
    public ResponseEntity<Resource> spaRoutes(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        
        // 如果是API请求，不处理
        if (path.startsWith("/api/")) {
            return ResponseEntity.notFound().build();
        }
        
        // 去掉 /yunkeji 前缀，获取实际资源路径
        String resourcePath = path;
        if (resourcePath.isEmpty() || resourcePath.equals("/")) {
            resourcePath = "/index.html";
        }
        
        // 尝试返回静态资源
        try {
            Resource resource = new ClassPathResource("static" + resourcePath);
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(getContentType(resourcePath)))
                        .body(resource);
            }
        } catch (Exception e) {
            // 记录异常但继续返回 index.html（用于SPA路由）
            log.debug("加载静态资源失败，返回index.html: {}", resourcePath, e);
        }
        
        // 返回 index.html 用于 SPA 路由
        Resource indexHtml = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(indexHtml);
    }
    
    private String getContentType(String path) {
        if (path.endsWith(".js")) {
            return "application/javascript";
        } else if (path.endsWith(".css")) {
            return "text/css";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream";
    }
}
