package org.yun.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yun.common.dto.ContactInfoResponse;

/**
 * 客服联系方式控制器
 */
@RestController
@RequestMapping("/api/contact")
@Tag(name = "客服信息", description = "客服联系方式相关API")
public class ContactController {
    
    @Value("${contact.wechat:}")
    private String wechat;
    
    @Value("${contact.phone:}")
    private String phone;
    
    @Value("${contact.work-hours:周一至周五 9:00-18:00}")
    private String workHours;
    
    /**
     * 获取客服联系方式
     */
    @GetMapping("/info")
    @Operation(summary = "获取客服联系方式")
    public ContactInfoResponse getContactInfo() {
        ContactInfoResponse response = new ContactInfoResponse();
        response.setWechat(wechat);
        response.setPhone(phone);
        response.setWorkHours(workHours);
        return response;
    }
}









