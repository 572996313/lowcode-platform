-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 为表单配置表添加模板相关字段
ALTER TABLE `low_form_config`
ADD COLUMN `template_id` BIGINT DEFAULT NULL COMMENT '关联的模板ID' AFTER `id`,
ADD COLUMN `source_type` VARCHAR(20) DEFAULT 'custom' COMMENT '来源类型（template=模板 custom=自定义）' AFTER `template_id`,
ADD KEY `idx_template_id` (`template_id`);