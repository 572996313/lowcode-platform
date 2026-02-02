-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 创建库表分组表
CREATE TABLE IF NOT EXISTS `db_table_group` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分组ID，0表示顶级分组',
  `group_name` VARCHAR(100) NOT NULL COMMENT '分组名称',
  `group_code` VARCHAR(50) NULL COMMENT '分组编码（可选）',
  `description` VARCHAR(500) NULL COMMENT '分组描述',
  `sort_order` INT DEFAULT 0 COMMENT '排序号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-存在，1-删除',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库表分组表';

-- 在 db_table 表添加 group_id 字段
ALTER TABLE `db_table` ADD COLUMN `group_id` BIGINT NULL COMMENT '所属分组ID' AFTER `sync_time`;
ALTER TABLE `db_table` ADD INDEX `idx_group_id` (`group_id`);
