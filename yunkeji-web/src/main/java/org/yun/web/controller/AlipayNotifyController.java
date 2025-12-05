package org.yun.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yun.service.pay.AlipayPayService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pay/alipay")
public class AlipayNotifyController {
    
    private static final Logger log = LoggerFactory.getLogger(AlipayNotifyController.class);
    
    private final AlipayPayService alipayPayService;
    
    public AlipayNotifyController(AlipayPayService alipayPayService) {
        this.alipayPayService = alipayPayService;
    }
    
    @PostMapping("/notify")
    public String handleNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values.length > 0) {
                params.put(key, values[0]);
            }
        });
        log.info("Received Alipay notify: {}", params);
        alipayPayService.handleNotify(params);
        return "success";
    }
}














