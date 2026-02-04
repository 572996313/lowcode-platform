create table sys_dict_category
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    parent_id     bigint   default 0                 not null comment '父分类ID，0表示根节点',
    category_name varchar(50)                        not null comment '分类名称',
    category_code varchar(50)                        not null comment '分类编码，唯一标识',
    sort_order    int      default 0                 null comment '排序序号',
    status        tinyint  default 1                 null comment '状态：0-禁用，1-启用',
    description   varchar(255)                       null comment '描述',
    deleted       int      default 0                 null comment '逻辑删除：0-正常，1-删除',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_category_code
        unique (category_code, deleted)
)
    comment '字典分类表';

create index idx_parent_id
    on sys_dict_category (parent_id);

INSERT INTO lowcode_platform.sys_dict_category (id, parent_id, category_name, category_code, sort_order, status, description, deleted, create_time, update_time) VALUES (1, 0, '系统状态', 'sys_status', 1, 1, '系统通用状态字典', 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_category (id, parent_id, category_name, category_code, sort_order, status, description, deleted, create_time, update_time) VALUES (2, 0, '用户类型', 'user_type', 2, 1, '用户类型分类', 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_category (id, parent_id, category_name, category_code, sort_order, status, description, deleted, create_time, update_time) VALUES (3, 0, '数据权限', 'data_scope', 3, 1, '数据权限范围', 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
INSERT INTO lowcode_platform.sys_dict_category (id, parent_id, category_name, category_code, sort_order, status, description, deleted, create_time, update_time) VALUES (4, 0, '性别', 'sys_gender', 4, 1, '性别分类', 0, '2026-02-01 20:11:53', '2026-02-01 20:11:53');
