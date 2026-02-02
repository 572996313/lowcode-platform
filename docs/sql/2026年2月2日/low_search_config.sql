create table low_search_config
(
    id            bigint auto_increment comment '查询ID'
        primary key,
    page_id       bigint                                null comment '所属页面ID',
    table_id      bigint                                null comment '关联表格ID',
    field_code    varchar(100)                          null comment '字段编码',
    field_label   varchar(100)                          null comment '字段标签',
    field_type    varchar(50)                           null comment '字段类型(input/select/date/daterange/cascader等)',
    search_type   varchar(50) default 'eq'              null comment '查询类型(eq/ne/like/likeLeft/likeRight/gt/ge/lt/le/between/in/notIn/isNull/isNotNull)',
    default_value varchar(500)                          null comment '默认值',
    placeholder   varchar(200)                          null comment '占位符',
    options_json  text                                  null comment '选项配置JSON',
    props_json    text                                  null comment '组件属性JSON',
    span          int         default 6                 null comment '栅格占位',
    sort_order    int         default 0                 null comment '排序',
    is_visible    tinyint     default 1                 null comment '是否显示',
    is_advanced   tinyint     default 0                 null comment '是否高级查询(折叠)',
    deleted       tinyint     default 0                 null comment '是否删除',
    create_time   datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '查询条件配置表';

create index idx_page_id
    on low_search_config (page_id);

create index idx_table_id
    on low_search_config (table_id);

