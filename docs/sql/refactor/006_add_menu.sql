-- 添加重构版功能的菜单
USE lowcode_platform;

-- 检查菜单是否存在，不存在则添加
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, route_path, component_path, icon, sort_order, visible, status)
SELECT
    0,
    '数据集管理',
    'dataset_manage',
    1,
    '/dataset/Manage',
    '/views/dataset/DatasetManage.vue',
    'Database',
    100,
    1,
    1
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_code = 'dataset_manage'
);

-- 插入页面设计器（新版）菜单
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, route_path, component_path, icon, sort_order, visible, status)
SELECT
    0,
    '页面设计器（新版）',
    'page_designer_new',
    1,
    '/page/DesignerNew',
    '/views/page/PageDesignerNew.vue',
    'Edit',
    101,
    1,
    1
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_code = 'page_designer_new'
);

-- 查看菜单
SELECT id, menu_name, menu_code, route_path, icon FROM sys_menu WHERE menu_code IN ('dataset_manage', 'page_designer_new');
