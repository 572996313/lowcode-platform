-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 下拉配置表
CREATE TABLE IF NOT EXISTS low_dropdown_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    config_code VARCHAR(50) NOT NULL COMMENT '配置编码',
    dropdown_type VARCHAR(20) NOT NULL COMMENT '下拉类型：flat/tree/cascader',
    sql_template TEXT NOT NULL COMMENT 'SQL模板',
    label_field VARCHAR(100) NOT NULL COMMENT '标签字段',
    value_field VARCHAR(100) NOT NULL COMMENT '值字段',
    parent_field VARCHAR(100) COMMENT '父级字段',
    param_config JSON COMMENT '参数配置',
    extra_fields TEXT COMMENT '额外字段',
    description VARCHAR(500) COMMENT '描述',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_config_code (config_code),
    KEY idx_enabled (enabled),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='下拉配置表';

-- 插入示例数据
INSERT INTO low_dropdown_config (config_name, config_code, dropdown_type, sql_template, label_field, value_field, parent_field, param_config, extra_fields, description, enabled, sort_order, created_by) VALUES
(
    '部门树',
    'dept_tree',
    'tree',
    'SELECT id, dept_name, parent_id FROM sys_dept WHERE status = 1 ORDER BY sort_order, id',
    'dept_name',
    'id',
    'parent_id',
    '{"params": []}',
    '',
    '部门树形下拉',
    1,
    1,
    'system'
),
(
    '用户状态',
    'user_status',
    'flat',
    'SELECT \n  status_code as value,\n  status_name as label,\n  status_color as color\nFROM sys_dict_status\nWHERE enabled = 1\nORDER BY sort_order',
    'label',
    'value',
    NULL,
    '{"params": []}',
    'color',
    '用户状态列表',
    1,
    2,
    'system'
),
(
    '带参数的用户列表',
    'user_list_by_status',
    'flat',
    'SELECT \n  id as value,\n  username as label,\n  dept_name,\n  email\nFROM sys_user u\nLEFT JOIN sys_dept d ON u.dept_id = d.id\nWHERE 1=1\n  AND (#{status} IS NULL OR u.status = #{status})\n  AND (#{deptId} IS NULL OR u.dept_id = #{deptId})\nORDER BY u.id',
    'label',
    'value',
    NULL,
    '{"params": [{"name": "status", "type": "String", "required": false, "defaultValue": "1", "description": "用户状态"}, {"name": "deptId", "type": "Long", "required": false, "defaultValue": null, "description": "部门ID"}]}',
    'dept_name,email',
    '根据状态和部门查询用户',
    1,
    3,
    'system'
);
