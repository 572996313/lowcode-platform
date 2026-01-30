-- 页面配置模板功能数据库迁移
-- 执行日期: 2026-01-30

-- 添加 config_template 和 config_version 字段（如果不存在）
ALTER TABLE low_page_config
ADD COLUMN config_template TEXT COMMENT '模板配置JSON(v2版本)' AFTER config_json,
ADD COLUMN config_version INT DEFAULT 1 COMMENT '配置版本';
