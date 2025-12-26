-- 删除失败的 V5 迁移记录
-- 执行此脚本后，重启应用，Flyway 会重新执行 V5 迁移

DELETE FROM flyway_schema_history 
WHERE version = '5';

-- 验证删除结果
SELECT * FROM flyway_schema_history 
ORDER BY installed_rank;

