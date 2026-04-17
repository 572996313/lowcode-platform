create table low_form_config_new
(
    id            bigint auto_increment comment '表单ID'
        primary key,
    form_name     varchar(100)                                       not null comment '表单名称',
    form_code     varchar(100)                                       not null comment '表单编码（唯一）',
    dataset_id    bigint                                             null comment '关联数据集ID',
    field_widgets json                                               not null comment '字段控件映射',
    layout_type   enum ('horizontal', 'vertical', 'inline', 'grid')  null comment '布局类型',
    layout_config json                                               null comment '布局配置',
    form_type     enum ('add', 'edit', 'search', 'detail', 'dialog') null comment '表单类型',
    status        tinyint  default 1                                 null comment '状态',
    deleted       tinyint  default 0                                 null comment '逻辑删除',
    create_by     varchar(64)                                        null comment '创建者',
    create_time   datetime default CURRENT_TIMESTAMP                 null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP                 null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint form_code
        unique (form_code),
    constraint fk_form_config_dataset
        foreign key (dataset_id) references low_dataset_config (id)
            on delete set null
)
    comment '表单配置表（重构版）';

create index idx_dataset_id
    on low_form_config_new (dataset_id);

create index idx_form_code
    on low_form_config_new (form_code);

INSERT INTO lowcode_platform.low_form_config_new (form_name, form_code, dataset_id, field_widgets, layout_type, layout_config, form_type, status, deleted, create_by, create_time, update_time) VALUES ('用户编辑表单', 'user_edit_form', 1, '[{"fieldCode": "username", "widgetType": "input", "widgetConfig": {"span": 12, "required": true, "labelWidth": 100, "placeholder": "请输入用户名"}}, {"fieldCode": "nickname", "widgetType": "input", "widgetConfig": {"span": 12, "required": false, "labelWidth": 100, "placeholder": "请输入昵称"}}, {"fieldCode": "email", "widgetType": "input", "widgetConfig": {"span": 12, "required": false, "labelWidth": 100, "placeholder": "请输入邮箱"}}, {"fieldCode": "phone", "widgetType": "input", "widgetConfig": {"span": 12, "required": false, "labelWidth": 100, "placeholder": "请输入手机号"}}, {"fieldCode": "status", "widgetType": "select", "widgetConfig": {"span": 12, "options": [{"label": "启用", "value": 1}, {"label": "禁用", "value": 0}], "required": false, "labelWidth": 100, "placeholder": "请选择状态"}}]', 'horizontal', null, 'edit', 1, 0, 'admin', '2026-03-04 18:30:39', '2026-03-04 18:30:39');
