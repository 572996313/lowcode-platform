create table sys_menu
(
    id             bigint auto_increment comment '菜单ID'
        primary key,
    parent_id      bigint   default 0                 null comment '父菜单ID',
    menu_name      varchar(50)                        not null comment '菜单名称',
    menu_code      varchar(50)                        null comment '菜单编码',
    menu_type      tinyint                            null comment '菜单类型(1目录 2菜单 3按钮)',
    icon           varchar(100)                       null comment '菜单图标',
    sort_order     int      default 0                 null comment '排序',
    route_path     varchar(200)                       null comment '路由地址',
    component_path varchar(200)                       null comment '组件路径',
    page_id        bigint                             null comment '关联页面ID',
    is_external    tinyint  default 0                 null comment '是否外链(0否 1是)',
    external_url   varchar(500)                       null comment '外链地址',
    visible        tinyint  default 1                 null comment '是否可见',
    status         tinyint  default 1                 null comment '状态(0停用 1启用)',
    perms          varchar(100)                       null comment '权限标识',
    deleted        tinyint  default 0                 null comment '是否删除',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '菜单配置表';

create index idx_parent_id
    on sys_menu (parent_id);

create index idx_status
    on sys_menu (status);

INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (1, 0, '系统管理', 'system', 1, 'Setting', 1, '/system', null, null, 0, null, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (2, 0, '低代码配置', 'lowcode', 1, 'Edit', 2, '/lowcode', null, null, 0, null, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (3, 1, '用户管理', 'system:user', 2, 'User', 1, '/system/user', '/views/system/UserManage.vue', null, 0, null, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (4, 1, '菜单管理', 'system:menu', 2, 'Menu', 2, '/system/menu', '/views/system/MenuManage.vue', null, 0, null, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (5, 1, '字典管理', 'system:dict', 2, 'Collection', 3, '/system/dict', '/views/system/DictManage.vue', null, 0, null, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (6, 2, '页面管理', 'lowcode:page', 2, 'Document', 1, '/lowcode/page', '/views/lowcode/PageManage.vue', null, 0, null, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (7, 2, '表单管理', 'lowcode:form', 2, 'Document', 2, '/lowcode/form', '/views/lowcode/FormList.vue', null, 0, null, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (8, 2, '表格管理', 'lowcode:table', 2, 'Grid', 3, '/lowcode/table', '/views/lowcode/TableList.vue', null, 0, null, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (9, 2, '模板管理', 'lowcode:template', 2, 'Files', 4, '/lowcode/template', '/views/lowcode/TemplateManage.vue', null, 0, null, 1, 1, null, 0, '2026-01-28 17:01:19', '2026-01-28 17:01:19');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (10, 2, '按钮管理', 'lowcode:button', 2, 'Setting', 5, '/lowcode/ButtonLibrary', 'views/lowcode/ButtonLibrary.vue', null, 0, null, 1, 1, 'lowcode:button:list', 0, '2026-01-30 23:54:27', '2026-01-31 00:25:36');
INSERT INTO lowcode_platform.sys_menu (id, parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path, page_id, is_external, external_url, visible, status, perms, deleted, create_time, update_time) VALUES (11, 2, 'V4用户测试', 'v4:user:test', 2, 'User', 99, '/v4-user-test', null, 34, 0, null, 1, 1, null, 0, '2026-01-31 18:05:00', '2026-01-31 18:05:00');
