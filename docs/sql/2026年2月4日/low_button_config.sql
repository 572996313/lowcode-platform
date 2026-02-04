create table low_button_config
(
    id                 bigint auto_increment comment '按钮ID'
        primary key,
    page_id            bigint                                null comment '所属页面ID',
    form_id            bigint                                null comment '关联表单ID',
    table_id           bigint                                null comment '关联表格ID',
    button_name        varchar(100)                          null comment '按钮名称',
    button_code        varchar(100)                          null comment '按钮编码',
    component_category varchar(20) default 'business'        null comment '组件分类: common=通用组件库, business=业务组件库',
    component_tags     text                                  null comment '组件标签JSON: ["system","user","create"]',
    position           varchar(50)                           null comment '位置(toolbar/row/form/dialog/footer)',
    button_type        varchar(50) default 'default'         null comment '按钮类型(primary/success/warning/danger/info/default)',
    button_size        varchar(20) default 'default'         null comment '按钮尺寸(large/default/small)',
    icon               varchar(100)                          null comment '图标',
    is_plain           tinyint     default 0                 null comment '是否朴素按钮',
    is_round           tinyint     default 0                 null comment '是否圆角按钮',
    is_circle          tinyint     default 0                 null comment '是否圆形按钮',
    is_loading         tinyint     default 0                 null comment '是否加载中',
    is_disabled        tinyint     default 0                 null comment '是否禁用',
    action_type        varchar(50)                           null comment '动作类型(api/dialog/drawer/route/link/custom/confirm)',
    action_config      text                                  null comment '动作配置JSON',
    confirm_config     text                                  null comment '确认框配置JSON',
    perms              varchar(100)                          null comment '权限标识',
    sort_order         int         default 0                 null comment '排序',
    is_visible         tinyint     default 1                 null comment '是否显示',
    show_condition     text                                  null comment '显示条件表达式',
    status             tinyint(1)  default 1                 null comment '状态: 1=启用, 0=禁用',
    deleted            tinyint     default 0                 null comment '是否删除',
    create_by          varchar(64)                           null comment '创建者',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '按钮配置表';

create index idx_btn_category
    on low_button_config (component_category);

create index idx_button_code
    on low_button_config (button_code);

create index idx_form_id
    on low_button_config (form_id);

create index idx_page_id
    on low_button_config (page_id);

create index idx_table_id
    on low_button_config (table_id);

INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (1, null, null, null, '保存', 'btn_save', 'common', null, 'form', 'primary', 'default', null, 0, 0, 0, 0, 0, null, null, null, null, 1, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (2, null, null, null, '取消', 'btn_cancel', 'common', null, 'form', 'default', 'default', null, 0, 0, 0, 0, 0, null, null, null, null, 2, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (3, null, null, null, '提交', 'btn_submit', 'common', null, 'form', 'primary', 'default', null, 0, 0, 0, 0, 0, null, null, null, null, 3, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (4, null, null, null, '刷新', 'btn_refresh', 'common', null, 'toolbar', 'default', 'default', null, 0, 0, 0, 0, 0, null, null, null, null, 1, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (5, null, null, null, '重置', 'btn_reset', 'common', null, 'toolbar', 'default', 'default', null, 0, 0, 0, 0, 0, null, null, null, null, 2, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (6, null, null, null, '新增', 'btn_add', 'business', null, 'toolbar', 'primary', 'default', null, 0, 0, 0, 0, 0, null, null, null, null, 1, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (7, null, null, null, '编辑', 'btn_edit', 'business', null, 'row', 'primary', 'small', null, 0, 0, 0, 0, 0, null, null, null, null, 1, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (8, null, null, null, '删除', 'btn_delete', 'business', null, 'row', 'danger', 'small', null, 0, 0, 0, 0, 0, null, null, null, null, 2, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (9, null, null, null, '查询', 'btn_query', 'business', null, 'toolbar', 'success', 'default', null, 0, 0, 0, 0, 0, null, null, null, null, 3, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
INSERT INTO lowcode_platform.low_button_config (id, page_id, form_id, table_id, button_name, button_code, component_category, component_tags, position, button_type, button_size, icon, is_plain, is_round, is_circle, is_loading, is_disabled, action_type, action_config, confirm_config, perms, sort_order, is_visible, show_condition, status, deleted, create_by, create_time, update_time) VALUES (10, null, null, null, '导出', 'btn_export', 'business', null, 'toolbar', 'success', 'default', null, 0, 0, 0, 0, 0, null, null, null, null, 4, 1, null, 1, 0, null, '2026-01-31 00:16:23', '2026-01-31 00:16:23');
