-- 更新 low_button_config 表，添加 form_id 和 table_id 字段
-- 执行前请先备份数据库

USE lowcode_platform;

-- 添加 form_id 字段
ALTER TABLE low_button_config ADD COLUMN form_id BIGINT COMMENT '关联表单ID' AFTER page_id;

-- 添加 table_id 字段
ALTER TABLE low_button_config ADD COLUMN table_id BIGINT COMMENT '关联表格ID' AFTER form_id;

-- 更新 position 字段的注释
ALTER TABLE low_button_config MODIFY COLUMN position VARCHAR(50) COMMENT '位置(toolbar/row/form/dialog/footer)';

-- 添加索引
CREATE INDEX idx_form_id ON low_button_config(form_id);
CREATE INDEX idx_table_id ON low_button_config(table_id);

-- 验证表结构
DESC low_button_config;

SELECT '更新完成！' AS message;
