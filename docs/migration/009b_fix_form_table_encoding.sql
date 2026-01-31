-- 修复表单和表格乱码
-- 删除乱码数据
DELETE FROM low_form_config WHERE component_category = 'common';
DELETE FROM low_table_config WHERE component_category = 'common';

-- 重新插入正确编码的数据
INSERT INTO low_form_config (form_name, form_code, component_category, component_tags, form_type, layout_type, layout_cols, label_width, label_position, size, status, create_by, create_time, update_time) VALUES
('通用新增表单', 'common_add_form', 'common', '["system","create"]', 'add', 'horizontal', 2, 100, 'right', 'default', 1, 'system', NOW(), NOW()),
('通用编辑表单', 'common_edit_form', 'common', '["system","update"]', 'edit', 'horizontal', 2, 100, 'right', 'default', 1, 'system', NOW(), NOW()),
('通用搜索表单', 'common_search_form', 'common', '["system","search"]', 'search', 'inline', 4, 80, 'left', 'small', 1, 'system', NOW(), NOW()),
('通用详情表单', 'common_detail_form', 'common', '["system","read"]', 'detail', 'horizontal', 2, 120, 'right', 'default', 1, 'system', NOW(), NOW());

INSERT INTO low_table_config (table_name, table_code, component_category, component_tags, data_source_type, api_method, is_pagination, page_size, page_sizes, is_index, is_border, is_stripe, row_key, empty_text, status, create_by, create_time, update_time) VALUES
('通用数据表格', 'common_data_table', 'common', '["system","list"]', 'api', 'GET', 1, 20, '10,20,50,100', 1, 1, 1, 'id', '暂无数据', 1, 'system', NOW(), NOW()),
('通用列表表格', 'common_list_table', 'common', '["system","list"]', 'api', 'GET', 1, 10, '10,20,50,100', 1, 1, 0, 'id', '暂无数据', 1, 'system', NOW(), NOW()),
('通用报表表格', 'common_report_table', 'common', '["system","report"]', 'sql', 'GET', 0, NULL, '10,20,50,100', 0, 1, 1, 'id', '暂无数据', 1, 'system', NOW(), NOW());
