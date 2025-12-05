package org.yun.dao.config;

import com.mybatisflex.core.audit.AuditMessage;
import com.mybatisflex.core.audit.MessageReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * SQL审计日志消息报告器
 * 将SQL审计日志输出到专门的日志文件，而不是控制台
 */
public class SqlAuditMessageReporter implements MessageReporter {

    private static final Logger sqlAuditLogger = LoggerFactory.getLogger("SQL_AUDIT");

    @Override
    public void sendMessages(List<AuditMessage> messages) {
        for (AuditMessage message : messages) {
            sqlAuditLogger.info("Sql Audit: {}", message);
        }
    }
}


