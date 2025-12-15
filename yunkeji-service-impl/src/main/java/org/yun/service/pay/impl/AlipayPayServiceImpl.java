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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yun.common.config.AlipayPayProperties;
import org.yun.common.dto.RealEstateQueryRequest;
import org.yun.common.dto.RealEstateQueryResponse;
import org.yun.dao.entity.PayOrder;
import org.yun.dao.entity.RealEstateFile;
import org.yun.dao.entity.RealEstateQueryRecord;
import org.yun.dao.mapper.PayOrderMapper;
import org.yun.dao.mapper.RealEstateFileMapper;
import org.yun.dao.mapper.RealEstateQueryRecordMapper;
import org.yun.dao.table.PayOrderTableDef;
import org.yun.dao.table.RealEstateFileTableDef;
import org.yun.service.RealEstateService;
import org.yun.service.pay.AlipayPayService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlipayPayServiceImpl implements AlipayPayService, InitializingBean {
    
    private static final Logger log = LoggerFactory.getLogger(AlipayPayServiceImpl.class);
    
    @Autowired
    private AlipayPayProperties alipayPayProperties;
    
    @Autowired
    private PayOrderMapper payOrderMapper;
    
    @Autowired
    private RealEstateQueryRecordMapper recordMapper;
    
    @Autowired
    private RealEstateFileMapper realEstateFileMapper;
    
    @Autowired
    private RealEstateService realEstateService;
    
    @Value("${real.estate.callback.url:}")
    private String callbackUrl;
    
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
            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equalsIgnoreCase(tradeStatus)
                    && !"TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
                log.info("Ignore Alipay notify, trade_status={}", tradeStatus);
                return;
            }
            
            String outTradeNo = params.get("out_trade_no");
            if (outTradeNo == null || outTradeNo.isEmpty()) {
                log.warn("Alipay notify missing out_trade_no");
                return;
            }
            
            PayOrder payOrder = payOrderMapper.selectOneByQuery(
                    com.mybatisflex.core.query.QueryWrapper.create()
                            .where(PayOrderTableDef.PAY_ORDER.ORDER_NO.eq(outTradeNo))
            );
            if (payOrder == null) {
                log.warn("Alipay notify order not found: {}", outTradeNo);
                return;
            }
            if (!"UNPAID".equalsIgnoreCase(payOrder.getStatus())) {
                log.info("Alipay notify already processed, status={}", payOrder.getStatus());
                return;
            }
            
            BigDecimal totalAmount = params.get("total_amount") != null
                    ? new BigDecimal(params.get("total_amount"))
                    : null;
            if (totalAmount != null && payOrder.getAmount() != null
                    && payOrder.getAmount().compareTo(totalAmount) != 0) {
                log.warn("Alipay notify amount mismatch, orderNo={}, expected={}, actual={}",
                        outTradeNo, payOrder.getAmount(), totalAmount);
                return;
            }
            
            // 更新订单状态
            payOrder.setStatus("PAID");
            payOrder.setUpdatedAt(new Date());
            payOrderMapper.update(payOrder);
            
            // 更新查询记录并触发查询
            RealEstateQueryRecord record = recordMapper.selectOneById(payOrder.getQueryRecordId());
            if (record == null) {
                log.warn("Query record not found for order {}", outTradeNo);
                return;
            }
            record.setPayStatus("PAID");
            record.setStatus("SUBMITTED");
            record.setUpdatedAt(new Date());
            
            RealEstateQueryRequest request = new RealEstateQueryRequest();
            request.setName(record.getName());
            request.setIdCard(record.getIdCard());
            if (callbackUrl != null && !callbackUrl.isEmpty()) {
                request.setCallBackUrl(callbackUrl);
            }
            request.setFiles(buildFileInfos(record.getId()));
            
            RealEstateQueryResponse response = realEstateService.submitRealEstateQuery(request);
            if (response.getSuccess() && response.getResult() != null) {
                record.setRequestNo(response.getResult().toString());
            }
            recordMapper.update(record);
        } catch (AlipayApiException e) {
            throw new RuntimeException("支付宝回调验签异常", e);
        }
    }
    
    private List<RealEstateQueryRequest.FileInfo> buildFileInfos(Long recordId) {
        List<RealEstateFile> storedFiles = realEstateFileMapper.selectListByQuery(
                com.mybatisflex.core.query.QueryWrapper.create()
                        .where(RealEstateFileTableDef.REAL_ESTATE_FILE.QUERY_RECORD_ID.eq(recordId))
        );
        List<RealEstateQueryRequest.FileInfo> result = new ArrayList<>();
        for (RealEstateFile file : storedFiles) {
            if (file.getFilePath() == null) {
                continue;
            }
            try {
                Path path = Paths.get(file.getFilePath());
                byte[] content = Files.readAllBytes(path);
                String base64 = Base64.getEncoder().encodeToString(content);
                RealEstateQueryRequest.FileInfo info = new RealEstateQueryRequest.FileInfo();
                info.setFileName(file.getFileName());
                info.setType(file.getFileType());
                info.setFileBase64(base64);
                result.add(info);
            } catch (Exception ex) {
                log.warn("读取文件失败，recordId={}, file={}", recordId, file.getFilePath(), ex);
            }
        }
        return result;
    }
}















