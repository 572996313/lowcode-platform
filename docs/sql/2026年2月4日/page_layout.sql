create table page_layout
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    layout_name varchar(100)                       not null comment '布局名称',
    layout_code varchar(100)                       not null comment '布局编码（唯一）',
    layout_type varchar(50)                        not null comment '布局类型(top-bottom/left-right/tree-table/tabs)',
    area_config text                               null comment '区域配置JSON',
    published   tinyint  default 0                 null comment '是否已发布',
    route_path  varchar(200)                       null comment '路由路径',
    menu_id     bigint                             null comment '关联菜单ID',
    status      tinyint  default 1                 null comment '状态（1=启用，0=禁用）',
    deleted     tinyint  default 0                 null comment '是否删除（1=删除，0=存在）',
    create_by   varchar(50)                        null comment '创建者',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_layout_code
        unique (layout_code)
)
    comment '页面布局配置表' charset = utf8mb4;

create index idx_layout_type
    on page_layout (layout_type);

create index idx_menu_id
    on page_layout (menu_id);

