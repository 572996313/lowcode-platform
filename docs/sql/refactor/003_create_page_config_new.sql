-- ============================================================================
-- 页面配置表（重构版）
-- 用途：前端页面配置，引用数据集和定义展示字段
-- 创建时间：2026-03-04
-- ============================================================================

-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `low_page_config_new`;

-- 创建页面配置表（重构版）
CREATE TABLE `low_page_config_new` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '页面ID',
    `page_name`       VARCHAR(100) NOT NULL COMMENT '页面名称',
    `page_code`       VARCHAR(100) UNIQUE NOT NULL COMMENT '页面编码（唯一）',
    `dataset_id`      BIGINT COMMENT '关联数据集ID',
    `display_fields`  JSON COMMENT '要显示的字段列表',
    `layout_type`     ENUM('tree-table', 'top-bottom', 'free-canvas', 'tabs') COMMENT '布局类型',
    `layout_config`   JSON COMMENT '布局配置',
    `route_path`      VARCHAR(200) COMMENT '路由路径',
    `published`       TINYINT DEFAULT 0 COMMENT '是否已发布',
    `config_json`     JSON COMMENT '扩展配置',
    `status`          TINYINT DEFAULT 1 COMMENT '状态',
    `deleted`         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_by`       VARCHAR(64) COMMENT '创建者',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_dataset_id` (`dataset_id`),
    INDEX `idx_page_code` (`page_code`),
    INDEX `idx_published` (`published`),
    CONSTRAINT `fk_page_config_dataset` FOREIGN KEY (`dataset_id`) REFERENCES `low_dataset_config` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页面配置表（重构版）';

-- 插入测试数据
INSERT INTO `low_page_config_new`
(`page_name`, `page_code`, `dataset_id`, `display_fields`, `layout_type`, `route_path`, `published`, `create_by`)
VALUES
('用户列表页面', 'user_list_page', 1,
 JSON_ARRAY(
    JSON_OBJECT('fieldCode', 'id', 'visible', true, 'order', 1, 'label', 'ID'),
    JSON_OBJECT('fieldCode', 'username', 'visible', true, 'order', 2, 'label', '用户名'),
    JSON_OBJECT('fieldCode', 'nickname', 'visible', true, 'order', 3, 'label', '昵称'),
    JSON_OBJECT('fieldCode', 'email', 'visible', true, 'order', 4, 'label', '邮箱'),
    JSON_OBJECT('fieldCode', 'phone', 'visible', true, 'order', 5, 'label', '手机号'),
    JSON_OBJECT('fieldCode', 'status', 'visible', true, 'order', 6, 'label', '状态')
 ),
 'top-bottom',
 '/user/list',
 1,
 'admin');
