-- =============================================
-- 初始化完整通用组件库
-- 版本: 007
-- 日期: 2026-01-30
-- =============================================

-- 1. 初始化通用按钮库（12个按钮）
INSERT INTO low_button_config (
    button_name, button_code, component_category, component_tags,
    button_type, button_size, action_type, position,
    icon, sort_order, is_visible, perms,
    create_by, create_time, update_time
) VALUES
-- CRUD 操作按钮
('新增', 'btn_add', 'common', '["system","create","crud"]',
 'primary', 'default', 'dialog', 'toolbar',
 'Plus', 1, 1, 'low:button:add',
 'system', NOW(), NOW()),

('编辑', 'btn_edit', 'common', '["system","update","crud"]',
 'primary', 'default', 'dialog', 'row',
 'Edit', 2, 1, 'low:button:edit',
 'system', NOW(), NOW()),

('删除', 'btn_delete', 'common', '["system","delete","crud"]',
 'danger', 'default', 'api', 'row',
 'Delete', 3, 1, 'low:button:delete',
 'system', NOW(), NOW()),

('查看', 'btn_view', 'common', '["system","read","crud"]',
 'info', 'default', 'dialog', 'row',
 'View', 4, 1, 'low:button:view',
 'system', NOW(), NOW()),

-- 导入导出按钮
('导出', 'btn_export', 'common', '["system","export"]',
 'success', 'default', 'api', 'toolbar',
 'Download', 5, 1, 'low:button:export',
 'system', NOW(), NOW()),

('导入', 'btn_import', 'common', '["system","import"]',
 'warning', 'default', 'custom', 'toolbar',
 'Upload', 6, 1, 'low:button:import',
 'system', NOW(), NOW()),

-- 搜索操作按钮
('查询', 'btn_search', 'common', '["system","search"]',
 'primary', 'default', 'custom', 'toolbar',
 'Search', 7, 1, 'low:button:search',
 'system', NOW(), NOW()),

('重置', 'btn_reset', 'common', '["system","search"]',
 'default', 'default', 'custom', 'toolbar',
 'RefreshLeft', 8, 1, 'low:button:reset',
 'system', NOW(), NOW()),

-- 通用操作按钮
('刷新', 'btn_refresh', 'common', '["system","common"]',
 'default', 'default', 'custom', 'toolbar',
 'Refresh', 9, 1, 'low:button:refresh',
 'system', NOW(), NOW()),

('提交', 'btn_submit', 'common', '["system","submit"]',
 'primary', 'default', 'api', 'form',
 'Select', 10, 1, 'low:button:submit',
 'system', NOW(), NOW()),

('取消', 'btn_cancel', 'common', '["system","cancel"]',
 'default', 'default', 'custom', 'form',
 'Close', 11, 1, 'low:button:cancel',
 'system', NOW(), NOW()),

('保存', 'btn_save', 'common', '["system","save"]',
 'success', 'default', 'api', 'form',
 'Check', 12, 1, 'low:button:save',
 'system', NOW(), NOW());

-- 2. 初始化通用表单库（4个表单）
INSERT INTO low_form_config (
    form_name, form_code, component_category, component_tags,
    form_type, layout_type, layout_cols, label_width, label_position, size, status,
    create_by, create_time, update_time
) VALUES
('通用新增表单', 'common_add_form', 'common', '["system","create"]',
 'add', 'horizontal', 2, 100, 'right', 'default', 1,
 'system', NOW(), NOW()),

('通用编辑表单', 'common_edit_form', 'common', '["system","update"]',
 'edit', 'horizontal', 2, 100, 'right', 'default', 1,
 'system', NOW(), NOW()),

('通用搜索表单', 'common_search_form', 'common', '["system","search"]',
 'search', 'inline', 4, 80, 'left', 'small', 1,
 'system', NOW(), NOW()),

('通用详情表单', 'common_detail_form', 'common', '["system","read"]',
 'detail', 'horizontal', 2, 120, 'right', 'default', 1,
 'system', NOW(), NOW());

-- 3. 初始化通用表格库（3个表格）
INSERT INTO low_table_config (
    table_name, table_code, component_category, component_tags,
    data_source_type, is_pagination, page_size, is_index, is_border, is_stripe, status,
    row_key, create_by, create_time, update_time
) VALUES
('通用数据表格', 'common_data_table', 'common', '["system","list"]',
 'api', 1, 20, 1, 1, 1, 1,
 'id', 'system', NOW(), NOW()),

('通用列表表格', 'common_list_table', 'common', '["system","list"]',
 'api', 1, 10, 1, 1, 0, 1,
 'id', 'system', NOW(), NOW()),

('通用报表表格', 'common_report_table', 'common', '["system","report"]',
 'sql', 0, NULL, 0, 1, 1, 1,
 'id', 'system', NOW(), NOW());

-- 验证初始化结果
SELECT '✓ 通用组件库初始化完成' as message;
SELECT
  'button' as type, COUNT(*) as count
FROM low_button_config WHERE component_category = 'common'
UNION ALL
SELECT
  'form' as type, COUNT(*) as count
FROM low_form_config WHERE component_category = 'common'
UNION ALL
SELECT
  'table' as type, COUNT(*) as count
FROM low_table_config WHERE component_category = 'common';
