create table low_component_template
(
    id              bigint auto_increment comment '模板ID'
        primary key,
    template_name   varchar(100)                       not null comment '模板名称',
    template_code   varchar(100)                       null comment '模板编码',
    component_type  varchar(50)                        null comment '组件类型(container/table/form/custom)',
    config_template text                               null comment '组件配置JSON模板',
    category        varchar(50)                        null comment '分类(layout/content)',
    preview_image   varchar(500)                       null comment '预览图URL',
    description     varchar(500)                       null comment '描述',
    keywords        varchar(200)                       null comment '关键词，逗号分隔',
    is_system       tinyint  default 0                 null comment '系统模板标识',
    sort_order      int      default 0                 null comment '排序',
    status          tinyint  default 1                 null comment '状态',
    deleted         tinyint  default 0                 null comment '删除标志',
    create_by       varchar(64)                        null,
    create_time     datetime default CURRENT_TIMESTAMP null,
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint template_code
        unique (template_code)
)
    comment '组件模板表' charset = utf8mb4;

create index idx_category
    on low_component_template (category);

create index idx_type
    on low_component_template (component_type);

INSERT INTO lowcode_platform.low_component_template (id, template_name, template_code, component_type, config_template, category, preview_image, description, keywords, is_system, sort_order, status, deleted, create_by, create_time, update_time) VALUES (1, '行布局', 'layout_row', 'container', null, 'layout', null, '横向排列子组件', 'row,行,横向', 1, 1, 1, 0, null, '2026-01-28 17:01:18', '2026-01-28 17:01:18');
INSERT INTO lowcode_platform.low_component_template (id, template_name, template_code, component_type, config_template, category, preview_image, description, keywords, is_system, sort_order, status, deleted, create_by, create_time, update_time) VALUES (2, '列布局', 'layout_column', 'container', null, 'layout', null, '纵向排列子组件', 'column,列,纵向', 1, 2, 1, 0, null, '2026-01-28 17:01:18', '2026-01-28 17:01:18');
INSERT INTO lowcode_platform.low_component_template (id, template_name, template_code, component_type, config_template, category, preview_image, description, keywords, is_system, sort_order, status, deleted, create_by, create_time, update_time) VALUES (3, 'Tab页签', 'layout_tabs', 'container', null, 'layout', null, '创建标签页容器', 'tabs,标签页', 1, 3, 1, 0, null, '2026-01-28 17:01:18', '2026-01-28 17:01:18');
INSERT INTO lowcode_platform.low_component_template (id, template_name, template_code, component_type, config_template, category, preview_image, description, keywords, is_system, sort_order, status, deleted, create_by, create_time, update_time) VALUES (4, '卡片', 'layout_card', 'container', null, 'layout', null, '卡片容器', 'card,卡片', 1, 4, 1, 0, null, '2026-01-28 17:01:18', '2026-01-28 17:01:18');
INSERT INTO lowcode_platform.low_component_template (id, template_name, template_code, component_type, config_template, category, preview_image, description, keywords, is_system, sort_order, status, deleted, create_by, create_time, update_time) VALUES (5, '可折叠区', 'layout_collapsible', 'container', null, 'layout', null, '可折叠的内容区域', 'collapsible,折叠', 1, 5, 1, 0, null, '2026-01-28 17:01:18', '2026-01-28 17:01:18');
INSERT INTO lowcode_platform.low_component_template (id, template_name, template_code, component_type, config_template, category, preview_image, description, keywords, is_system, sort_order, status, deleted, create_by, create_time, update_time) VALUES (6, '表格', 'content_table', 'table', null, 'content', null, '数据表格', 'table,表格', 1, 101, 1, 0, null, '2026-01-28 17:01:18', '2026-01-28 17:01:18');
INSERT INTO lowcode_platform.low_component_template (id, template_name, template_code, component_type, config_template, category, preview_image, description, keywords, is_system, sort_order, status, deleted, create_by, create_time, update_time) VALUES (7, '表单', 'content_form', 'form', null, 'content', null, '数据表单', 'form,表单', 1, 102, 1, 0, null, '2026-01-28 17:01:18', '2026-01-28 17:01:18');
INSERT INTO lowcode_platform.low_component_template (id, template_name, template_code, component_type, config_template, category, preview_image, description, keywords, is_system, sort_order, status, deleted, create_by, create_time, update_time) VALUES (8, '自定义HTML', 'content_custom', 'custom', null, 'content', null, '自定义HTML组件', 'custom,自定义', 1, 103, 1, 0, null, '2026-01-28 17:01:18', '2026-01-28 17:01:18');
