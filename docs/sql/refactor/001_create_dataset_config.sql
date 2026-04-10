-- ============================================================================
-- 数据集配置表
-- 用途：定义数据来源（表、视图、SQL查询、API接口）
-- 创建时间：2026-03-04
-- ============================================================================

-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `low_dataset_config`;

-- 创建数据集配置表
CREATE TABLE `low_dataset_config` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '数据集ID',
    `dataset_name`    VARCHAR(100) NOT NULL COMMENT '数据集名称',
    `dataset_code`    VARCHAR(100) UNIQUE NOT NULL COMMENT '数据集编码（唯一）',
    `source_type`     ENUM('table', 'view', 'sql', 'api') NOT NULL COMMENT '数据源类型',
    `source_config`   JSON NOT NULL COMMENT '数据源配置',
    `primary_key`     VARCHAR(50) COMMENT '主键字段名',
    `description`     TEXT COMMENT '描述',
    `status`          TINYINT DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
    `deleted`         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_by`       VARCHAR(64) COMMENT '创建者',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_source_type` (`source_type`),
    INDEX `idx_dataset_code` (`dataset_code`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据集配置表';

-- 插入测试数据
INSERT INTO `low_dataset_config` (`dataset_name`, `dataset_code`, `source_type`, `source_config`, `primary_key`, `description`, `create_by`) VALUES
('用户数据集', 'user_dataset', 'table',
 JSON_OBJECT('tableName', 'sys_user', 'schema', 'lowcode_platform'),
 'id', '系统用户数据集', 'admin'),

('菜单数据集', 'menu_dataset', 'table',
 JSON_OBJECT('tableName', 'sys_menu', 'schema', 'lowcode_platform'),
 'id', '系统菜单数据集', 'admin');
