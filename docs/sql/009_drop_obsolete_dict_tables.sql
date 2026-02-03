-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 删除旧的废弃字典表（已由 sys_dict_category 和 sys_dict_item 替代）
DROP TABLE IF EXISTS `sys_dict_data`;
DROP TABLE IF EXISTS `sys_dict_type`;

-- 说明：
-- 旧设计使用 sys_dict_type + sys_dict_data
-- 新设计使用 sys_dict_category + sys_dict_item（支持树形结构）
