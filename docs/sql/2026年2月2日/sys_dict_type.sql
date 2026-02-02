create table sys_dict_type
(
    id          bigint auto_increment comment '字典ID'
        primary key,
    dict_name   varchar(100)                       not null comment '字典名称',
    dict_code   varchar(100)                       not null comment '字典编码',
    status      tinyint  default 1                 null comment '状态',
    remark      varchar(500)                       null comment '备注',
    deleted     tinyint  default 0                 null comment '是否删除',
    create_by   varchar(64)                        null comment '创建者',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint dict_code
        unique (dict_code)
)
    comment '数据字典类型表';

INSERT INTO lowcode_platform.sys_dict_type (id, dict_name, dict_code, status, remark, deleted, create_by, create_time, update_time) VALUES (1, '状态', 'sys_status', 1, '通用状态字典', 0, null, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_type (id, dict_name, dict_code, status, remark, deleted, create_by, create_time, update_time) VALUES (2, '是否', 'sys_yes_no', 1, '是否选项', 0, null, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_type (id, dict_name, dict_code, status, remark, deleted, create_by, create_time, update_time) VALUES (3, '性别', 'sys_gender', 1, '性别选项', 0, null, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_type (id, dict_name, dict_code, status, remark, deleted, create_by, create_time, update_time) VALUES (4, '菜单类型', 'sys_menu_type', 1, '菜单类型', 0, null, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_type (id, dict_name, dict_code, status, remark, deleted, create_by, create_time, update_time) VALUES (5, '按钮类型', 'sys_button_type', 1, '按钮样式类型', 0, null, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_type (id, dict_name, dict_code, status, remark, deleted, create_by, create_time, update_time) VALUES (6, '表单字段类型', 'form_field_type', 1, '表单字段组件类型', 0, null, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_type (id, dict_name, dict_code, status, remark, deleted, create_by, create_time, update_time) VALUES (7, '表格列类型', 'table_column_type', 1, '表格列展示类型', 0, null, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_type (id, dict_name, dict_code, status, remark, deleted, create_by, create_time, update_time) VALUES (8, '查询类型', 'search_type', 1, '查询条件类型', 0, null, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_type (id, dict_name, dict_code, status, remark, deleted, create_by, create_time, update_time) VALUES (9, '布局类型', 'layout_type', 1, '页面布局类型', 0, null, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
