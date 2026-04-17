create table low_dict_category
(
    id            bigint auto_increment comment '分类ID'
        primary key,
    category_code varchar(100)                       not null comment '分类编码（唯一）',
    category_name varchar(100)                       not null comment '分类名称',
    description   text                               null comment '描述',
    sort_order    int      default 0                 null comment '排序',
    status        tinyint  default 1                 null comment '状态（1=启用，0=禁用）',
    deleted       tinyint  default 0                 null comment '逻辑删除',
    create_by     varchar(64)                        null comment '创建者',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint category_code
        unique (category_code)
)
    comment '数据字典分类表';

create index idx_category_code
    on low_dict_category (category_code);

create index idx_status
    on low_dict_category (status);

