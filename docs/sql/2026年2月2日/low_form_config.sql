create table low_form_config
(
    id                 bigint auto_increment comment '表单ID'
        primary key,
    form_name          varchar(100)                          not null comment '表单名称',
    form_code          varchar(100)                          null comment '表单编码',
    form_type          varchar(50)                           null comment '表单类型(add/edit/search/detail/dialog)',
    layout_type        varchar(50) default 'horizontal'      null comment '布局类型(horizontal/vertical/inline)',
    layout_cols        int         default 2                 null comment '表单列数',
    label_width        int         default 100               null comment '标签宽度',
    label_position     varchar(20) default 'right'           null comment '标签位置(left/right/top)',
    size               varchar(20) default 'default'         null comment '组件尺寸(large/default/small)',
    config_json        text                                  null comment '表单配置JSON',
    rules_json         text                                  null comment '校验规则JSON',
    status             tinyint     default 1                 null comment '状态',
    deleted            tinyint     default 0                 null comment '是否删除',
    create_by          varchar(64)                           null comment '创建者',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    component_category varchar(20) default 'business'        null comment 'ç»„ä»¶åˆ†ç±»: common=é€šç”¨ç»„ä»¶åº“, business=ä¸šåŠ¡ç»„ä»¶åº“',
    component_tags     text                                  null comment 'ç»„ä»¶æ ‡ç­¾JSON: ["system","user"]',
    constraint form_code
        unique (form_code)
)
    comment '表单配置表';

create index idx_form_category
    on low_form_config (component_category);

create index idx_form_code
    on low_form_config (form_code);

INSERT INTO lowcode_platform.low_form_config (id, template_id, source_type, form_name, form_code, table_id, style_template_id, template_code, form_type, layout_type, layout_cols, label_width, label_position, size, config_json, rules_json, status, deleted, create_by, create_time, update_time, component_category, component_tags) VALUES (14, null, 'custom', '通用新增表单', 'common_add_form', null, null, null, 'add', 'horizontal', 2, 100, 'right', 'default', null, null, 1, 0, null, '2026-01-31 08:48:51', '2026-01-31 08:48:51', 'common', '["system","create"]');
INSERT INTO lowcode_platform.low_form_config (id, template_id, source_type, form_name, form_code, table_id, style_template_id, template_code, form_type, layout_type, layout_cols, label_width, label_position, size, config_json, rules_json, status, deleted, create_by, create_time, update_time, component_category, component_tags) VALUES (15, null, 'custom', '通用编辑表单', 'common_edit_form', null, null, null, 'edit', 'horizontal', 2, 100, 'right', 'default', null, null, 1, 0, null, '2026-01-31 08:48:59', '2026-01-31 08:48:59', 'common', '["system","update"]');
INSERT INTO lowcode_platform.low_form_config (id, template_id, source_type, form_name, form_code, table_id, style_template_id, template_code, form_type, layout_type, layout_cols, label_width, label_position, size, config_json, rules_json, status, deleted, create_by, create_time, update_time, component_category, component_tags) VALUES (16, null, 'custom', '通用搜索表单', 'common_search_form', null, null, null, 'search', 'inline', 4, 80, 'left', 'small', null, null, 1, 0, null, '2026-01-31 08:49:08', '2026-01-31 08:49:08', 'common', '["system","search"]');
INSERT INTO lowcode_platform.low_form_config (id, template_id, source_type, form_name, form_code, table_id, style_template_id, template_code, form_type, layout_type, layout_cols, label_width, label_position, size, config_json, rules_json, status, deleted, create_by, create_time, update_time, component_category, component_tags) VALUES (17, null, 'custom', '通用详情表单', 'common_detail_form', null, null, null, 'detail', 'horizontal', 2, 120, 'right', 'default', null, null, 1, 0, null, '2026-01-31 08:49:15', '2026-01-31 08:49:15', 'common', '["system","read"]');
