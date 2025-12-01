package org.yun.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.yun.web.interceptor.JwtInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/user/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedOrigins("http://localhost:3000", "http://101.200.217.106:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传的文件
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/", "file:/opt/yunkeji/uploads/");
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 将根路径和/yunkeji路径重定向到/yunkeji/
        registry.addViewController("/").setViewName("redirect:/yunkeji/");
        registry.addViewController("/yunkeji").setViewName("redirect:/yunkeji/");
    }
}