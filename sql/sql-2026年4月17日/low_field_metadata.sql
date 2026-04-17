create table low_field_metadata
(
    id            bigint auto_increment comment '字段元数据ID'
        primary key,
    dataset_id    bigint                                                                           not null comment '关联数据集ID',
    field_name    varchar(100)                                                                     not null comment '字段名称（对应数据源字段）',
    field_label   varchar(100)                                                                     null comment '字段标签',
    field_type    enum ('string', 'number', 'date', 'datetime', 'time', 'boolean', 'json', 'text') not null comment '字段类型',
    data_type     varchar(50)                                                                      null comment '数据库数据类型（VARCHAR、INT、DATETIME等）',
    length        int                                                                              null comment '字段长度',
    is_nullable   tinyint  default 1                                                               null comment '是否可空',
    default_value text                                                                             null comment '默认值',
    description   text                                                                             null comment '字段描述',
    is_primary    tinyint  default 0                                                               null comment '是否主键',
    sort_order    int      default 0                                                               null comment '排序',
    create_time   datetime default CURRENT_TIMESTAMP                                               null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP                                               null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_dataset_field
        unique (dataset_id, field_name),
    constraint fk_field_metadata_dataset
        foreign key (dataset_id) references low_dataset_config (id)
            on delete cascade
)
    comment '字段元数据表';

create index idx_dataset_id
    on low_field_metadata (dataset_id);

create index idx_field_name
    on low_field_metadata (field_name);

INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (1, 'id', 'ID', 'number', 'BIGINT', 20, 0, null, '用户ID', 1, 1, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (1, 'username', '用户名', 'string', 'VARCHAR', 50, 0, null, '用户登录名', 0, 2, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (1, 'password', '密码', 'string', 'VARCHAR', 100, 0, null, '登录密码（加密）', 0, 3, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (1, 'nickname', '昵称', 'string', 'VARCHAR', 50, 1, null, '用户昵称', 0, 4, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (1, 'email', '邮箱', 'string', 'VARCHAR', 100, 1, null, '电子邮箱', 0, 5, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (1, 'phone', '手机号', 'string', 'VARCHAR', 20, 1, null, '手机号码', 0, 6, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (1, 'status', '状态', 'number', 'INT', 11, 1, null, '用户状态', 0, 7, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (1, 'create_time', '创建时间', 'datetime', 'DATETIME', 0, 1, null, '创建时间', 0, 8, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (1, 'update_time', '更新时间', 'datetime', 'DATETIME', 0, 1, null, '更新时间', 0, 9, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'id', 'ID', 'number', 'BIGINT', 20, 0, null, '菜单ID', 1, 1, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'menu_name', '菜单名称', 'string', 'VARCHAR', 50, 0, null, '菜单名称', 0, 2, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'menu_code', '菜单编码', 'string', 'VARCHAR', 100, 0, null, '菜单编码', 0, 3, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'parent_id', '父菜单ID', 'number', 'BIGINT', 20, 1, null, '父级菜单ID', 0, 4, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'menu_type', '菜单类型', 'string', 'VARCHAR', 20, 0, null, '菜单类型（directory/menu/button）', 0, 5, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'route_path', '路由路径', 'string', 'VARCHAR', 200, 1, null, '前端路由路径', 0, 6, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'component', '组件路径', 'string', 'VARCHAR', 200, 1, null, '组件路径', 0, 7, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'icon', '图标', 'string', 'VARCHAR', 50, 1, null, '菜单图标', 0, 8, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'sort_order', '排序', 'number', 'INT', 11, 1, null, '排序号', 0, 9, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
INSERT INTO lowcode_platform.low_field_metadata (dataset_id, field_name, field_label, field_type, data_type, length, is_nullable, default_value, description, is_primary, sort_order, create_time, update_time) VALUES (2, 'status', '状态', 'number', 'INT', 11, 1, null, '菜单状态', 0, 10, '2026-03-04 18:30:39', '2026-03-04 18:30:39');
