create table low_dict_item
(
    id          bigint auto_increment comment '字典项ID'
        primary key,
    category_id bigint                             not null comment '所属分类ID',
    item_label  varchar(100)                       not null comment '字典项标签',
    item_value  varchar(200)                       not null comment '字典项值',
    item_color  varchar(20)                        null comment '颜色（用于标签等）',
    item_icon   varchar(100)                       null comment '图标',
    sort_order  int      default 0                 null comment '排序',
    status      tinyint  default 1                 null comment '状态（1=启用，0=禁用）',
    deleted     tinyint  default 0                 null comment '逻辑删除',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint fk_dict_item_category
        foreign key (category_id) references low_dict_category (id)
            on delete cascade
)
    comment '数据字典项表';

create index idx_category_id
    on low_dict_item (category_id);

create index idx_item_value
    on low_dict_item (item_value);

