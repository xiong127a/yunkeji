-- ============================================
-- 手动修复 Flyway 校验和不匹配问题
-- ============================================
-- 执行步骤：
-- 1. 连接到数据库 yunkeji
-- 2. 执行下面的 UPDATE 语句
-- 3. 重启应用
-- ============================================

-- 查看当前 V1 迁移的校验和
SELECT version, description, checksum, installed_on 
FROM flyway_schema_history 
WHERE version = '1';

-- 更新 V1 迁移的校验和为新的值（-203041221）
UPDATE flyway_schema_history 
SET checksum = -203041221 
WHERE version = '1';

-- 验证更新结果
SELECT version, description, checksum, installed_on 
FROM flyway_schema_history 
WHERE version = '1';

-- ============================================
-- 如果上面的 UPDATE 没有找到记录，尝试：
-- ============================================
-- UPDATE flyway_schema_history 
-- SET checksum = -203041221 
-- WHERE installed_rank = 1;

