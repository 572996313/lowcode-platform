-- 页面配置发布功能数据库迁移
-- 执行日期: 2026-01-29

-- 1. 页面配置表新增路由发布字段
ALTER TABLE low_page_config
ADD COLUMN route_path VARCHAR(200) COMMENT '路由路径',
ADD COLUMN published TINYINT DEFAULT 0 COMMENT '是否已发布(0否 1是)',
ADD COLUMN publish_time DATETIME COMMENT '发布时间';

-- 2. 添加索引优化查询性能
CREATE INDEX idx_route_path ON low_page_config(route_path);
CREATE INDEX idx_published ON low_page_config(published);

-- 3. 添加页面状态字段（如果不存在）
ALTER TABLE low_page_config
MODIFY COLUMN status TINYINT DEFAULT 1 COMMENT '状态(0停用 1启用)';
