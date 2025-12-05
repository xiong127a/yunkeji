package org.yun.service.pay.impl;


import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yun.common.config.AlipayPayProperties;
import org.yun.service.pay.AlipayPayService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlipayPayServiceImpl implements AlipayPayService, InitializingBean {
    
    private static final Logger log = LoggerFactory.getLogger(AlipayPayServiceImpl.class);
    
    @Autowired
    private AlipayPayProperties alipayPayProperties;
    
    private AlipayClient alipayClient;
    
    @Override
    public void afterPropertiesSet() {
        alipayClient = new DefaultAlipayClient(
                alipayPayProperties.getGatewayUrl(),
                alipayPayProperties.getAppId(),
                alipayPayProperties.getPrivateKey(),
                "json",
                "UTF-8",
                alipayPayProperties.getAlipayPublicKey(),
                alipayPayProperties.getSignType()
        );
    }
    
    @Override
    public String createPrecreateOrder(String orderNo, BigDecimal amount, String subject) {
        try {
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            request.setNotifyUrl(alipayPayProperties.getNotifyUrl());
            Map<String, Object> bizContent = new HashMap<>();
            bizContent.put("out_trade_no", orderNo);
            bizContent.put("total_amount", amount.setScale(2, RoundingMode.HALF_UP).toPlainString());
            bizContent.put("subject", subject != null ? subject : alipayPayProperties.getDescription());
            request.setBizContent(JSON.toJSONString(bizContent));
            
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return response.getQrCode();
            } else {
                log.error("Alipay precreate order failed: {} - {}", response.getSubCode(), response.getSubMsg());
                throw new RuntimeException("调用支付宝预下单失败: " + response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("调用支付宝预下单异常", e);
        }
    }
    
    @Override
    public void handleNotify(Map<String, String> params) {
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayPayProperties.getAlipayPublicKey(),
                    "UTF-8",
                    alipayPayProperties.getSignType()
            );
            if (!signVerified) {
                log.warn("Alipay notify signature verify failed");
                throw new RuntimeException("支付宝回调验签失败");
            }
            // TODO: 根据 out_trade_no 更新 pay_order 与 real_estate_query_record，触发查询
            // String outTradeNo = params.get("out_trade_no");
            // String tradeStatus = params.get("trade_status");
        } catch (AlipayApiException e) {
            throw new RuntimeException("支付宝回调验签异常", e);
        }
    }
}














