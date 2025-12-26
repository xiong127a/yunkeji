-- ============================================
-- 完整数据库修复脚本
-- ============================================
-- 用途：修复 Flyway 迁移问题和缺失字段
-- 数据库：yunkeji
-- 执行顺序：按顺序执行所有SQL语句
-- ============================================

-- ============================================
-- 第一部分：修复 Flyway 校验和（已完成，可跳过）
-- ============================================
-- 如果 V1 迁移的校验和已经是 -203041221，可以跳过这部分

-- 查看当前 V1 迁移的校验和
SELECT version, description, checksum, installed_on 
FROM flyway_schema_history 
WHERE version = '1';

-- 更新 V1 迁移的校验和（如果还没更新）
UPDATE flyway_schema_history 
SET checksum = -203041221 
WHERE version = '1' AND checksum != -203041221;

-- 验证更新结果
SELECT version, description, checksum, installed_on 
FROM flyway_schema_history 
WHERE version = '1';

-- ============================================
-- 第二部分：删除失败的 V5 迁移记录
-- ============================================

-- 查看所有迁移记录
SELECT version, description, type, installed_on, success, checksum 
FROM flyway_schema_history 
ORDER BY installed_rank;

-- 删除失败的 V5 迁移记录（如果存在）
DELETE FROM flyway_schema_history 
WHERE version = '5';

-- 验证删除结果
SELECT version, description, type, installed_on, success, checksum 
FROM flyway_schema_history 
ORDER BY installed_rank;

-- ============================================
-- 第三部分：检查并添加缺失的用户表字段
-- ============================================
-- 注意：V3 和 V4 迁移应该已经添加了这些字段
-- 如果字段已存在，执行 ALTER TABLE 会报错，可以忽略

-- 检查当前 user 表的结构
DESC `user`;

-- 检查 status 字段是否存在
SELECT COUNT(*) as status_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'user'
  AND COLUMN_NAME = 'status';

-- 检查 kyc_status 字段是否存在
SELECT COUNT(*) as kyc_status_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'user'
  AND COLUMN_NAME = 'kyc_status';

-- 检查 trusted 字段是否存在
SELECT COUNT(*) as trusted_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'user'
  AND COLUMN_NAME = 'trusted';

-- 检查 id_card_front_url 字段是否存在
SELECT COUNT(*) as id_card_front_url_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'user'
  AND COLUMN_NAME = 'id_card_front_url';

-- 检查 id_card_back_url 字段是否存在
SELECT COUNT(*) as id_card_back_url_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'user'
  AND COLUMN_NAME = 'id_card_back_url';

-- 检查 business_license_url 字段是否存在
SELECT COUNT(*) as business_license_url_exists
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'user'
  AND COLUMN_NAME = 'business_license_url';

-- ============================================
-- 第四部分：添加缺失的字段（仅在字段不存在时执行）
-- ============================================
-- 注意：如果字段已存在，执行会报错 "Duplicate column name"，可以忽略

-- 添加 status 字段（如果不存在）
-- 如果上面的检查显示 status_exists = 0，则执行：
-- ALTER TABLE `user` ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER depth;

-- 添加 kyc_status 字段（如果不存在）
-- 如果上面的检查显示 kyc_status_exists = 0，则执行：
-- ALTER TABLE `user` ADD COLUMN kyc_status VARCHAR(20) NOT NULL DEFAULT 'APPROVED' AFTER status;

-- 添加 trusted 字段（如果不存在）
-- 如果上面的检查显示 trusted_exists = 0，则执行：
-- ALTER TABLE `user` ADD COLUMN trusted TINYINT(1) NOT NULL DEFAULT 0 AFTER kyc_status;

-- 添加 id_card_front_url 字段（如果不存在）
-- 如果上面的检查显示 id_card_front_url_exists = 0，则执行：
-- ALTER TABLE `user` ADD COLUMN id_card_front_url VARCHAR(255) NULL AFTER trusted;

-- 添加 id_card_back_url 字段（如果不存在）
-- 如果上面的检查显示 id_card_back_url_exists = 0，则执行：
-- ALTER TABLE `user` ADD COLUMN id_card_back_url VARCHAR(255) NULL AFTER id_card_front_url;

-- 添加 business_license_url 字段（如果不存在）
-- 如果上面的检查显示 business_license_url_exists = 0，则执行：
-- ALTER TABLE `user` ADD COLUMN business_license_url VARCHAR(255) NULL AFTER id_card_back_url;

-- ============================================
-- 第五部分：最终验证
-- ============================================

-- 查看所有迁移记录（最终状态）
SELECT version, description, type, installed_on, success, checksum 
FROM flyway_schema_history 
ORDER BY installed_rank;

-- 查看 user 表的完整结构
DESC `user`;

-- ============================================
-- 执行说明：
-- ============================================
-- 1. 按顺序执行第一部分到第三部分的检查语句
-- 2. 根据检查结果，决定是否需要执行第四部分的 ALTER TABLE 语句
-- 3. 如果字段已存在（COUNT(*) = 1），则跳过对应的 ALTER TABLE
-- 4. 如果字段不存在（COUNT(*) = 0），则执行对应的 ALTER TABLE
-- 5. 执行完成后，重启应用，Flyway 会自动执行 V5 迁移
-- ============================================

