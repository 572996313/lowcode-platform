-- 添加低代码管理（新版）菜单
-- 说明：本脚本添加低代码平台管理菜单（新版）
-- 执行方式：docker exec -i <mysql容器名> mysql -uroot -p<密码> --default-character-set=utf8mb4 lowcode_platform < 009_add_new_menus.sql

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 插入低代码管理（新版）菜单
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, icon, route_path, component_path, sort_order, status, create_time, update_time)
VALUES
(0, '低代码管理', 'lowcode', 1, 'Grid', NULL, NULL, 100, 1, NOW(), NOW());

SET @parent_id = LAST_INSERT_ID();

-- 插入子菜单
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, icon, route_path, component_path, sort_order, status, create_time, update_time)
VALUES
(@parent_id, '数据集管理', 'dataset_manage', 2, 'Database', '/dataset/Manage', '/views/dataset/DatasetManage.vue', 1, 1, NOW(), NOW()),
(@parent_id, '表单管理', 'form_manage', 2, 'List', '/form/ManageNew', '/views/form/FormManageNew.vue', 2, 1, NOW(), NOW()),
(@parent_id, '表格管理', 'table_manage', 2, 'Grid', '/table/ManageNew', '/views/table/TableManageNew.vue', 3, 1, NOW(), NOW()),
(@parent_id, '页面管理', 'page_manage', 2, 'Document', '/page/ManageNew', '/views/page/PageManageNew.vue', 4, 1, NOW(), NOW());

SELECT '✓ 菜单添加完成！' AS result;
SELECT @parent_id AS '父菜单ID';
SELECT COUNT(*) AS '新增子菜单数' FROM sys_menu WHERE parent_id = @parent_id;
