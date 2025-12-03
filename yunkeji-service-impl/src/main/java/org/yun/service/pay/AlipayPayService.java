package org.yun.service.pay;

import java.math.BigDecimal;
import java.util.Map;

public interface AlipayPayService {
    
    /**
     * 创建支付宝预下单，返回二维码内容
     */
    String createPrecreateOrder(String orderNo, BigDecimal amount, String subject);
    
    /**
     * 处理支付宝回调
     */
    void handleNotify(Map<String, String> params);
}




