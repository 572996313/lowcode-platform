create table db_table
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    table_schema  varchar(128)                       not null comment '数据库/模式名称',
    table_name    varchar(128)                       not null comment '表名',
    table_type    varchar(20)                        not null comment '表类型：TABLE-表，VIEW-视图',
    table_comment varchar(500)                       null comment '表注释',
    sync_status   tinyint  default 0                 not null comment '同步状态：0-未同步，1-已同步',
    sync_time     datetime                           null comment '最后同步时间',
    group_id      bigint                             null comment '所属分组ID',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted       tinyint  default 0                 not null comment '逻辑删除：1-删除，0-存在',
    constraint uk_schema_table
        unique (table_schema, table_name, deleted)
)
    comment '库表管理表';

create index idx_group_id
    on db_table (group_id);

create index idx_sync_status
    on db_table (sync_status);

create index idx_table_name
    on db_table (table_name);

create index idx_table_type
    on db_table (table_type);

INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (1, 'lowcode_platform', 'db_table', 'TABLE', '库表管理表', 1, '2026-02-01 14:22:16', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (2, 'lowcode_platform', 'db_table_field', 'TABLE', '字段管理表', 1, '2026-02-01 14:22:43', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (3, 'lowcode_platform', 'low_button_config', 'TABLE', '按钮配置表', 1, '2026-02-01 14:22:45', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (4, 'lowcode_platform', 'low_component_template', 'TABLE', '组件模板表', 1, '2026-02-01 14:22:49', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (5, 'lowcode_platform', 'low_datasource_config', 'TABLE', '数据源配置表', 1, '2026-02-01 14:22:50', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (6, 'lowcode_platform', 'low_form_config', 'TABLE', '表单配置表', 1, '2026-02-01 14:22:52', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (7, 'lowcode_platform', 'low_form_field', 'TABLE', '表单字段表', 1, '2026-02-01 14:22:53', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (8, 'lowcode_platform', 'low_page_config', 'TABLE', '页面配置表', 1, '2026-02-01 14:22:55', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (9, 'lowcode_platform', 'low_page_template', 'TABLE', '页面模板表', 1, '2026-02-01 14:22:56', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (10, 'lowcode_platform', 'low_search_config', 'TABLE', '查询条件配置表', 1, '2026-02-01 14:22:58', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (11, 'lowcode_platform', 'low_table_column', 'TABLE', '表格列配置表', 1, '2026-02-01 14:23:01', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (12, 'lowcode_platform', 'low_table_config', 'TABLE', '表格配置表', 1, '2026-02-01 14:23:02', null, '2026-02-01 14:21:56', '2026-02-01 14:21:56', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (13, 'lowcode_platform', 'db_field_widget_config', 'TABLE', '字段-控件绑定配置表', 1, '2026-02-01 16:24:42', null, '2026-02-01 16:23:59', '2026-02-01 16:23:59', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (14, 'lowcode_platform', 'db_field_widget_template', 'TABLE', '字段控件配置模板表', 0, null, null, '2026-02-02 20:45:01', '2026-02-02 20:45:01', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (15, 'lowcode_platform', 'low_dropdown_config', 'TABLE', '下拉配置表', 0, null, null, '2026-02-02 20:45:01', '2026-02-02 20:45:01', 0);
INSERT INTO lowcode_platform.db_table (id, table_schema, table_name, table_type, table_comment, sync_status, sync_time, group_id, create_time, update_time, deleted) VALUES (16, 'lowcode_platform', 'low_form_template', 'TABLE', '表单模板表', 0, null, null, '2026-02-02 20:45:01', '2026-02-02 20:45:01', 0);
