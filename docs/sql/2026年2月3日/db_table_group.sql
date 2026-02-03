create table db_table_group
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    parent_id   bigint   default 0                 null comment '父分组ID，0表示顶级分组',
    group_name  varchar(100)                       not null comment '分组名称',
    group_code  varchar(50)                        null comment '分组编码（可选）',
    description varchar(500)                       null comment '分组描述',
    sort_order  int      default 0                 null comment '排序号',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     tinyint  default 0                 not null comment '逻辑删除：0-存在，1-删除'
)
    comment '库表分组表';

create index idx_parent_id
    on db_table_group (parent_id);

