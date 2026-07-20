create table low_page_config_new
(
    id             bigint auto_increment comment '页面ID'
        primary key,
    page_name      varchar(100)                                             not null comment '页面名称',
    page_code      varchar(100)                                             not null comment '页面编码（唯一）',
    dataset_id     bigint                                                   null comment '关联数据集ID',
    display_fields json                                                     null comment '要显示的字段列表',
    layout_type    enum ('tree-table', 'top-bottom', 'free-canvas', 'tabs') null comment '布局类型',
    layout_config  json                                                     null comment '布局配置',
    route_path     varchar(200)                                             null comment '路由路径',
    published      tinyint  default 0                                       null comment '是否已发布',
    config_json    json                                                     null comment '扩展配置',
    status         tinyint  default 1                                       null comment '状态',
    deleted        tinyint  default 0                                       null comment '逻辑删除',
    create_by      varchar(64)                                              null comment '创建者',
    create_time    datetime default CURRENT_TIMESTAMP                       null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP                       null on update CURRENT_TIMESTAMP comment '更新时间',
    publish_time   datetime                                                 null comment '发布时间',
    constraint page_code
        unique (page_code),
    constraint fk_page_config_dataset
        foreign key (dataset_id) references low_dataset_config (id)
            on delete set null
)
    comment '页面配置表（重构版）';

create index idx_dataset_id
    on low_page_config_new (dataset_id);

create index idx_page_code
    on low_page_config_new (page_code);

create index idx_published
    on low_page_config_new (published);

INSERT INTO lowcode_platform.low_page_config_new (id, page_name, page_code, dataset_id, display_fields, layout_type, layout_config, route_path, published, config_json, status, deleted, create_by, create_time, update_time, publish_time) VALUES (1, '用户列表页面', 'user_list_page', 1, '[{"label": "ID", "order": 1, "visible": true, "fieldCode": "id"}, {"label": "用户名", "order": 2, "visible": true, "fieldCode": "username"}, {"label": "昵称", "order": 3, "visible": true, "fieldCode": "nickname"}, {"label": "邮箱", "order": 4, "visible": true, "fieldCode": "email"}, {"label": "手机号", "order": 5, "visible": true, "fieldCode": "phone"}, {"label": "状态", "order": 6, "visible": true, "fieldCode": "status"}]', 'top-bottom', null, '/user/list', 1, null, 1, 0, 'admin', '2026-03-04 18:30:39', '2026-03-04 18:30:39', null);
