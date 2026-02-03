-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 字典引用关系表
CREATE TABLE `sys_dict_reference` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_category_code` VARCHAR(64) NOT NULL COMMENT '字典分类编码',
  `resource_type` VARCHAR(32) NOT NULL COMMENT '资源类型: form_field/table_column/form_template/table_template',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `resource_name` VARCHAR(128) DEFAULT NULL COMMENT '资源名称（冗余）',
  `page_id` BIGINT DEFAULT NULL COMMENT '所属页面ID',
  `page_name` VARCHAR(128) DEFAULT NULL COMMENT '所属页面名称（冗余）',
  `field_code` VARCHAR(64) DEFAULT NULL COMMENT '字段编码',
  `field_label` VARCHAR(128) DEFAULT NULL COMMENT '字段标签',
  `reference_path` VARCHAR(255) DEFAULT NULL COMMENT '引用路径（JSON Path）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_resource` (`dict_category_code`, `resource_type`, `resource_id`, `field_code`),
  KEY `idx_dict_code` (`dict_category_code`),
  KEY `idx_page_id` (`page_id`),
  KEY `idx_resource` (`resource_type`, `resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典引用关系表';
