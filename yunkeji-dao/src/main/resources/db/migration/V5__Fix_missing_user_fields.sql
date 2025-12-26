-- 修复缺失的用户表字段（兜底方案）
-- 注意：V3 和 V4 迁移应该已经添加了这些字段
-- 此脚本作为兜底，如果字段已存在会报错，但可以忽略
-- 
-- 由于 Druid SQL 防火墙限制，不能使用存储过程
-- 如果字段已存在，执行此脚本会报错，需要手动删除失败的迁移记录

-- 这些字段应该已经通过 V3 和 V4 添加了，所以这里留空
-- 如果确实需要添加缺失的字段，请手动执行以下 SQL：
-- 
-- ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' AFTER `depth`;
-- ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `kyc_status` VARCHAR(20) NOT NULL DEFAULT 'APPROVED' AFTER `status`;
-- ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `trusted` TINYINT(1) NOT NULL DEFAULT 0 AFTER `kyc_status`;
-- ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `id_card_front_url` VARCHAR(255) NULL AFTER `trusted`;
-- ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `id_card_back_url` VARCHAR(255) NULL AFTER `id_card_front_url`;
-- ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `business_license_url` VARCHAR(255) NULL AFTER `id_card_back_url`;

-- 由于 MySQL 5.7 不支持 IF NOT EXISTS，这里使用空脚本
-- 如果字段缺失，请手动执行上面的 ALTER TABLE 语句

SELECT 1;
