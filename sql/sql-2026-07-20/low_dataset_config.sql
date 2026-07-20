create table low_dataset_config
(
    id            bigint auto_increment comment '数据集ID'
        primary key,
    dataset_name  varchar(100)                         not null comment '数据集名称',
    dataset_code  varchar(100)                         not null comment '数据集编码（唯一）',
    source_type   enum ('table', 'view', 'sql', 'api') not null comment '数据源类型',
    source_config json                                 not null comment '数据源配置',
    primary_key   varchar(50)                          null comment '主键字段名',
    description   text                                 null comment '描述',
    status        tinyint  default 1                   null comment '状态（1=启用，0=禁用）',
    deleted       tinyint  default 0                   null comment '逻辑删除',
    create_by     varchar(64)                          null comment '创建者',
    create_time   datetime default CURRENT_TIMESTAMP   null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP   null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint dataset_code
        unique (dataset_code)
)
    comment '数据集配置表';

create index idx_dataset_code
    on low_dataset_config (dataset_code);

create index idx_source_type
    on low_dataset_config (source_type);

create index idx_status
    on low_dataset_config (status);

INSERT INTO lowcode_platform.low_dataset_config (id, dataset_name, dataset_code, source_type, source_config, primary_key, description, status, deleted, create_by, create_time, update_time) VALUES (1, '用户数据集', 'user_dataset', 'table', '{"schema": "lowcode_platform", "tableName": "sys_user"}', 'id', '系统用户数据集', 1, 0, 'admin', '2026-03-04 14:39:10', '2026-03-04 14:39:10');
INSERT INTO lowcode_platform.low_dataset_config (id, dataset_name, dataset_code, source_type, source_config, primary_key, description, status, deleted, create_by, create_time, update_time) VALUES (2, '菜单数据集', 'menu_dataset', 'table', '{"schema": "lowcode_platform", "tableName": "sys_menu"}', 'id', '系统菜单数据集', 1, 0, 'admin', '2026-03-04 14:39:10', '2026-03-04 14:39:10');
