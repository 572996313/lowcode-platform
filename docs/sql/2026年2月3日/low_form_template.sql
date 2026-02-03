create table low_form_template
(
    id            bigint auto_increment comment '主键ID'
        primary key,
    template_name varchar(100)                       not null comment '模板名称',
    template_code varchar(50)                        not null comment '模板编码（唯一，对应前端组件名）',
    description   varchar(500)                       null comment '模板描述',
    preview_image varchar(500)                       null comment '预览图URL',
    is_system     tinyint  default 0                 null comment '是否系统模板（0=否 1=是）',
    status        tinyint  default 1                 null comment '状态（0=禁用 1=启用）',
    sort_order    int      default 0                 null comment '排序序号',
    create_by     varchar(50)                        null comment '创建者',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted       tinyint  default 0                 null comment '是否删除（0=否 1=是）',
    constraint uk_template_code
        unique (template_code)
)
    comment '表单模板表' charset = utf8mb4;

create index idx_status
    on low_form_template (status);

INSERT INTO lowcode_platform.low_form_template (id, template_name, template_code, description, preview_image, is_system, status, sort_order, create_by, create_time, update_time, deleted) VALUES (1, '简单表单模板', 'simple', '适用于基础信息录入，包含标题和分割线', null, 1, 1, 1, null, '2026-02-01 20:20:48', '2026-02-01 20:20:48', 0);
INSERT INTO lowcode_platform.low_form_template (id, template_name, template_code, description, preview_image, is_system, status, sort_order, create_by, create_time, update_time, deleted) VALUES (2, '页签表单模板', 'tab', '适用于多分类信息录入，使用页签分组', null, 1, 1, 2, null, '2026-02-01 20:20:48', '2026-02-01 20:20:48', 0);
INSERT INTO lowcode_platform.low_form_template (id, template_name, template_code, description, preview_image, is_system, status, sort_order, create_by, create_time, update_time, deleted) VALUES (3, '卡片表单模板', 'card', '适用于分组信息录入，使用卡片容器', null, 1, 1, 3, null, '2026-02-01 20:20:48', '2026-02-01 20:20:48', 0);
