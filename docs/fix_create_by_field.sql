-- =============================================
-- 修复所有缺少 create_by 字段的表
-- =============================================

USE lowcode_platform;

-- 1. 修复 low_button_config 表（当前报错的表）
ALTER TABLE low_button_config
ADD COLUMN IF NOT EXISTS create_by VARCHAR(64) COMMENT '创建者' AFTER deleted;

-- 2. 检查并修复 low_form_field 表
-- ALTER TABLE low_form_field
-- ADD COLUMN IF NOT EXISTS create_by VARCHAR(64) COMMENT '创建者' AFTER deleted;

-- 3. 检查并修复 low_table_column 表
-- ALTER TABLE low_table_column
-- ADD COLUMN IF NOT EXISTS create_by VARCHAR(64) COMMENT '创建者' AFTER deleted;

-- 4. 检查并修复 low_search_config 表
-- ALTER TABLE low_search_config
-- ADD COLUMN IF NOT EXISTS create_by VARCHAR(64) COMMENT '创建者' AFTER deleted;

-- 5. 检查并修复 sys_dict_data 表
-- ALTER TABLE sys_dict_data
-- ADD COLUMN IF NOT EXISTS create_by VARCHAR(64) COMMENT '创建者' AFTER deleted;

-- 验证修改
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    COLUMN_TYPE,
    COLUMN_COMMENT
FROM
    INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'lowcode_platform'
    AND TABLE_NAME = 'low_button_config'
    AND COLUMN_NAME = 'create_by';
