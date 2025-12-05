package org.yun.dao.config;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * MyBatis Flex 配置类
 * 用于配置 MyBatis Flex 相关的全局设置
 *
 * @author 任相鹏
 * @since 1.0.0
 */
@Configuration
public class MyBatisFlexConfig implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        // 可选：全局配置逻辑删除字段名（如果所有表都使用相同的字段名）
        // globalConfig.setLogicDeleteColumn("deleted");

        // 雪花算法ID生成器已内置，无需额外配置
        // 直接在实体类使用: @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    }

    @PostConstruct
    public void initAuditManager() {
        // 开启SQL审计功能
        AuditManager.setAuditEnable(true);
        // 设置自定义的消息报告器，将SQL审计日志输出到专属日志文件
        AuditManager.setMessageReporter(new SqlAuditMessageReporter());
    }
}