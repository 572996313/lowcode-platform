-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 创建表单模板表
CREATE TABLE `low_form_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `template_code` VARCHAR(50) NOT NULL COMMENT '模板编码（唯一，对应前端组件名）',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '模板描述',
  `preview_image` VARCHAR(500) DEFAULT NULL COMMENT '预览图URL',
  `is_system` TINYINT DEFAULT 0 COMMENT '是否系统模板（0=否 1=是）',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0=禁用 1=启用）',
  `sort_order` INT DEFAULT 0 COMMENT '排序序号',
  `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除（0=否 1=是）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单模板表';

-- 插入系统模板数据
INSERT INTO `low_form_template` (`template_name`, `template_code`, `description`, `is_system`, `status`, `sort_order`) VALUES
('简单表单模板', 'simple', '适用于基础信息录入，包含标题和分割线', 1, 1, 1),
('页签表单模板', 'tab', '适用于多分类信息录入，使用页签分组', 1, 1, 2),
('卡片表单模板', 'card', '适用于分组信息录入，使用卡片容器', 1, 1, 3);