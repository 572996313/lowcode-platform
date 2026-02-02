-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- 表单/表格样式模板管理功能
-- 版本: 019
-- 说明: 添加样式模板功能，支持预设样式和交互配置
-- =====================================================

-- 1. 创建表单样式模板表
CREATE TABLE IF NOT EXISTS form_style_template (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name   VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_code   VARCHAR(100) NOT NULL COMMENT '模板编码（唯一）',
    description     VARCHAR(500) COMMENT '模板描述',
    style_config    TEXT COMMENT '样式配置JSON',
    is_system       TINYINT DEFAULT 0 COMMENT '是否系统模板',
    status          TINYINT DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    deleted         TINYINT DEFAULT 0 COMMENT '是否删除',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单样式模板表';

-- 2. 创建表格样式模板表
CREATE TABLE IF NOT EXISTS table_style_template (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name   VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_code   VARCHAR(100) NOT NULL COMMENT '模板编码（唯一）',
    description     VARCHAR(500) COMMENT '模板描述',
    style_config    TEXT COMMENT '样式配置JSON',
    is_system       TINYINT DEFAULT 0 COMMENT '是否系统模板',
    status          TINYINT DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    deleted         TINYINT DEFAULT 0 COMMENT '是否删除',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表格样式模板表';

-- 3. 为 low_form_config 添加样式模板引用字段（使用存储过程检查）
SET @dbname = DATABASE();
SET @tablename = 'low_form_config';
SET @columnname = 'style_template_id';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_SCHEMA = @dbname
      AND TABLE_NAME = @tablename
      AND COLUMN_NAME = @columnname
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN style_template_id BIGINT COMMENT ''样式模板ID'' AFTER table_id')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 4. 为 low_table_config 添加样式模板引用字段（使用存储过程检查）
SET @tablename = 'low_table_config';
SET @columnname = 'style_template_id';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      TABLE_SCHEMA = @dbname
      AND TABLE_NAME = @tablename
      AND COLUMN_NAME = @columnname
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN style_template_id BIGINT COMMENT ''样式模板ID'' AFTER source_table_id')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 5. 插入表单样式默认模板数据
INSERT INTO form_style_template (template_name, template_code, description, style_config, is_system, status, sort_order) VALUES
('默认样式', 'form_default', '标准表单样式', '{"labelWidth":100,"labelPosition":"right","layoutCols":2,"size":"default","border":false}', 1, 1, 1),
('紧凑样式', 'form_compact', '紧凑型表单', '{"labelWidth":80,"labelPosition":"right","layoutCols":3,"size":"small","border":false}', 1, 1, 2),
('纵向样式', 'form_vertical', '标签在上方', '{"labelWidth":100,"labelPosition":"top","layoutCols":1,"size":"default","border":false}', 1, 1, 3),
('大尺寸样式', 'form_large', '大尺寸组件', '{"labelWidth":120,"labelPosition":"right","layoutCols":2,"size":"large","border":false}', 1, 1, 4),
('带边框样式', 'form_bordered', '带边框表单', '{"labelWidth":100,"labelPosition":"right","layoutCols":2,"size":"default","border":true,"backgroundColor":"#f5f7fa"}', 1, 1, 5)
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 6. 插入表格样式默认模板数据
INSERT INTO table_style_template (template_name, template_code, description, style_config, is_system, status, sort_order) VALUES
('默认样式', 'table_default', '标准表格样式', '{"border":true,"stripe":true,"size":"default","pagination":true,"pageSize":10}', 1, 1, 1),
('简约样式', 'table_simple', '无边框无斑马纹', '{"border":false,"stripe":false,"size":"default","pagination":true,"pageSize":10}', 1, 1, 2),
('紧凑样式', 'table_compact', '紧凑型表格', '{"border":true,"stripe":true,"size":"small","pagination":true,"pageSize":20}', 1, 1, 3),
('大尺寸样式', 'table_large', '大尺寸表格', '{"border":true,"stripe":true,"size":"large","pagination":true,"pageSize":5}', 1, 1, 4),
('不分页样式', 'table_no_page', '不分页表格', '{"border":true,"stripe":true,"size":"default","pagination":false}', 1, 1, 5)
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- 7. 添加菜单
-- 查找低代码菜单ID
SET @lowcode_menu_id = (SELECT id FROM sys_menu WHERE menu_code = 'lowcode' LIMIT 1);

-- 如果没有找到低代码菜单，使用默认值
SET @lowcode_menu_id = IFNULL(@lowcode_menu_id, 0);

-- 表单样式模板管理
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, icon, route_path, component_path, sort_order, visible, status, deleted)
VALUES (@lowcode_menu_id, '表单样式模板', 'lowcode:form_style_template', 2, 'Brush', '/lowcode/FormStyleTemplateManage', '/views/lowcode/FormStyleTemplateManage.vue', 18, 1, 1, 0)
ON DUPLICATE KEY UPDATE component_path=VALUES(component_path);

-- 表格样式模板管理
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, icon, route_path, component_path, sort_order, visible, status, deleted)
VALUES (@lowcode_menu_id, '表格样式模板', 'lowcode:table_style_template', 2, 'Grid', '/lowcode/TableStyleTemplateManage', '/views/lowcode/TableStyleTemplateManage.vue', 19, 1, 1, 0)
ON DUPLICATE KEY UPDATE component_path=VALUES(component_path);
