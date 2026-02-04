create table low_dropdown_config
(
    id            bigint auto_increment comment '主键'
        primary key,
    config_name   varchar(100)                         not null comment '配置名称',
    config_code   varchar(50)                          not null comment '配置编码',
    dropdown_type varchar(20)                          not null comment '下拉类型：flat/tree/cascader',
    sql_template  text                                 not null comment 'SQL模板',
    label_field   varchar(100)                         not null comment '标签字段',
    value_field   varchar(100)                         not null comment '值字段',
    parent_field  varchar(100)                         null comment '父级字段',
    param_config  json                                 null comment '参数配置',
    extra_fields  text                                 null comment '额外字段',
    description   varchar(500)                         null comment '描述',
    enabled       tinyint(1) default 1                 null comment '是否启用',
    sort_order    int        default 0                 null comment '排序',
    created_by    varchar(50)                          null comment '创建人',
    create_time   datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted       tinyint(1) default 0                 null comment '逻辑删除',
    constraint uk_config_code
        unique (config_code)
)
    comment '下拉配置表';

create index idx_enabled
    on low_dropdown_config (enabled);

create index idx_sort_order
    on low_dropdown_config (sort_order);

INSERT INTO lowcode_platform.low_dropdown_config (id, config_name, config_code, dropdown_type, sql_template, label_field, value_field, parent_field, param_config, extra_fields, description, enabled, sort_order, created_by, create_time, update_time, deleted) VALUES (1, '部门树', 'dept_tree', 'tree', 'SELECT id, dept_name, parent_id FROM sys_dept WHERE status = 1 ORDER BY sort_order, id', 'dept_name', 'id', 'parent_id', '{"params": []}', '', '部门树形下拉', 1, 1, 'system', '2026-02-01 19:57:01', '2026-02-01 19:57:01', 0);
INSERT INTO lowcode_platform.low_dropdown_config (id, config_name, config_code, dropdown_type, sql_template, label_field, value_field, parent_field, param_config, extra_fields, description, enabled, sort_order, created_by, create_time, update_time, deleted) VALUES (2, '用户状态', 'user_status', 'flat', 'SELECT 
  status_code as value,
  status_name as label,
  status_color as color
FROM sys_dict_status
WHERE enabled = 1
ORDER BY sort_order', 'label', 'value', null, '{"params": []}', 'color', '用户状态列表', 1, 2, 'system', '2026-02-01 19:57:01', '2026-02-01 19:57:01', 0);
INSERT INTO lowcode_platform.low_dropdown_config (id, config_name, config_code, dropdown_type, sql_template, label_field, value_field, parent_field, param_config, extra_fields, description, enabled, sort_order, created_by, create_time, update_time, deleted) VALUES (3, '带参数的用户列表', 'user_list_by_status', 'flat', 'SELECT 
  id as value,
  username as label,
  dept_name,
  email
FROM sys_user u
LEFT JOIN sys_dept d ON u.dept_id = d.id
WHERE 1=1
  AND (#{status} IS NULL OR u.status = #{status})
  AND (#{deptId} IS NULL OR u.dept_id = #{deptId})
ORDER BY u.id', 'label', 'value', null, '{"params": [{"name": "status", "type": "String", "required": false, "description": "用户状态", "defaultValue": "1"}, {"name": "deptId", "type": "Long", "required": false, "description": "部门ID", "defaultValue": null}]}', 'dept_name,email', '根据状态和部门查询用户', 1, 3, 'system', '2026-02-01 19:57:01', '2026-02-01 19:57:01', 0);
