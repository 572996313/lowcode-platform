create table low_table_column
(
    id                       bigint auto_increment comment '列ID'
        primary key,
    table_id                 bigint                                not null comment '表格ID',
    column_name              varchar(100)                          null comment '列名称',
    column_code              varchar(100)                          null comment '列编码(字段名)',
    column_type              varchar(50) default 'text'            null comment '列类型(text/image/link/tag/badge/progress/switch/button/slot/index/selection/expand)',
    label                    varchar(100)                          null comment '列标题',
    width                    int                                   null comment '列宽度',
    min_width                int                                   null comment '最小宽度',
    is_sortable              tinyint     default 0                 null comment '是否可排序',
    sort_orders              varchar(50)                           null comment '排序方式(ascending/descending/null)',
    is_fixed                 varchar(10)                           null comment '固定列(left/right)',
    align                    varchar(10) default 'left'            null comment '对齐方式(left/center/right)',
    header_align             varchar(10)                           null comment '表头对齐方式',
    is_resizable             tinyint     default 1                 null comment '是否可调整宽度',
    is_show_overflow_tooltip tinyint     default 1                 null comment '是否显示溢出提示',
    formatter_type           varchar(50)                           null comment '格式化类型(date/datetime/money/percent/dict/custom)',
    formatter_config         text                                  null comment '格式化配置JSON',
    dict_code                varchar(100)                          null comment '字典编码(当格式化类型为dict时)',
    sort_order               int         default 0                 null comment '排序',
    is_visible               tinyint     default 1                 null comment '是否显示',
    is_export                tinyint     default 1                 null comment '是否导出',
    props_json               text                                  null comment '列属性JSON',
    deleted                  tinyint     default 0                 null comment '是否删除',
    create_time              datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time              datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '表格列配置表';

create index idx_table_id
    on low_table_column (table_id);

INSERT INTO lowcode_platform.low_table_column (id, table_id, column_name, column_code, column_type, label, width, min_width, is_sortable, sort_orders, is_fixed, align, header_align, is_resizable, is_show_overflow_tooltip, formatter_type, formatter_config, dict_code, sort_order, is_visible, is_export, props_json, deleted, create_time, update_time) VALUES (1, 1, '姓名', 'name', 'text', '姓名', null, 120, 0, null, null, 'left', null, 1, 1, null, null, null, 0, 1, 1, null, 1, '2026-01-30 00:23:30', '2026-01-31 04:54:28');
INSERT INTO lowcode_platform.low_table_column (id, table_id, column_name, column_code, column_type, label, width, min_width, is_sortable, sort_orders, is_fixed, align, header_align, is_resizable, is_show_overflow_tooltip, formatter_type, formatter_config, dict_code, sort_order, is_visible, is_export, props_json, deleted, create_time, update_time) VALUES (2, 1, '年龄', 'age', 'text', '年龄', 80, null, 0, null, null, 'center', null, 1, 1, null, null, null, 1, 1, 1, null, 1, '2026-01-30 00:23:30', '2026-01-31 04:54:28');
INSERT INTO lowcode_platform.low_table_column (id, table_id, column_name, column_code, column_type, label, width, min_width, is_sortable, sort_orders, is_fixed, align, header_align, is_resizable, is_show_overflow_tooltip, formatter_type, formatter_config, dict_code, sort_order, is_visible, is_export, props_json, deleted, create_time, update_time) VALUES (3, 1, '邮箱', 'email', 'text', '邮箱', null, 180, 0, null, null, 'left', null, 1, 1, null, null, null, 2, 1, 1, null, 1, '2026-01-30 00:23:30', '2026-01-31 04:54:28');
INSERT INTO lowcode_platform.low_table_column (id, table_id, column_name, column_code, column_type, label, width, min_width, is_sortable, sort_orders, is_fixed, align, header_align, is_resizable, is_show_overflow_tooltip, formatter_type, formatter_config, dict_code, sort_order, is_visible, is_export, props_json, deleted, create_time, update_time) VALUES (4, 1, '状态', 'status', 'text', '状态', 100, null, 0, null, null, 'center', null, 1, 1, null, null, null, 3, 1, 1, null, 1, '2026-01-30 00:23:30', '2026-01-31 04:54:28');
INSERT INTO lowcode_platform.low_table_column (id, table_id, column_name, column_code, column_type, label, width, min_width, is_sortable, sort_orders, is_fixed, align, header_align, is_resizable, is_show_overflow_tooltip, formatter_type, formatter_config, dict_code, sort_order, is_visible, is_export, props_json, deleted, create_time, update_time) VALUES (5, 8, '姓名', 'name', 'text', '姓名', null, 120, 0, null, null, 'left', null, 1, 1, null, null, null, 0, 1, 1, null, 1, '2026-01-31 13:04:34', '2026-01-31 10:14:02');
INSERT INTO lowcode_platform.low_table_column (id, table_id, column_name, column_code, column_type, label, width, min_width, is_sortable, sort_orders, is_fixed, align, header_align, is_resizable, is_show_overflow_tooltip, formatter_type, formatter_config, dict_code, sort_order, is_visible, is_export, props_json, deleted, create_time, update_time) VALUES (6, 8, '年龄', 'age', 'text', '年龄', 80, null, 0, null, null, 'center', null, 1, 1, null, null, null, 1, 1, 1, null, 1, '2026-01-31 13:04:34', '2026-01-31 10:14:02');
INSERT INTO lowcode_platform.low_table_column (id, table_id, column_name, column_code, column_type, label, width, min_width, is_sortable, sort_orders, is_fixed, align, header_align, is_resizable, is_show_overflow_tooltip, formatter_type, formatter_config, dict_code, sort_order, is_visible, is_export, props_json, deleted, create_time, update_time) VALUES (7, 8, '邮箱', 'email', 'text', '邮箱', null, 180, 0, null, null, 'left', null, 1, 1, null, null, null, 2, 1, 1, null, 1, '2026-01-31 13:04:34', '2026-01-31 10:14:02');
INSERT INTO lowcode_platform.low_table_column (id, table_id, column_name, column_code, column_type, label, width, min_width, is_sortable, sort_orders, is_fixed, align, header_align, is_resizable, is_show_overflow_tooltip, formatter_type, formatter_config, dict_code, sort_order, is_visible, is_export, props_json, deleted, create_time, update_time) VALUES (8, 8, '状态', 'status', 'text', '状态', 100, null, 0, null, null, 'center', null, 1, 1, null, null, null, 3, 1, 1, null, 1, '2026-01-31 13:04:34', '2026-01-31 10:14:02');
