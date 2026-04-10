-- ============================================================================
-- 表单配置表（重构版）
-- 用途：表单配置，定义字段与控件的映射
-- 创建时间：2026-03-04
-- ============================================================================

-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `low_form_config_new`;

-- 创建表单配置表（重构版）
CREATE TABLE `low_form_config_new` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '表单ID',
    `form_name`       VARCHAR(100) NOT NULL COMMENT '表单名称',
    `form_code`       VARCHAR(100) UNIQUE NOT NULL COMMENT '表单编码（唯一）',
    `dataset_id`      BIGINT COMMENT '关联数据集ID',
    `field_widgets`   JSON NOT NULL COMMENT '字段控件映射',
    `layout_type`     ENUM('horizontal', 'vertical', 'inline', 'grid') COMMENT '布局类型',
    `layout_config`   JSON COMMENT '布局配置',
    `form_type`       ENUM('add', 'edit', 'search', 'detail', 'dialog') COMMENT '表单类型',
    `status`          TINYINT DEFAULT 1 COMMENT '状态',
    `deleted`         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_by`       VARCHAR(64) COMMENT '创建者',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_dataset_id` (`dataset_id`),
    INDEX `idx_form_code` (`form_code`),
    CONSTRAINT `fk_form_config_dataset` FOREIGN KEY (`dataset_id`) REFERENCES `low_dataset_config` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单配置表（重构版）';

-- 插入测试数据
INSERT INTO `low_form_config_new`
(`form_name`, `form_code`, `dataset_id`, `field_widgets`, `layout_type`, `form_type`, `create_by`)
VALUES
('用户编辑表单', 'user_edit_form', 1,
 JSON_ARRAY(
    JSON_OBJECT(
        'fieldCode', 'username',
        'widgetType', 'input',
        'widgetConfig', JSON_OBJECT(
            'placeholder', '请输入用户名',
            'required', true,
            'span', 12,
            'labelWidth', 100
        )
    ),
    JSON_OBJECT(
        'fieldCode', 'nickname',
        'widgetType', 'input',
        'widgetConfig', JSON_OBJECT(
            'placeholder', '请输入昵称',
            'required', false,
            'span', 12,
            'labelWidth', 100
        )
    ),
    JSON_OBJECT(
        'fieldCode', 'email',
        'widgetType', 'input',
        'widgetConfig', JSON_OBJECT(
            'placeholder', '请输入邮箱',
            'required', false,
            'span', 12,
            'labelWidth', 100
        )
    ),
    JSON_OBJECT(
        'fieldCode', 'phone',
        'widgetType', 'input',
        'widgetConfig', JSON_OBJECT(
            'placeholder', '请输入手机号',
            'required', false,
            'span', 12,
            'labelWidth', 100
        )
    ),
    JSON_OBJECT(
        'fieldCode', 'status',
        'widgetType', 'select',
        'widgetConfig', JSON_OBJECT(
            'placeholder', '请选择状态',
            'required', false,
            'span', 12,
            'labelWidth', 100,
            'options', JSON_ARRAY(
                JSON_OBJECT('label', '启用', 'value', 1),
                JSON_OBJECT('label', '禁用', 'value', 0)
            )
        )
    )
 ),
 'horizontal',
 'edit',
 'admin');
