create table sys_dict_item
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    category_id bigint                             not null comment '所属分类ID',
    parent_id   bigint   default 0                 not null comment '父项ID，0表示根项',
    item_label  varchar(100)                       not null comment '字典项显示文本',
    item_value  varchar(100)                       not null comment '字典项值',
    sort_order  int      default 0                 null comment '排序序号',
    status      tinyint  default 1                 null comment '状态：0-禁用，1-启用',
    description varchar(255)                       null comment '描述',
    css_class   varchar(50)                        null comment 'CSS样式类',
    icon        varchar(100)                       null comment '图标',
    deleted     int      default 0                 null comment '逻辑删除：0-正常，1-删除',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_category_value
        unique (category_id, item_value, deleted)
)
    comment '字典明细表';

create index idx_category_id
    on sys_dict_item (category_id);

create index idx_parent_id
    on sys_dict_item (parent_id);

INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (1, 1, 0, '启用', '1', 1, 1, '状态启用', 'tag-success', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (2, 1, 0, '禁用', '0', 2, 1, '状态禁用', 'tag-info', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (3, 1, 0, '删除', '-1', 3, 1, '已删除', 'tag-danger', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (4, 2, 0, '系统管理员', 'admin', 1, 1, '系统管理员角色', 'tag-primary', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (5, 2, 0, '普通用户', 'user', 2, 1, '普通用户', 'tag-default', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (6, 2, 0, '访客', 'guest', 3, 1, '访客用户', 'tag-warning', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (7, 3, 0, '全部数据', 'all', 1, 1, '全部数据权限', 'tag-success', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (8, 3, 0, '本部门及以下', 'dept_and_child', 2, 1, '本部门及子部门数据', 'tag-primary', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (9, 3, 0, '本部门', 'dept', 3, 1, '本部门数据', 'tag-info', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (10, 3, 0, '仅本人', 'self', 4, 1, '仅本人数据', 'tag-warning', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (11, 4, 0, '男', 'male', 1, 1, '男性', 'tag-primary', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (12, 4, 0, '女', 'female', 2, 1, '女性', 'tag-danger', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_item (id, category_id, parent_id, item_label, item_value, sort_order, status, description, css_class, icon, deleted, create_time, update_time) VALUES (13, 4, 0, '保密', 'secret', 3, 1, '性别保密', 'tag-info', null, 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
