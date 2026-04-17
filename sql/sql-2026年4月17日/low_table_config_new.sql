create table low_table_config_new
(
    id             bigint auto_increment comment '表格ID'
        primary key,
    table_name     varchar(100)                       not null comment '表格名称',
    table_code     varchar(100)                       not null comment '表格编码（唯一）',
    dataset_id     bigint                             null comment '关联数据集ID',
    column_display json                               not null comment '列展示配置',
    table_config   json                               null comment '表格配置（分页、边框等）',
    status         tinyint  default 1                 null comment '状态',
    deleted        tinyint  default 0                 null comment '逻辑删除',
    create_by      varchar(64)                        null comment '创建者',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint table_code
        unique (table_code),
    constraint fk_table_config_dataset
        foreign key (dataset_id) references low_dataset_config (id)
            on delete set null
)
    comment '表格配置表（重构版）';

create index idx_dataset_id
    on low_table_config_new (dataset_id);

create index idx_table_code
    on low_table_config_new (table_code);

INSERT INTO lowcode_platform.low_table_config_new (table_name, table_code, dataset_id, column_display, table_config, status, deleted, create_by, create_time, update_time) VALUES ('用户列表表格', 'user_list_table', 1, '[{"align": "center", "fixed": "left", "label": "ID", "width": 80, "sortable": false, "fieldCode": "id"}, {"align": "left", "label": "用户名", "width": 150, "sortable": true, "fieldCode": "username"}, {"align": "left", "label": "昵称", "width": 120, "sortable": false, "fieldCode": "nickname"}, {"align": "left", "label": "邮箱", "width": 200, "sortable": false, "fieldCode": "email"}, {"align": "center", "label": "手机号", "width": 150, "sortable": false, "fieldCode": "phone"}, {"align": "center", "label": "状态", "width": 100, "sortable": false, "fieldCode": "status", "formatter": "tag", "formatterConfig": {"typeMap": {"0": "info", "1": "success"}, "labelMap": {"0": "禁用", "1": "启用"}}}, {"align": "center", "label": "创建时间", "width": 180, "sortable": false, "fieldCode": "create_time", "formatter": "datetime"}]', '{"border": true, "height": "auto", "stripe": true, "pageSize": 20, "pageSizes": [10, 20, 50, 100], "selection": true, "showIndex": true, "pagination": true, "selectionType": "multiple"}', 1, 0, 'admin', '2026-03-04 18:30:39', '2026-03-04 18:30:39');
