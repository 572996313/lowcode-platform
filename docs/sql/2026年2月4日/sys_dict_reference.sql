create table sys_dict_reference
(
    id                 bigint auto_increment comment '主键ID'
        primary key,
    dict_category_code varchar(64)                        not null comment '字典分类编码',
    resource_type      varchar(32)                        not null comment '资源类型: form_field/table_column/form_template/table_template',
    resource_id        bigint                             not null comment '资源ID',
    resource_name      varchar(128)                       null comment '资源名称（冗余）',
    page_id            bigint                             null comment '所属页面ID',
    page_name          varchar(128)                       null comment '所属页面名称（冗余）',
    field_code         varchar(64)                        null comment '字段编码',
    field_label        varchar(128)                       null comment '字段标签',
    reference_path     varchar(255)                       null comment '引用路径（JSON Path）',
    created_at         datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at         datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_dict_resource
        unique (dict_category_code, resource_type, resource_id, field_code)
)
    comment '字典引用关系表';

create index idx_dict_code
    on sys_dict_reference (dict_category_code);

create index idx_page_id
    on sys_dict_reference (page_id);

create index idx_resource
    on sys_dict_reference (resource_type, resource_id);

