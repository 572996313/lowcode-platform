-- 修复 low_button_config 表的字符集问题
-- 执行方式: docker exec -i wizardly_yalow mysql -uroot -p123456 --default-character-set=utf8mb4 lowcode_platform < docs/migration/008_fix_button_charset.sql

-- 1. 备份现有数据
CREATE TABLE IF NOT EXISTS low_button_config_backup_20260131 AS SELECT * FROM low_button_config;

-- 2. 删除旧表
DROP TABLE IF EXISTS low_button_config;

-- 3. 重新创建表（使用正确的字符集）
CREATE TABLE low_button_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '按钮ID',
    page_id BIGINT COMMENT '所属页面ID',
    form_id BIGINT COMMENT '关联表单ID',
    table_id BIGINT COMMENT '关联表格ID',
    button_name VARCHAR(100) COMMENT '按钮名称',
    button_code VARCHAR(100) COMMENT '按钮编码',
    component_category VARCHAR(20) DEFAULT 'business' COMMENT '组件分类: common=通用组件库, business=业务组件库',
    component_tags TEXT COMMENT '组件标签JSON: ["system","user","create"]',
    position VARCHAR(50) COMMENT '位置(toolbar/row/form/dialog/footer)',
    button_type VARCHAR(50) DEFAULT 'default' COMMENT '按钮类型(primary/success/warning/danger/info/default)',
    button_size VARCHAR(20) DEFAULT 'default' COMMENT '按钮尺寸(large/default/small)',
    icon VARCHAR(100) COMMENT '图标',
    is_plain TINYINT DEFAULT 0 COMMENT '是否朴素按钮',
    is_round TINYINT DEFAULT 0 COMMENT '是否圆角按钮',
    is_circle TINYINT DEFAULT 0 COMMENT '是否圆形按钮',
    is_loading TINYINT DEFAULT 0 COMMENT '是否加载中',
    is_disabled TINYINT DEFAULT 0 COMMENT '是否禁用',
    action_type VARCHAR(50) COMMENT '动作类型(api/dialog/drawer/route/link/custom/confirm)',
    action_config TEXT COMMENT '动作配置JSON',
    confirm_config TEXT COMMENT '确认框配置JSON',
    perms VARCHAR(100) COMMENT '权限标识',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示',
    show_condition TEXT COMMENT '显示条件表达式',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 1=启用, 0=禁用',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_page_id (page_id),
    INDEX idx_form_id (form_id),
    INDEX idx_table_id (table_id),
    INDEX idx_button_code (button_code),
    INDEX idx_btn_category (component_category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='按钮配置表';

-- 4. 恢复数据（忽略新字段的值，它们会被设置为默认值）
INSERT INTO low_button_config (
    id, button_name, button_code, component_category, component_tags,
    position, button_type, button_size, icon, is_plain, is_round, is_circle,
    is_loading, is_disabled, action_type, action_config, confirm_config,
    perms, sort_order, is_visible, show_condition, deleted, create_by, create_time, update_time
)
SELECT
    id, button_name, button_code,
    COALESCE(component_category, 'business') as component_category,
    component_tags,
    position, button_type, button_size, icon, is_plain, is_round, is_circle,
    is_loading, is_disabled, action_type, action_config, confirm_config,
    perms, sort_order, is_visible, show_condition, deleted, create_by, create_time, update_time
FROM low_button_config_backup_20260131;

-- 5. 设置 AUTO_INCREMENT 的起始值
SET @max_id = (SELECT MAX(id) FROM low_button_config);
SET @sql = CONCAT('ALTER TABLE low_button_config AUTO_INCREMENT = ', IFNULL(@max_id, 0) + 1);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 6. 验证数据
SELECT COUNT(*) as '总记录数' FROM low_button_config;
SELECT COUNT(*) as '备份记录数' FROM low_button_config_backup_20260131;
