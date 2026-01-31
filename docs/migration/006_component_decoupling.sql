-- =============================================
-- 组件化解耦迁移脚本（完全删除旧字段版本）
-- 版本: 006
-- 日期: 2026-01-30
-- =============================================

-- 1. 备份现有数据
CREATE TABLE IF NOT EXISTS low_button_config_backup_006 AS SELECT * FROM low_button_config;
CREATE TABLE IF NOT EXISTS low_form_config_backup_006 AS SELECT * FROM low_form_config;
CREATE TABLE IF NOT EXISTS low_table_config_backup_006 AS SELECT * FROM low_table_config;

-- 2. 添加新字段到按钮配置表
ALTER TABLE low_button_config
ADD COLUMN component_category VARCHAR(20) DEFAULT 'business' COMMENT '组件分类: common=通用组件库, business=业务组件库',
ADD COLUMN component_tags TEXT COMMENT '组件标签JSON: ["system","user","create"]';

-- 3. 添加新字段到表单配置表
ALTER TABLE low_form_config
ADD COLUMN component_category VARCHAR(20) DEFAULT 'business' COMMENT '组件分类: common=通用组件库, business=业务组件库',
ADD COLUMN component_tags TEXT COMMENT '组件标签JSON: ["system","user"]';

-- 4. 添加新字段到表格配置表
ALTER TABLE low_table_config
ADD COLUMN component_category VARCHAR(20) DEFAULT 'business' COMMENT '组件分类: common=通用组件库, business=业务组件库',
ADD COLUMN component_tags TEXT COMMENT '组件标签JSON: ["system","list"]';

-- 5. 迁移现有数据为业务组件
UPDATE low_button_config
SET component_category = 'business',
    component_tags = '["custom","legacy"]'
WHERE component_category = 'business';

UPDATE low_form_config
SET component_category = 'business',
    component_tags = '["custom","legacy"]'
WHERE component_category = 'business';

UPDATE low_table_config
SET component_category = 'business',
    component_tags = '["custom","legacy"]'
WHERE component_category = 'business';

-- 6. 创建索引以提高查询性能
CREATE INDEX idx_btn_category ON low_button_config(component_category);
CREATE INDEX idx_form_category ON low_form_config(component_category);
CREATE INDEX idx_table_category ON low_table_config(component_category);

-- 7. 验证数据迁移
SELECT 'button' as type, COUNT(*) as total,
  SUM(CASE WHEN component_category = 'common' THEN 1 ELSE 0 END) as common_count,
  SUM(CASE WHEN component_category = 'business' THEN 1 ELSE 0 END) as business_count
FROM low_button_config
UNION ALL
SELECT 'form' as type, COUNT(*) as total,
  SUM(CASE WHEN component_category = 'common' THEN 1 ELSE 0 END) as common_count,
  SUM(CASE WHEN component_category = 'business' THEN 1 ELSE 0 END) as business_count
FROM low_form_config
UNION ALL
SELECT 'table' as type, COUNT(*) as total,
  SUM(CASE WHEN component_category = 'common' THEN 1 ELSE 0 END) as common_count,
  SUM(CASE WHEN component_category = 'business' THEN 1 ELSE 0 END) as business_count
FROM low_table_config;
