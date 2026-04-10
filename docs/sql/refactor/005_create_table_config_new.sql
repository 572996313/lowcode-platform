-- ============================================================================
-- 表格配置表（重构版）
-- 用途：表格配置，定义列的展示方式
-- 创建时间：2026-03-04
-- ============================================================================

-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `low_table_config_new`;

-- 创建表格配置表（重构版）
CREATE TABLE `low_table_config_new` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '表格ID',
    `table_name`      VARCHAR(100) NOT NULL COMMENT '表格名称',
    `table_code`      VARCHAR(100) UNIQUE NOT NULL COMMENT '表格编码（唯一）',
    `dataset_id`      BIGINT COMMENT '关联数据集ID',
    `column_display`  JSON NOT NULL COMMENT '列展示配置',
    `table_config`    JSON COMMENT '表格配置（分页、边框等）',
    `status`          TINYINT DEFAULT 1 COMMENT '状态',
    `deleted`         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_by`       VARCHAR(64) COMMENT '创建者',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_dataset_id` (`dataset_id`),
    INDEX `idx_table_code` (`table_code`),
    CONSTRAINT `fk_table_config_dataset` FOREIGN KEY (`dataset_id`) REFERENCES `low_dataset_config` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表格配置表（重构版）';

-- 插入测试数据
INSERT INTO `low_table_config_new`
(`table_name`, `table_code`, `dataset_id`, `column_display`, `table_config`, `create_by`)
VALUES
('用户列表表格', 'user_list_table', 1,
 JSON_ARRAY(
    JSON_OBJECT('fieldCode', 'id', 'label', 'ID', 'width', 80, 'align', 'center', 'sortable', false, 'fixed', 'left'),
    JSON_OBJECT('fieldCode', 'username', 'label', '用户名', 'width', 150, 'align', 'left', 'sortable', true),
    JSON_OBJECT('fieldCode', 'nickname', 'label', '昵称', 'width', 120, 'align', 'left', 'sortable', false),
    JSON_OBJECT('fieldCode', 'email', 'label', '邮箱', 'width', 200, 'align', 'left', 'sortable', false),
    JSON_OBJECT('fieldCode', 'phone', 'label', '手机号', 'width', 150, 'align', 'center', 'sortable', false),
    JSON_OBJECT('fieldCode', 'status', 'label', '状态', 'width', 100, 'align', 'center', 'sortable', false, 'formatter', 'tag',
        'formatterConfig', JSON_OBJECT(
            'typeMap', JSON_OBJECT('1', 'success', '0', 'info'),
            'labelMap', JSON_OBJECT('1', '启用', '0', '禁用')
        )
    ),
    JSON_OBJECT('fieldCode', 'create_time', 'label', '创建时间', 'width', 180, 'align', 'center', 'sortable', false, 'formatter', 'datetime')
 ),
 JSON_OBJECT(
    'pagination', true,
    'pageSize', 20,
    'pageSizes', JSON_ARRAY(10, 20, 50, 100),
    'border', true,
    'stripe', true,
    'selection', true,
    'selectionType', 'multiple',
    'showIndex', true,
    'height', 'auto'
 ),
 'admin');
