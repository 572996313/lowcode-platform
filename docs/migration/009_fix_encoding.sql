-- =============================================
-- 修复按钮和菜单乱码问题
-- 版本: 009
-- 日期: 2026-01-31
-- =============================================

-- 1. 删除乱码的通用按钮
DELETE FROM low_button_config WHERE component_category = 'common' AND id >= 30;

-- 2. 重新插入正确编码的通用按钮库
INSERT INTO low_button_config (
    button_name, button_code, component_category, component_tags,
    button_type, button_size, icon, position,
    action_type, perms, sort_order, is_visible, status,
    create_by, create_time, update_time
) VALUES
-- CRUD 操作按钮
('新增', 'btn_add', 'common', '["system","create","crud"]',
 'primary', 'default', 'Plus', 'toolbar',
 'dialog', 'lowcode:button:add', 1, 1, 1,
 'system', NOW(), NOW()),

('编辑', 'btn_edit', 'common', '["system","update","crud"]',
 'primary', 'default', 'Edit', 'row',
 'dialog', 'lowcode:button:edit', 2, 1, 1,
 'system', NOW(), NOW()),

('删除', 'btn_delete', 'common', '["system","delete","crud"]',
 'danger', 'default', 'Delete', 'row',
 'api', 'lowcode:button:delete', 3, 1, 1,
 'system', NOW(), NOW()),

('查看', 'btn_view', 'common', '["system","read","crud"]',
 'info', 'default', 'View', 'row',
 'dialog', 'lowcode:button:view', 4, 1, 1,
 'system', NOW(), NOW()),

-- 导入导出按钮
('导出', 'btn_export', 'common', '["system","export"]',
 'success', 'default', 'Download', 'toolbar',
 'api', 'lowcode:button:export', 5, 1, 1,
 'system', NOW(), NOW()),

('导入', 'btn_import', 'common', '["system","import"]',
 'warning', 'default', 'Upload', 'toolbar',
 'custom', 'lowcode:button:import', 6, 1, 1,
 'system', NOW(), NOW()),

-- 搜索操作按钮
('查询', 'btn_search', 'common', '["system","search"]',
 'primary', 'default', 'Search', 'toolbar',
 'custom', 'lowcode:button:search', 7, 1, 1,
 'system', NOW(), NOW()),

('重置', 'btn_reset', 'common', '["system","search"]',
 'default', 'default', 'RefreshLeft', 'toolbar',
 'custom', 'lowcode:button:reset', 8, 1, 1,
 'system', NOW(), NOW()),

-- 通用操作按钮
('刷新', 'btn_refresh', 'common', '["system","common"]',
 'default', 'default', 'Refresh', 'toolbar',
 'custom', 'lowcode:button:refresh', 9, 1, 1,
 'system', NOW(), NOW()),

('提交', 'btn_submit', 'common', '["system","submit"]',
 'primary', 'default', 'Select', 'form',
 'api', 'lowcode:button:submit', 10, 1, 1,
 'system', NOW(), NOW()),

('取消', 'btn_cancel', 'common', '["system","cancel"]',
 'default', 'default', 'Close', 'form',
 'custom', 'lowcode:button:cancel', 11, 1, 1,
 'system', NOW(), NOW()),

('保存', 'btn_save', 'common', '["system","save"]',
 'success', 'default', 'Check', 'form',
 'api', 'lowcode:button:save', 12, 1, 1,
 'system', NOW(), NOW());

-- 3. 修复通用表单乱码
DELETE FROM low_form_config WHERE component_category = 'common';

INSERT INTO low_form_config (
    form_name, form_code, component_category, component_tags,
    form_type, layout_type, layout_cols, label_width, label_position, size,
    status, create_by, create_time, update_time
) VALUES
('通用新增表单', 'common_add_form', 'common', '["system","create"]',
 'add', 'horizontal', 2, 100, 'right', 'default',
 1, 'system', NOW(), NOW()),

('通用编辑表单', 'common_edit_form', 'common', '["system","update"]',
 'edit', 'horizontal', 2, 100, 'right', 'default',
 1, 'system', NOW(), NOW()),

('通用搜索表单', 'common_search_form', 'common', '["system","search"]',
 'search', 'inline', 4, 80, 'left', 'small',
 1, 'system', NOW(), NOW()),

('通用详情表单', 'common_detail_form', 'common', '["system","read"]',
 'detail', 'horizontal', 2, 120, 'right', 'default',
 1, 'system', NOW(), NOW());

-- 4. 修复通用表格乱码
DELETE FROM low_table_config WHERE component_category = 'common';

INSERT INTO low_table_config (
    table_name, table_code, component_category, component_tags,
    data_source_type, api_method, is_pagination, page_size, page_sizes,
    is_index, is_border, is_stripe, row_key, empty_text,
    status, create_by, create_time, update_time
) VALUES
('通用数据表格', 'common_data_table', 'common', '["system","list"]',
 'api', 'GET', 1, 20, '10,20,50,100',
 1, 1, 1, 'id', '暂无数据',
 1, 'system', NOW(), NOW()),

('通用列表表格', 'common_list_table', 'common', '["system","list"]',
 'api', 'GET', 1, 10, '10,20,50,100',
 1, 1, 0, 'id', '暂无数据',
 1, 'system', NOW(), NOW()),

('通用报表表格', 'common_report_table', 'common', '["system","report"]',
 'sql', 'GET', 0, NULL, '10,20,50,100',
 0, 1, 1, 'id', '暂无数据',
 1, 'system', NOW(), NOW());

-- 5. 修复菜单乱码
UPDATE sys_menu SET menu_name = '低代码' WHERE id = 2;
UPDATE sys_menu SET menu_name = '页面管理' WHERE id = 6;
UPDATE sys_menu SET menu_name = '表单管理' WHERE id = 7;
UPDATE sys_menu SET menu_name = '表格管理' WHERE id = 8;
UPDATE sys_menu SET menu_name = '页面模板' WHERE id = 9;

-- 6. 验证数据
SELECT 'button' as type, id, button_name, button_code
FROM low_button_config
WHERE component_category = 'common'
LIMIT 5;

SELECT 'form' as type, id, form_name, form_code
FROM low_form_config
WHERE component_category = 'common';

SELECT 'table' as type, id, table_name, table_code
FROM low_table_config
WHERE component_category = 'common';

SELECT 'menu' as type, id, menu_name, menu_code
FROM sys_menu
WHERE parent_id = 2;
