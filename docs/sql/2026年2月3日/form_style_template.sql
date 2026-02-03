create table form_style_template
(
    id            bigint auto_increment
        primary key,
    template_name varchar(100)                       not null comment '模板名称',
    template_code varchar(100)                       not null comment '模板编码（唯一）',
    description   varchar(500)                       null comment '模板描述',
    style_config  text                               null comment '样式配置JSON',
    is_system     tinyint  default 0                 null comment '是否系统模板',
    status        tinyint  default 1                 null comment '状态（1=启用，0=禁用）',
    sort_order    int      default 0                 null comment '排序',
    deleted       tinyint  default 0                 null comment '是否删除',
    create_time   datetime default CURRENT_TIMESTAMP null,
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint uk_template_code
        unique (template_code)
)
    comment '表单样式模板表';

INSERT INTO lowcode_platform.form_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (1, '默认样式', 'form_default', '标准表单样式', '{"labelWidth":100,"labelPosition":"right","layoutCols":2,"size":"default","border":false}', 1, 1, 1, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
INSERT INTO lowcode_platform.form_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (2, '紧凑样式', 'form_compact', '紧凑型表单', '{"labelWidth":80,"labelPosition":"right","layoutCols":3,"size":"small","border":false}', 1, 1, 2, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
INSERT INTO lowcode_platform.form_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (3, '纵向样式', 'form_vertical', '标签在上方', '{"labelWidth":100,"labelPosition":"top","layoutCols":1,"size":"default","border":false}', 1, 1, 3, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
INSERT INTO lowcode_platform.form_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (4, '大尺寸样式', 'form_large', '大尺寸组件', '{"labelWidth":120,"labelPosition":"right","layoutCols":2,"size":"large","border":false}', 1, 1, 4, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
INSERT INTO lowcode_platform.form_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (5, '带边框样式', 'form_bordered', '带边框表单', '{"labelWidth":100,"labelPosition":"right","layoutCols":2,"size":"default","border":true,"backgroundColor":"#f5f7fa"}', 1, 1, 5, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
