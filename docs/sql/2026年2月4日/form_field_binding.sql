create table form_field_binding
(
    id            bigint auto_increment comment '绑定ID'
        primary key,
    form_id       bigint                             not null comment '表单ID（关联low_form_config.id）',
    field_id      bigint                             not null comment '字段ID（关联db_table_field.id）',
    widget_type   varchar(50)                        not null comment '控件类型',
    widget_config text                               null comment '控件配置JSON',
    span          int      default 12                null comment '栅格占位格数',
    required      tinyint  default 0                 null comment '是否必填',
    visible       tinyint  default 1                 null comment '是否可见',
    readonly      tinyint  default 0                 null comment '是否只读',
    disabled      tinyint  default 0                 null comment '是否禁用',
    sort_order    int      default 0                 null comment '排序序号',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_form_field
        unique (form_id, field_id)
)
    comment '表单字段绑定表' charset = utf8mb4;

create index idx_field_id
    on form_field_binding (field_id);

create index idx_form_id
    on form_field_binding (form_id);

