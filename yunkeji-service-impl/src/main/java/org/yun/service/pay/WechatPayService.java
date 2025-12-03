package org.yun.service.pay;

import java.math.BigDecimal;

public interface WechatPayService {
    
    /**
     * 调用微信支付统一下单（Native）接口，返回code_url
     */
    String createNativeOrder(String orderNo, BigDecimal amount, String description);
    
    /**
     * 处理微信支付回调通知
     */
    void handleNotify(String notifyBody, String wechatSignature, String nonce, String timestamp);
}




