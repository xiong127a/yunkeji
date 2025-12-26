-- ============================================
-- 修复 Flyway 迁移校验和不匹配问题
-- ============================================
-- 问题：修改了 V1__Create_tables.sql 文件（将"不动产"改为"大数据"）
-- 导致文件校验和与数据库记录不匹配
-- 
-- 错误信息：
-- Migration checksum mismatch for migration version 1
-- -> Applied to database : -160953704
-- -> Resolved locally    : -203041221
-- ============================================

-- 方法1: 直接更新校验和（推荐，最简单）
-- 将数据库中的校验和更新为当前文件的实际校验和
UPDATE flyway_schema_history 
SET checksum = -203041221 
WHERE version = '1';

-- 验证更新结果
SELECT version, description, type, installed_on, success, checksum 
FROM flyway_schema_history 
WHERE version = '1';

-- ============================================
-- 方法2: 如果方法1不行，使用 Flyway repair（需要命令行工具）
-- ============================================
-- 在项目根目录执行：
-- mvn flyway:repair
-- 
-- 或者使用 Flyway 命令行工具：
-- flyway repair -url=jdbc:mysql://localhost:3306/yunkeji -user=root -password=your_password
-- ============================================

-- ============================================
-- 方法3: 删除记录重新运行（谨慎使用，仅用于开发环境）
-- ============================================
-- 警告：这会删除 V1 迁移记录，重启应用时会重新执行 V1 迁移
-- 如果 V1 迁移包含数据初始化（如 INSERT），可能会导致数据重复
-- 
-- DELETE FROM flyway_schema_history WHERE version = '1';
-- 然后重启应用
-- ============================================

-- 查看所有迁移记录
SELECT version, description, type, installed_on, success, checksum 
FROM flyway_schema_history 
ORDER BY installed_rank;

