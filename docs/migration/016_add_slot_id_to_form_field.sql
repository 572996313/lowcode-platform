-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- 表单设计器重构 - 添加模板槽位ID字段
-- =====================================================

-- 1. 为 low_form_field 表添加 slot_id 字段（如果已存在会报错，可以忽略）
-- ALTER TABLE `low_form_field`
-- ADD COLUMN `slot_id` VARCHAR(50) NULL COMMENT '模板槽位ID（用于基于模板的表单）' AFTER `form_id`;

-- 2. 为 low_form_config 表添加 template_code 字段（如果已存在会报错，可以忽略）
-- ALTER TABLE `low_form_config`
-- ADD COLUMN `template_code` VARCHAR(50) NULL COMMENT '表单模板编码' AFTER `form_code`;

-- 3. 添加索引以提高查询性能（如果已存在会报错，可以忽略）
-- ALTER TABLE `low_form_field`
-- ADD INDEX `idx_slot_id` (`slot_id`);

-- 4. 验证表结构
SELECT 'low_form_field 表结构 (检查 slot_id 字段)' AS info;
DESC low_form_field;

SELECT 'low_form_config 表结构 (检查 template_code 字段)' AS info;
DESC low_form_config;

-- 5. 验证字符集
SELECT
    TABLE_NAME,
    TABLE_COLLATION,
    TABLE_COMMENT
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'lowcode_platform'
  AND TABLE_NAME IN ('low_form_field', 'low_form_config');
