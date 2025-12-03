package org.yun.service.pay.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yun.common.config.WechatPayProperties;
import org.yun.service.pay.WechatPayService;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

@Service
public class WechatPayServiceImpl implements WechatPayService, InitializingBean {
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final Logger log = LoggerFactory.getLogger(WechatPayServiceImpl.class);
    
    @Autowired
    private WechatPayProperties wechatPayProperties;
    
    private CloseableHttpClient httpClient;
    
    @SuppressWarnings("deprecation")
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            PrivateKey privateKey = PemUtil.loadPrivateKey(
                    new FileInputStream(wechatPayProperties.getPrivateKeyPath().replace("classpath:", "src/main/resources/"))
            );
            
            AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(
                            wechatPayProperties.getMchid(),
                            new PrivateKeySigner(wechatPayProperties.getSerialNo(), privateKey)
                    ),
                    wechatPayProperties.getApiV3Key().getBytes(StandardCharsets.UTF_8)
            );
            
            httpClient = WechatPayHttpClientBuilder.create()
                    .withMerchant(wechatPayProperties.getMchid(), wechatPayProperties.getSerialNo(), privateKey)
                    .withValidator(new WechatPay2Validator(verifier))
                    .build();
        } catch (Exception e) {
            log.warn("WechatPayService init skipped, please configure certificates later: {}", e.getMessage());
            httpClient = null;
        }
    }
    
    @Override
    public String createNativeOrder(String orderNo, BigDecimal amount, String description) {
        if (httpClient == null) {
            // 没有证书时返回模拟二维码，避免阻塞流程
            return "wechat-mock:" + orderNo;
        }
        try {
            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/native");
            Map<String, Object> body = new HashMap<>();
            body.put("mchid", wechatPayProperties.getMchid());
            body.put("appid", wechatPayProperties.getAppid());
            body.put("description", description != null ? description : wechatPayProperties.getDescription());
            body.put("out_trade_no", orderNo);
            body.put("notify_url", wechatPayProperties.getNotifyUrl());
            Map<String, Object> amountMap = new HashMap<>();
            amountMap.put("total", amount.multiply(BigDecimal.valueOf(100)).intValue());
            amountMap.put("currency", "CNY");
            body.put("amount", amountMap);
            
            String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body);
            httpPost.setEntity(new StringEntity(jsonBody, "UTF-8"));
            httpPost.setHeader("Content-Type", "application/json");
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseString = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                if (statusCode == 200 || statusCode == 201) {
                    Map<String, Object> respMap = objectMapper.readValue(responseString, new TypeReference<Map<String, Object>>() {});
                    return (String) respMap.get("code_url");
                } else {
                    log.error("Wechat pay unified order failed, status: {}, body: {}", statusCode, responseString);
                    throw new RuntimeException("调用微信支付统一下单失败");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("调用微信支付统一下单异常", e);
        }
    }
    
    @Override
    public void handleNotify(String notifyBody, String wechatSignature, String nonce, String timestamp) {
        // TODO: 根据微信官方文档验证签名并解密报文，然后更新订单与查询状态
        // 1. 使用 AutoUpdateCertificatesVerifier 校验 wechatSignature
        // 2. 解密 resource.ciphertext 获取交易详情
        // 3. 根据 out_trade_no 更新 pay_order 与 real_estate_query_record
        // 4. 触发不动产查询（或异步任务执行）
    }
}


