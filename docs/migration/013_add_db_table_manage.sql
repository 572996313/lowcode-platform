-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- 库表管理和字段管理功能
-- =====================================================

-- 1. 创建库表管理表
CREATE TABLE IF NOT EXISTS `db_table` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `table_schema` VARCHAR(128) NOT NULL COMMENT '数据库/模式名称',
  `table_name` VARCHAR(128) NOT NULL COMMENT '表名',
  `table_type` VARCHAR(20) NOT NULL COMMENT '表类型：TABLE-表，VIEW-视图',
  `table_comment` VARCHAR(500) NULL COMMENT '表注释',
  `sync_status` TINYINT NOT NULL DEFAULT 0 COMMENT '同步状态：0-未同步，1-已同步',
  `sync_time` DATETIME NULL COMMENT '最后同步时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：1-删除，0-存在',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_schema_table` (`table_schema`, `table_name`, `deleted`),
  KEY `idx_table_name` (`table_name`),
  KEY `idx_table_type` (`table_type`),
  KEY `idx_sync_status` (`sync_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库表管理表';

-- 2. 创建字段管理表
CREATE TABLE IF NOT EXISTS `db_table_field` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `table_id` BIGINT NOT NULL COMMENT '所属表ID（关联db_table.id）',
  `field_name` VARCHAR(128) NOT NULL COMMENT '字段名',
  `field_type` VARCHAR(50) NOT NULL COMMENT '字段类型（VARCHAR, INT, DATETIME等）',
  `field_length` INT NULL COMMENT '字段长度',
  `decimal_digits` INT NULL COMMENT '小数位数',
  `is_nullable` TINYINT NOT NULL DEFAULT 1 COMMENT '是否可空：1-可空，0-不可空',
  `column_default` VARCHAR(500) NULL COMMENT '默认值',
  `is_primary_key` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主键：1-是，0-否',
  `is_auto_increment` TINYINT NOT NULL DEFAULT 0 COMMENT '是否自增：1-是，0-否',
  `field_comment` VARCHAR(500) NULL COMMENT '字段注释',
  `field_label` VARCHAR(128) NULL COMMENT '字段显示标签（用户可编辑）',
  `ordinal_position` INT NOT NULL COMMENT '字段在表中的位置（从1开始）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：1-删除，0-存在',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_table_field` (`table_id`, `field_name`, `deleted`),
  KEY `idx_table_id` (`table_id`),
  KEY `idx_field_name` (`field_name`),
  KEY `idx_ordinal_position` (`ordinal_position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字段管理表';

-- 3. 验证表结构
SELECT 'db_table 表结构' AS info;
DESC db_table;

SELECT 'db_table_field 表结构' AS info;
DESC db_table_field;

-- 4. 验证字符集
SELECT
    TABLE_NAME,
    TABLE_COLLATION,
    TABLE_COMMENT
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'lowcode_platform'
  AND TABLE_NAME IN ('db_table', 'db_table_field');
