-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- 添加低代码重构相关菜单
-- =====================================================

-- 1. 添加页面布局管理菜单
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, icon, route_path, component_path, sort_order, visible, status, perms, deleted)
VALUES (2, '页面布局', 'lowcode:page_layout', 2, 'Grid', '/lowcode/PageLayoutManage', '/views/lowcode/PageLayoutManage.vue', 15, 1, 1, NULL, 0);

-- 2. 更新表单管理路由（指向FormManage）
UPDATE sys_menu SET route_path = '/lowcode/FormManage', component_path = '/views/lowcode/FormManage.vue'
WHERE menu_code = 'lowcode:form';

-- 3. 更新表格管理路由（指向TableManage）
UPDATE sys_menu SET route_path = '/lowcode/TableManage', component_path = '/views/lowcode/TableManage.vue'
WHERE menu_code = 'lowcode:table';

-- 4. 添加表单字段绑定菜单（作为表单管理的子菜单）
-- 先获取表单管理的ID
SET @form_menu_id = (SELECT id FROM sys_menu WHERE menu_code = 'lowcode:form');

INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, icon, route_path, component_path, sort_order, visible, status, perms, deleted)
VALUES (@form_menu_id, '字段绑定', 'lowcode:form_field_binding', 2, 'Link', '/lowcode/FormFieldBinding', '/views/lowcode/FormFieldBinding.vue', 1, 0, 1, NULL, 0);

-- 5. 添加表格列绑定菜单（作为表格管理的子菜单）
-- 先获取表格管理的ID
SET @table_menu_id = (SELECT id FROM sys_menu WHERE menu_code = 'lowcode:table');

INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, icon, route_path, component_path, sort_order, visible, status, perms, deleted)
VALUES (@table_menu_id, '列绑定', 'lowcode:table_column_binding', 2, 'Link', '/lowcode/TableColumnBinding', '/views/lowcode/TableColumnBinding.vue', 1, 0, 1, NULL, 0);

-- =====================================================
-- 菜单添加完成
-- =====================================================
