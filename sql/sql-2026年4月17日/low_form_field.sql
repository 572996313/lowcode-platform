create table low_form_field
(
    id            bigint auto_increment comment '字段ID'
        primary key,
    form_id       bigint                             not null comment '表单ID',
    slot_id       varchar(50)                        null comment '模板槽位ID（用于基于模板的表单）',
    field_name    varchar(100)                       null comment '字段名称',
    field_code    varchar(100)                       null comment '字段编码',
    field_type    varchar(50)                        null comment '字段类型(input/textarea/select/radio/checkbox/date/time/datetime/upload/editor/cascader/tree-select/switch/slider/rate/color-picker)',
    label         varchar(100)                       null comment '字段标签',
    placeholder   varchar(200)                       null comment '占位符',
    default_value varchar(500)                       null comment '默认值',
    is_required   tinyint  default 0                 null comment '是否必填',
    is_readonly   tinyint  default 0                 null comment '是否只读',
    is_disabled   tinyint  default 0                 null comment '是否禁用',
    is_visible    tinyint  default 1                 null comment '是否显示',
    sort_order    int      default 0                 null comment '排序',
    span          int      default 12                null comment '栅格占位(1-24)',
    label_width   int                                null comment '标签宽度(覆盖表单设置)',
    options_json  text                               null comment '选项配置JSON(下拉框/单选/多选等)',
    rules_json    text                               null comment '校验规则JSON',
    props_json    text                               null comment '组件属性JSON',
    events_json   text                               null comment '事件配置JSON',
    linkage_json  text                               null comment '联动配置JSON',
    deleted       tinyint  default 0                 null comment '是否删除',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '表单字段表';

create index idx_field_code
    on low_form_field (field_code);

create index idx_form_id
    on low_form_field (form_id);

