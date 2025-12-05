package org.yun.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(BalanceInsufficientException.class)
    public ResponseEntity<Map<String, Object>> handleBalanceInsufficient(BalanceInsufficientException ex) {
        log.warn("余额不足异常: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.value());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("系统异常", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", ex.getMessage() != null ? ex.getMessage() : "服务器异常");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}






