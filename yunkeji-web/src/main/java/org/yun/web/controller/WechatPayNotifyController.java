package org.yun.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yun.service.pay.WechatPayService;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/pay/wechat")
public class WechatPayNotifyController {
    
    private static final Logger log = LoggerFactory.getLogger(WechatPayNotifyController.class);
    
    private final WechatPayService wechatPayService;
    
    public WechatPayNotifyController(WechatPayService wechatPayService) {
        this.wechatPayService = wechatPayService;
    }
    
    @PostMapping("/notify")
    public ResponseEntity<String> handleNotify(HttpServletRequest request,
                                               @RequestHeader("Wechatpay-Signature") String signature,
                                               @RequestHeader("Wechatpay-Timestamp") String timestamp,
                                               @RequestHeader("Wechatpay-Nonce") String nonce,
                                               @RequestHeader("Wechatpay-Serial") String serialNo) {
        try {
            StringBuilder notifyJson = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    notifyJson.append(line);
                }
            }
            log.info("Received WeChat notify: {}", notifyJson);
            wechatPayService.handleNotify(notifyJson.toString(), signature, nonce, timestamp);
            return ResponseEntity.ok("{\"code\":\"SUCCESS\",\"message\":\"成功\"}");
        } catch (IOException e) {
            log.error("Handle WeChat notify error", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"code\":\"FAIL\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}


