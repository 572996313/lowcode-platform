-- =============================================
-- 修复 low_button_config 表缺少 create_by 字段
-- =============================================

USE lowcode_platform;

-- 添加 create_by 字段
ALTER TABLE low_button_config
ADD COLUMN create_by VARCHAR(50) COMMENT '创建者' AFTER deleted;

-- 验证字段是否添加成功
DESCRIBE low_button_config;
