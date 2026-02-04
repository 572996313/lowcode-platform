create table db_field_widget_template
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    table_id      bigint                             not null comment '所属表ID',
    field_id      bigint                             not null comment '字段ID',
    template_name varchar(100)                       not null comment '模板名称',
    template_code varchar(100)                       null comment '模板编码',
    widget_type   varchar(50)                        not null comment '控件类型',
    widget_config text                               null comment '控件配置JSON',
    is_system     tinyint  default 0                 not null comment '是否系统预设',
    description   varchar(500)                       null comment '模板描述',
    enabled       tinyint  default 1                 not null comment '是否启用',
    sort_order    int      default 0                 not null comment '排序序号',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted       tinyint  default 0                 not null comment '逻辑删除',
    constraint uk_field_template
        unique (field_id, template_code, deleted)
)
    comment '字段控件配置模板表';

create index idx_field_id
    on db_field_widget_template (field_id);

create index idx_table_id
    on db_field_widget_template (table_id);

create index idx_template_code
    on db_field_widget_template (template_code);

INSERT INTO lowcode_platform.db_field_widget_template (id, table_id, field_id, template_name, template_code, widget_type, widget_config, is_system, description, enabled, sort_order, create_time, update_time, deleted) VALUES (1, 5, 72, 'df', 'df', 'textarea', '{"label":"数据源ID","placeholder":"请输入数据源ID","required":true,"span":12}', 0, '', 1, 0, '2026-02-01 17:18:08', '2026-02-01 17:18:28', 1);
