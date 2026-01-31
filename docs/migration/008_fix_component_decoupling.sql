-- =============================================
-- 修复组件解耦迁移脚本
-- 版本: 008
-- 日期: 2026-01-31
-- =============================================

-- 1. 添加 status 字段
ALTER TABLE low_button_config
ADD COLUMN status TINYINT(1) DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用';

ALTER TABLE low_form_config
ADD COLUMN status TINYINT(1) DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用';

ALTER TABLE low_table_config
ADD COLUMN status TINYINT(1) DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用';

-- 2. 删除旧关联字段（完全删除）
ALTER TABLE low_button_config DROP COLUMN page_id;
ALTER TABLE low_button_config DROP COLUMN form_id;
ALTER TABLE low_button_config DROP COLUMN table_id;

ALTER TABLE low_form_config DROP COLUMN page_id;
ALTER TABLE low_table_config DROP COLUMN page_id;

-- 3. 创建索引
CREATE INDEX idx_btn_category ON low_button_config(component_category);
CREATE INDEX idx_btn_status ON low_button_config(status);

CREATE INDEX idx_form_category ON low_form_config(component_category);
CREATE INDEX idx_form_status ON low_form_config(status);

CREATE INDEX idx_table_category ON low_table_config(component_category);
CREATE INDEX idx_table_status ON low_table_config(status);

-- 4. 更新现有按钮的状态
UPDATE low_button_config SET status = 1 WHERE status IS NULL;
UPDATE low_form_config SET status = 1 WHERE status IS NULL;
UPDATE low_table_config SET status = 1 WHERE status IS NULL;

-- 5. 验证数据
SELECT 'button' as type, COUNT(*) as total,
  SUM(CASE WHEN component_category = 'common' THEN 1 ELSE 0 END) as common_count
FROM low_button_config
UNION ALL
SELECT 'form' as type, COUNT(*) as total,
  SUM(CASE WHEN component_category = 'common' THEN 1 ELSE 0 END) as common_count
FROM low_form_config
UNION ALL
SELECT 'table' as type, COUNT(*) as total,
  SUM(CASE WHEN component_category = 'common' THEN 1 ELSE 0 END) as common_count
FROM low_table_config;
