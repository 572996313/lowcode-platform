create table table_column_binding
(
    id               bigint auto_increment comment '绑定ID'
        primary key,
    table_config_id  bigint                                not null comment '表格配置ID（关联low_table_config.id）',
    field_id         bigint                                not null comment '字段ID（关联db_table_field.id）',
    column_label     varchar(128)                          null comment '列标题',
    column_type      varchar(50) default 'text'            null comment '列类型（text/image/tag/switch/dict/link等）',
    width            int                                   null comment '列宽度',
    min_width        int                                   null comment '最小列宽度',
    sortable         tinyint     default 0                 null comment '是否可排序',
    fixed            varchar(20)                           null comment '固定列（left/right）',
    align            varchar(20) default 'center'          null comment '对齐方式',
    show_overflow    tinyint     default 1                 null comment '是否溢出省略',
    formatter_config text                                  null comment '格式化配置JSON',
    sort_order       int         default 0                 null comment '排序序号',
    visible          tinyint     default 1                 null comment '是否可见',
    create_time      datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_table_field
        unique (table_config_id, field_id)
)
    comment '表格列绑定表' charset = utf8mb4;

create index idx_field_id
    on table_column_binding (field_id);

create index idx_table_config_id
    on table_column_binding (table_config_id);

