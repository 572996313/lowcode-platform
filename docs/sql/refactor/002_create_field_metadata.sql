-- ============================================================================
-- 字段元数据表
-- 用途：存储字段元数据（从数据源自动提取或手动配置）
-- 创建时间：2026-03-04
-- ============================================================================

-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `low_field_metadata`;

-- 创建字段元数据表
CREATE TABLE `low_field_metadata` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '字段元数据ID',
    `dataset_id`      BIGINT NOT NULL COMMENT '关联数据集ID',
    `field_name`      VARCHAR(100) NOT NULL COMMENT '字段名称（对应数据源字段）',
    `field_label`     VARCHAR(100) COMMENT '字段标签',
    `field_type`      ENUM('string', 'number', 'date', 'datetime', 'time', 'boolean', 'json', 'text') NOT NULL COMMENT '字段类型',
    `data_type`       VARCHAR(50) COMMENT '数据库数据类型（VARCHAR、INT、DATETIME等）',
    `length`          INT COMMENT '字段长度',
    `is_nullable`     TINYINT DEFAULT 1 COMMENT '是否可空',
    `default_value`   TEXT COMMENT '默认值',
    `description`     TEXT COMMENT '字段描述',
    `is_primary`      TINYINT DEFAULT 0 COMMENT '是否主键',
    `sort_order`      INT DEFAULT 0 COMMENT '排序',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_dataset_id` (`dataset_id`),
    INDEX `idx_field_name` (`field_name`),
    UNIQUE KEY `uk_dataset_field` (`dataset_id`, `field_name`),
    CONSTRAINT `fk_field_metadata_dataset` FOREIGN KEY (`dataset_id`) REFERENCES `low_dataset_config` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字段元数据表';

-- 插入测试数据（为用户数据集生成字段元数据）
INSERT INTO `low_field_metadata` (`dataset_id`, `field_name`, `field_label`, `field_type`, `data_type`, `length`, `is_nullable`, `is_primary`, `sort_order`, `description`) VALUES
(1, 'id', 'ID', 'number', 'BIGINT', 20, 0, 1, 1, '用户ID'),
(1, 'username', '用户名', 'string', 'VARCHAR', 50, 0, 0, 2, '用户登录名'),
(1, 'password', '密码', 'string', 'VARCHAR', 100, 0, 0, 3, '登录密码（加密）'),
(1, 'nickname', '昵称', 'string', 'VARCHAR', 50, 1, 0, 4, '用户昵称'),
(1, 'email', '邮箱', 'string', 'VARCHAR', 100, 1, 0, 5, '电子邮箱'),
(1, 'phone', '手机号', 'string', 'VARCHAR', 20, 1, 0, 6, '手机号码'),
(1, 'status', '状态', 'number', 'INT', 11, 1, 0, 7, '用户状态'),
(1, 'create_time', '创建时间', 'datetime', 'DATETIME', 0, 1, 0, 8, '创建时间'),
(1, 'update_time', '更新时间', 'datetime', 'DATETIME', 0, 1, 0, 9, '更新时间');

-- 为菜单数据集生成字段元数据
INSERT INTO `low_field_metadata` (`dataset_id`, `field_name`, `field_label`, `field_type`, `data_type`, `length`, `is_nullable`, `is_primary`, `sort_order`, `description`) VALUES
(2, 'id', 'ID', 'number', 'BIGINT', 20, 0, 1, 1, '菜单ID'),
(2, 'menu_name', '菜单名称', 'string', 'VARCHAR', 50, 0, 0, 2, '菜单名称'),
(2, 'menu_code', '菜单编码', 'string', 'VARCHAR', 100, 0, 0, 3, '菜单编码'),
(2, 'parent_id', '父菜单ID', 'number', 'BIGINT', 20, 1, 0, 4, '父级菜单ID'),
(2, 'menu_type', '菜单类型', 'string', 'VARCHAR', 20, 0, 0, 5, '菜单类型（directory/menu/button）'),
(2, 'route_path', '路由路径', 'string', 'VARCHAR', 200, 1, 0, 6, '前端路由路径'),
(2, 'component', '组件路径', 'string', 'VARCHAR', 200, 1, 0, 7, '组件路径'),
(2, 'icon', '图标', 'string', 'VARCHAR', 50, 1, 0, 8, '菜单图标'),
(2, 'sort_order', '排序', 'number', 'INT', 11, 1, 0, 9, '排序号'),
(2, 'status', '状态', 'number', 'INT', 11, 1, 0, 10, '菜单状态');
