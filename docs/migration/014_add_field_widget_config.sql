-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- 字段-控件绑定功能
-- =====================================================

-- 1. 创建字段-控件绑定配置表
CREATE TABLE IF NOT EXISTS `db_field_widget_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `table_id` BIGINT NULL COMMENT '所属表ID（关联db_table.id），为空表示默认映射规则',
  `field_id` BIGINT NULL COMMENT '字段ID（关联db_table_field.id），为空表示默认映射规则',
  `widget_type` VARCHAR(50) NOT NULL COMMENT '控件类型（input/textarea/select/radio/checkbox/switch/date/datetime/number/upload/cascader等）',
  `widget_config` TEXT NULL COMMENT '控件配置JSON（placeholder/默认值/校验规则/控件属性等）',
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否为默认映射规则：1-是，0-否',
  `default_for_field_type` VARCHAR(50) NULL COMMENT '作为默认映射的字段类型（VARCHAR/INT/DATETIME等）',
  `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：1-删除，0-存在',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_table_field` (`table_id`, `field_id`, `deleted`),
  KEY `idx_table_id` (`table_id`),
  KEY `idx_field_id` (`field_id`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_default_for_field_type` (`default_for_field_type`),
  KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字段-控件绑定配置表';

-- 2. 验证表结构
SELECT 'db_field_widget_config 表结构' AS info;
DESC db_field_widget_config;

-- 3. 验证字符集
SELECT
    TABLE_NAME,
    TABLE_COLLATION,
    TABLE_COMMENT
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'lowcode_platform'
  AND TABLE_NAME = 'db_field_widget_config';
