create table low_page_template
(
    id              bigint auto_increment comment '模板ID'
        primary key,
    template_name   varchar(100)                       not null comment '模板名称',
    template_code   varchar(100)                       null comment '模板编码',
    template_type   varchar(50)                        null comment '模板类型(list/form/detail/dashboard)',
    layout_type     varchar(50)                        null comment '布局类型(tree-table/top-bottom/left-right/tabs/custom)',
    preview_image   varchar(500)                       null comment '预览图',
    config_json     text                               null comment '模板配置JSON',
    config_template text                               null comment '模板配置JSON(v2版本)',
    description     varchar(500)                       null comment '模板描述',
    is_system       tinyint  default 0                 null comment '是否系统模板',
    status          tinyint  default 1                 null comment '状态',
    deleted         tinyint  default 0                 null comment '是否删除',
    create_by       varchar(64)                        null comment '创建者',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint template_code
        unique (template_code)
)
    comment '页面模板表';

INSERT INTO lowcode_platform.low_page_template (id, template_name, template_code, template_type, layout_type, preview_image, config_json, config_template, description, is_system, status, deleted, create_by, create_time, update_time) VALUES (1, '标准列表页', 'standard-list', 'list', 'top-bottom', null, null, '{
        "version": 2,
        "templateCode": "standard-list",
        "layoutType": "top-bottom",
        "pageInfo": {
            "title": "标准列表页",
            "description": "包含查询条件、工具栏和数据表格"
        },
        "areas": [
            {
                "id": "search-area",
                "type": "search",
                "name": "查询条件区",
                "enabled": true,
                "required": false,
                "config": { "collapsible": true },
                "props": { "fields": [] }
            },
            {
                "id": "toolbar-area",
                "type": "toolbar",
                "name": "工具栏",
                "enabled": true,
                "required": true,
                "props": { "buttons": [] }
            },
            {
                "id": "content-area",
                "type": "content",
                "name": "内容区",
                "enabled": true,
                "required": true,
                "config": {
                    "componentType": "table",
                    "configId": null,
                    "title": "数据列表"
                }
            }
        ]
    }', '标准的列表页面布局，顶部为查询区，中间为工具栏，底部为数据表格。适用于数据展示、筛选和操作场景。', 1, 1, 0, null, '2026-01-30 11:56:24', '2026-01-30 11:56:24');
INSERT INTO lowcode_platform.low_page_template (id, template_name, template_code, template_type, layout_type, preview_image, config_json, config_template, description, is_system, status, deleted, create_by, create_time, update_time) VALUES (2, '左树右表', 'tree-table', 'list', 'tree-table', null, null, '{"areas": [{"id": "tree-area", "name": "树形导航区", "type": "tree", "config": {"title": "导航树", "configId": null, "componentType": "tree"}, "enabled": true, "required": "false"}, {"id": "search-area", "name": "查询条件区", "type": "search", "props": {"fields": []}, "config": {"collapsible": true}, "enabled": true, "required": false}, {"id": "toolbar-area", "name": "工具栏", "type": "toolbar", "props": {"buttons": []}, "enabled": true, "required": "false"}, {"id": "content-area", "name": "内容区", "type": "content", "config": {"title": "数据列表", "configId": null, "componentType": "table"}, "enabled": true, "required": "false"}], "version": 2, "pageInfo": {"title": "左树右表", "description": "左侧树形导航，右侧数据表格"}, "layoutType": "tree-table", "templateCode": "tree-table"}', '左侧显示树形导航结构，右侧显示对应的数据表格。适用于分类管理、层级数据展示等场景。', 1, 1, 0, null, '2026-01-30 11:56:24', '2026-01-31 04:44:27');
