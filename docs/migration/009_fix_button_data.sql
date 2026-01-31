-- 修复乱码数据
-- 使用文件方式导入确保字符集正确

-- 清空表
TRUNCATE TABLE low_button_config;

-- 插入正确的测试数据
INSERT INTO low_button_config (button_name, button_code, component_category, position, button_type, button_size, sort_order, status) VALUES
('保存', 'btn_save', 'common', 'form', 'primary', 'default', 1, 1),
('取消', 'btn_cancel', 'common', 'form', 'default', 'default', 2, 1),
('提交', 'btn_submit', 'common', 'form', 'primary', 'default', 3, 1),
('刷新', 'btn_refresh', 'common', 'toolbar', 'default', 'default', 1, 1),
('重置', 'btn_reset', 'common', 'toolbar', 'default', 'default', 2, 1),
('新增', 'btn_add', 'business', 'toolbar', 'primary', 'default', 1, 1),
('编辑', 'btn_edit', 'business', 'row', 'primary', 'small', 1, 1),
('删除', 'btn_delete', 'business', 'row', 'danger', 'small', 2, 1),
('查询', 'btn_query', 'business', 'toolbar', 'success', 'default', 3, 1),
('导出', 'btn_export', 'business', 'toolbar', 'success', 'default', 4, 1);

-- 验证数据
SELECT id, button_name, button_code, component_category FROM low_button_config ORDER BY id;
