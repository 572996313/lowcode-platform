-- 页面模板配置功能数据库迁移
-- 执行日期: 2026-01-30

-- 添加 config_template 字段到 low_page_template 表（如果不存在）
ALTER TABLE low_page_template
ADD COLUMN config_template TEXT COMMENT '模板配置JSON(v2版本)' AFTER config_json;
