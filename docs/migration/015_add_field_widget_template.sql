-- 字段控件配置模板表
-- 用于为同一个数据库字段配置多套控件模板
-- 创建时间: 2025-02-01

CREATE TABLE `db_field_widget_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `table_id` BIGINT NOT NULL COMMENT '所属表ID',
  `field_id` BIGINT NOT NULL COMMENT '字段ID',
  `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `template_code` VARCHAR(100) NULL COMMENT '模板编码',
  `widget_type` VARCHAR(50) NOT NULL COMMENT '控件类型',
  `widget_config` TEXT NULL COMMENT '控件配置JSON',
  `is_system` TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统预设',
  `description` VARCHAR(500) NULL COMMENT '模板描述',
  `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_field_template` (`field_id`, `template_code`, `deleted`),
  KEY `idx_table_id` (`table_id`),
  KEY `idx_field_id` (`field_id`),
  KEY `idx_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字段控件配置模板表';
