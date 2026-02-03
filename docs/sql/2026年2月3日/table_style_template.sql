create table table_style_template
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
    comment '表格样式模板表';

INSERT INTO lowcode_platform.table_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (1, '默认样式', 'table_default', '标准表格样式', '{"border":true,"stripe":true,"size":"default","pagination":true,"pageSize":10}', 1, 1, 1, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
INSERT INTO lowcode_platform.table_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (2, '简约样式', 'table_simple', '无边框无斑马纹', '{"border":false,"stripe":false,"size":"default","pagination":true,"pageSize":10}', 1, 1, 2, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
INSERT INTO lowcode_platform.table_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (3, '紧凑样式', 'table_compact', '紧凑型表格', '{"border":true,"stripe":true,"size":"small","pagination":true,"pageSize":20}', 1, 1, 3, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
INSERT INTO lowcode_platform.table_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (4, '大尺寸样式', 'table_large', '大尺寸表格', '{"border":true,"stripe":true,"size":"large","pagination":true,"pageSize":5}', 1, 1, 4, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
INSERT INTO lowcode_platform.table_style_template (id, template_name, template_code, description, style_config, is_system, status, sort_order, deleted, create_time, update_time) VALUES (5, '不分页样式', 'table_no_page', '不分页表格', '{"border":true,"stripe":true,"size":"default","pagination":false}', 1, 1, 5, 0, '2026-02-03 00:25:53', '2026-02-03 00:25:53');
