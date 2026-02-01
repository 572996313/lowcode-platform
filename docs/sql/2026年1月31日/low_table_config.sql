create table low_table_config
(
    id                 bigint auto_increment comment '表格ID'
        primary key,
    table_name         varchar(100)                           not null comment '表格名称',
    table_code         varchar(100)                           null comment '表格编码',
    data_source_type   varchar(50)  default 'api'             null comment '数据源类型(api/sql/static)',
    api_url            varchar(500)                           null comment 'API地址',
    api_method         varchar(10)  default 'GET'             null comment '请求方法',
    api_params_json    text                                   null comment 'API参数配置JSON',
    sql_content        text                                   null comment 'SQL语句(当数据源为sql时)',
    is_pagination      tinyint      default 1                 null comment '是否分页',
    page_size          int          default 10                null comment '每页条数',
    page_sizes         varchar(100) default '10,20,50,100'    null comment '每页条数选项',
    is_selection       tinyint      default 0                 null comment '是否多选',
    selection_type     varchar(20)  default 'checkbox'        null comment '选择类型(checkbox/radio)',
    is_index           tinyint      default 1                 null comment '是否显示序号',
    is_border          tinyint      default 1                 null comment '是否显示边框',
    is_stripe          tinyint      default 1                 null comment '是否斑马纹',
    row_key            varchar(50)  default 'id'              null comment '行数据唯一标识',
    empty_text         varchar(100) default '暂无数据'        null comment '空数据提示',
    height             varchar(50)                            null comment '表格高度',
    max_height         varchar(50)                            null comment '表格最大高度',
    config_json        text                                   null comment '表格配置JSON',
    status             tinyint      default 1                 null comment '状态',
    deleted            tinyint      default 0                 null comment '是否删除',
    create_by          varchar(64)                            null comment '创建者',
    create_time        datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    component_category varchar(20)  default 'business'        null comment 'ç»„ä»¶åˆ†ç±»: common=é€šç”¨ç»„ä»¶åº“, business=ä¸šåŠ¡ç»„ä»¶åº“',
    component_tags     text                                   null comment 'ç»„ä»¶æ ‡ç­¾JSON: ["system","list"]',
    constraint table_code
        unique (table_code)
)
    comment '表格配置表';

create index idx_table_category
    on low_table_config (component_category);

create index idx_table_code
    on low_table_config (table_code);

INSERT INTO lowcode_platform.low_table_config (id, table_name, table_code, data_source_type, api_url, api_method, api_params_json, sql_content, is_pagination, page_size, page_sizes, is_selection, selection_type, is_index, is_border, is_stripe, row_key, empty_text, height, max_height, config_json, status, deleted, create_by, create_time, update_time, component_category, component_tags) VALUES (5, '通用数据表格', 'common_data_table', 'api', null, 'GET', null, null, 1, 20, '10,20,50,100', 0, 'checkbox', 1, 1, 1, 'id', '暂无数据', null, null, null, 1, 0, null, '2026-01-31 08:49:22', '2026-01-31 08:49:22', 'common', '["system","list"]');
INSERT INTO lowcode_platform.low_table_config (id, table_name, table_code, data_source_type, api_url, api_method, api_params_json, sql_content, is_pagination, page_size, page_sizes, is_selection, selection_type, is_index, is_border, is_stripe, row_key, empty_text, height, max_height, config_json, status, deleted, create_by, create_time, update_time, component_category, component_tags) VALUES (6, '通用列表表格', 'common_list_table', 'api', null, 'GET', null, null, 1, 10, '10,20,50,100', 0, 'checkbox', 1, 1, 0, 'id', '暂无数据', null, null, null, 1, 0, null, '2026-01-31 08:49:31', '2026-01-31 08:49:31', 'common', '["system","list"]');
INSERT INTO lowcode_platform.low_table_config (id, table_name, table_code, data_source_type, api_url, api_method, api_params_json, sql_content, is_pagination, page_size, page_sizes, is_selection, selection_type, is_index, is_border, is_stripe, row_key, empty_text, height, max_height, config_json, status, deleted, create_by, create_time, update_time, component_category, component_tags) VALUES (7, '通用报表表格', 'common_report_table', 'sql', null, 'GET', null, null, 0, 10, '10,20,50,100', 0, 'checkbox', 0, 1, 1, 'id', '暂无数据', null, null, null, 1, 0, null, '2026-01-31 08:49:42', '2026-01-31 08:49:42', 'common', '["system","report"]');
