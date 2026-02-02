-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- 迁移脚本 017: 添加页面布局和字段绑定表
-- =====================================================

-- 1. 创建页面布局表
CREATE TABLE IF NOT EXISTS page_layout (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    layout_name     VARCHAR(100) NOT NULL COMMENT '布局名称',
    layout_code     VARCHAR(100) NOT NULL COMMENT '布局编码（唯一）',
    layout_type     VARCHAR(50) NOT NULL COMMENT '布局类型(top-bottom/left-right/tree-table/tabs)',
    area_config     TEXT COMMENT '区域配置JSON',
    published       TINYINT DEFAULT 0 COMMENT '是否已发布',
    route_path      VARCHAR(200) COMMENT '路由路径',
    menu_id         BIGINT COMMENT '关联菜单ID',
    status          TINYINT DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
    deleted         TINYINT DEFAULT 0 COMMENT '是否删除（1=删除，0=存在）',
    create_by       VARCHAR(50) COMMENT '创建者',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_layout_code (layout_code),
    KEY idx_layout_type (layout_type),
    KEY idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面布局配置表';

-- 2. 创建表单字段绑定表
CREATE TABLE IF NOT EXISTS form_field_binding (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '绑定ID',
    form_id         BIGINT NOT NULL COMMENT '表单ID（关联low_form_config.id）',
    field_id        BIGINT NOT NULL COMMENT '字段ID（关联db_table_field.id）',
    widget_type     VARCHAR(50) NOT NULL COMMENT '控件类型',
    widget_config   TEXT COMMENT '控件配置JSON',
    span            INT DEFAULT 12 COMMENT '栅格占位格数',
    required        TINYINT DEFAULT 0 COMMENT '是否必填',
    visible         TINYINT DEFAULT 1 COMMENT '是否可见',
    readonly        TINYINT DEFAULT 0 COMMENT '是否只读',
    disabled        TINYINT DEFAULT 0 COMMENT '是否禁用',
    sort_order      INT DEFAULT 0 COMMENT '排序序号',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_form_field (form_id, field_id),
    KEY idx_form_id (form_id),
    KEY idx_field_id (field_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单字段绑定表';

-- 3. 创建表格列绑定表
CREATE TABLE IF NOT EXISTS table_column_binding (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '绑定ID',
    table_config_id BIGINT NOT NULL COMMENT '表格配置ID（关联low_table_config.id）',
    field_id        BIGINT NOT NULL COMMENT '字段ID（关联db_table_field.id）',
    column_label    VARCHAR(128) COMMENT '列标题',
    column_type     VARCHAR(50) DEFAULT 'text' COMMENT '列类型（text/image/tag/switch/dict/link等）',
    width           INT COMMENT '列宽度',
    min_width       INT COMMENT '最小列宽度',
    sortable        TINYINT DEFAULT 0 COMMENT '是否可排序',
    fixed           VARCHAR(20) COMMENT '固定列（left/right）',
    align           VARCHAR(20) DEFAULT 'center' COMMENT '对齐方式',
    show_overflow    TINYINT DEFAULT 1 COMMENT '是否溢出省略',
    formatter_config TEXT COMMENT '格式化配置JSON',
    sort_order      INT DEFAULT 0 COMMENT '排序序号',
    visible         TINYINT DEFAULT 1 COMMENT '是否可见',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_table_field (table_config_id, field_id),
    KEY idx_table_config_id (table_config_id),
    KEY idx_field_id (field_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表格列绑定表';

-- 4. 修改现有表 - 为low_form_config添加table_id字段
-- 检查字段是否存在，不存在则添加
SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE low_form_config ADD COLUMN table_id BIGINT COMMENT ''关联数据库表ID（关联db_table.id）'' AFTER form_code',
        'SELECT ''table_id column already exists'' AS message'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'lowcode_platform'
    AND TABLE_NAME = 'low_form_config'
    AND COLUMN_NAME = 'table_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. 修改现有表 - 为low_table_config添加source_table_id字段
SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE low_table_config ADD COLUMN source_table_id BIGINT COMMENT ''关联数据库表ID（关联db_table.id）'' AFTER table_code',
        'SELECT ''source_table_id column already exists'' AS message'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'lowcode_platform'
    AND TABLE_NAME = 'low_table_config'
    AND COLUMN_NAME = 'source_table_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 6. 添加索引（如果不存在）
SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE low_form_config ADD INDEX idx_table_id (table_id)',
        'SELECT ''idx_table_id index already exists'' AS message'
    )
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = 'lowcode_platform'
    AND TABLE_NAME = 'low_form_config'
    AND INDEX_NAME = 'idx_table_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE low_table_config ADD INDEX idx_source_table_id (source_table_id)',
        'SELECT ''idx_source_table_id index already exists'' AS message'
    )
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = 'lowcode_platform'
    AND TABLE_NAME = 'low_table_config'
    AND INDEX_NAME = 'idx_source_table_id'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- 迁移完成
-- =====================================================
