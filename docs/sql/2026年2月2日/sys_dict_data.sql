create table sys_dict_data
(
    id           bigint auto_increment comment '字典数据ID'
        primary key,
    dict_type_id bigint                             not null comment '字典类型ID',
    dict_code    varchar(100)                       not null comment '字典编码',
    dict_label   varchar(100)                       not null comment '字典标签',
    dict_value   varchar(100)                       not null comment '字典值',
    css_class    varchar(100)                       null comment 'CSS类名',
    list_class   varchar(100)                       null comment '表格回显样式',
    is_default   tinyint  default 0                 null comment '是否默认',
    sort_order   int      default 0                 null comment '排序',
    status       tinyint  default 1                 null comment '状态',
    remark       varchar(500)                       null comment '备注',
    deleted      tinyint  default 0                 null comment '是否删除',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '数据字典数据表';

create index idx_dict_code
    on sys_dict_data (dict_code);

create index idx_dict_type_id
    on sys_dict_data (dict_type_id);

INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (1, 1, 'sys_status', '启用', '1', null, null, 0, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (2, 1, 'sys_status', '禁用', '0', null, null, 0, 2, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (3, 2, 'sys_yes_no', '是', '1', null, null, 0, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (4, 2, 'sys_yes_no', '否', '0', null, null, 0, 2, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (5, 3, 'sys_gender', '男', '1', null, null, 0, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (6, 3, 'sys_gender', '女', '2', null, null, 0, 2, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (7, 3, 'sys_gender', '未知', '0', null, null, 0, 3, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (8, 4, 'sys_menu_type', '目录', '1', null, null, 0, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (9, 4, 'sys_menu_type', '菜单', '2', null, null, 0, 2, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (10, 4, 'sys_menu_type', '按钮', '3', null, null, 0, 3, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (11, 5, 'sys_button_type', '主要', 'primary', null, null, 0, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (12, 5, 'sys_button_type', '成功', 'success', null, null, 0, 2, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (13, 5, 'sys_button_type', '警告', 'warning', null, null, 0, 3, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (14, 5, 'sys_button_type', '危险', 'danger', null, null, 0, 4, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (15, 5, 'sys_button_type', '信息', 'info', null, null, 0, 5, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_dict_data (id, dict_type_id, dict_code, dict_label, dict_value, css_class, list_class, is_default, sort_order, status, remark, deleted, create_time, update_time) VALUES (16, 5, 'sys_button_type', '默认', 'default', null, null, 0, 6, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
