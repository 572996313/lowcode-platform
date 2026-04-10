-- ============================================================================
-- 数据字典表
-- 用途：管理系统中的数据字典，支持字段关联字典
-- 创建时间：2026-03-04
-- ============================================================================

-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `low_dict_category`;

-- 创建数据字典分类表
CREATE TABLE `low_dict_category` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    `category_code`   VARCHAR(100) UNIQUE NOT NULL COMMENT '分类编码（唯一）',
    `category_name`   VARCHAR(100) NOT NULL COMMENT '分类名称',
    `description`     TEXT COMMENT '描述',
    `sort_order`      INT DEFAULT 0 COMMENT '排序',
    `status`          TINYINT DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
    `deleted`         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_by`       VARCHAR(64) COMMENT '创建者',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_category_code` (`category_code`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典分类表';

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `low_dict_item`;

-- 创建数据字典项表
CREATE TABLE `low_dict_item` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '字典项ID',
    `category_id`     BIGINT NOT NULL COMMENT '所属分类ID',
    `item_label`      VARCHAR(100) NOT NULL COMMENT '字典项标签',
    `item_value`      VARCHAR(200) NOT NULL COMMENT '字典项值',
    `item_color`      VARCHAR(20) COMMENT '颜色（用于标签等）',
    `item_icon`       VARCHAR(100) COMMENT '图标',
    `sort_order`      INT DEFAULT 0 COMMENT '排序',
    `status`          TINYINT DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
    `deleted`         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_item_value` (`item_value`),
    CONSTRAINT `fk_dict_item_category` FOREIGN KEY (`category_id`) REFERENCES `low_dict_category` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典项表';

-- 插入测试数据

-- 字典分类
INSERT INTO `low_dict_category` (`category_code`, `category_name`, `description`, `sort_order`, `create_by`) VALUES
('user_status', '用户状态', '用户启用/禁用状态', 1, 'admin'),
('gender', '性别', '性别选择', 2, 'admin'),
('yes_no', '是否', '是/否选择', 3, 'admin');

-- 字典项
INSERT INTO `low_dict_item` (`category_id`, `item_label`, `item_value`, `item_color`, `sort_order`) VALUES
-- 用户状态
(1, '启用', '1', 'success', 1),
(1, '禁用', '0', 'info', 2),
-- 性别
(2, '男', 'male', '', 1),
(2, '女', 'female', '', 2),
-- 是否
(3, '是', '1', 'success', 1),
(3, '否', '0', 'info', 2);

-- 查看数据
SELECT * FROM `low_dict_category`;
SELECT * FROM `low_dict_item`;
