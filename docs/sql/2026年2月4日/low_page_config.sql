create table low_page_config
(
    id              bigint auto_increment comment '页面ID'
        primary key,
    page_name       varchar(100)                       not null comment '页面名称',
    page_code       varchar(100)                       null comment '页面编码',
    page_type       varchar(50)                        null comment '页面类型(list/form/detail/custom)',
    template_id     bigint                             null comment '模板ID',
    layout_type     varchar(50)                        null comment '布局类型(tree-table/top-bottom/left-right/tabs/custom)',
    layout_config   text                               null comment '布局配置JSON',
    config_json     text                               null comment '页面配置JSON',
    config_template text                               null comment '模板配置JSON(v2版本)',
    config_version  int      default 1                 null comment '配置版本(1=旧版 2=新版)',
    status          tinyint  default 1                 null comment '状态(0停用 1启用)',
    remark          varchar(500)                       null comment '备注',
    deleted         tinyint  default 0                 null comment '是否删除',
    create_by       varchar(64)                        null comment '创建者',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    route_path      varchar(200)                       null comment '路由路径',
    published       tinyint  default 0                 null comment '是否已发布(0否 1是)',
    publish_time    datetime                           null comment '发布时间',
    constraint page_code
        unique (page_code)
)
    comment '页面配置表';

create index idx_page_code
    on low_page_config (page_code);

create index idx_published
    on low_page_config (published);

create index idx_route_path
    on low_page_config (route_path);

create index idx_template_id
    on low_page_config (template_id);

INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (34, 'V4用户管理测试', 'v4_user_test', 'list', 1, 'top-bottom', null, null, '{"version":4,"pageType":"list","layoutType":"top-bottom","pageTitle":"用户管理","pageIcon":"User","description":"V4格式测试","areas":[{"id":"toolbar","type":"toolbar","name":"工具栏","enabled":true,"position":"top","config":{"buttons":[{"id":"btn-add","label":"新增","buttonCode":"add","type":"primary","icon":"Plus"}]}},{"id":"search","type":"search","name":"查询区","enabled":true,"position":"top","config":{"collapsible":true,"fields":[{"field":"userName","label":"用户名","type":"input","placeholder":"请输入用户名","width":200}]}},{"id":"content","type":"content","name":"用户列表","enabled":true,"position":"main","config":{"componentType":"table","columns":[{"type":"selection","width":55,"fixed":"left"},{"type":"index","label":"序号","width":60,"fixed":"left"},{"prop":"avatar","label":"头像","type":"image","width":80,"imageConfig":{"width":40,"height":40,"fit":"cover","radius":"50%","preview":true}},{"prop":"userName","label":"姓名","type":"text","width":120,"sortable":true},{"prop":"status","label":"状态","type":"tag","width":100,"tagConfig":{"1":{"text":"启用","type":"success"},"0":{"text":"禁用","type":"info"}}},{"prop":"registerTime","label":"注册时间","type":"datetime","width":180,"dateConfig":{"format":"YYYY-MM-DD HH:mm:ss"},"sortable":true}],"tableConfig":{"stripe":true,"border":true,"pagination":true,"pageSize":20}}}}],"actions":{}}', 1, 1, null, 1, null, '2026-01-31 18:02:44', '2026-02-01 10:44:14', '/v4-user-test', 1, '2026-01-31 18:03:00');
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (35, '示例页面_35', 'page_35', 'list', 1, 'free-canvas', null, '{"version":"free-canvas","pageInfo":{"pageName":"示例页面_35","pageCode":"page_35","pageType":"list","published":false},"canvas":{"width":1200,"height":null,"backgroundColor":"#f5f7fa","gridSize":10,"snapToGrid":true,"zoom":1,"minZoom":1,"maxZoom":1.5},"components":[{"id":"table_1770207595502","name":"表格","type":"table","position":{"x":330,"y":280,"width":700,"height":250},"config":{"dataSource":{"type":"api","api":{"url":"","method":"GET"}},"columns":[],"pagination":true,"pageSize":10},"style":{"backgroundColor":"#fff","borderRadius":"4px"},"enabled":true}]}', '{
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
    }', 10, 1, '自由画布页面', 1, null, '2026-02-01 10:45:41', '2026-02-04 20:23:05', '/ggee', 1, '2026-02-04 20:20:23');
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (36, '新页面', 'page_1770207786175', 'list', null, 'free-canvas', null, '{}', '{}', 10, 1, '自由画布页面', 1, null, '2026-02-04 20:23:11', '2026-02-04 21:55:27', '/55', 1, '2026-02-04 21:42:18');
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (37, '新页面', 'page_1770209036658', 'list', null, 'free-canvas', null, '{}', '{}', 10, 1, '自由画布页面', 1, null, '2026-02-04 20:44:13', '2026-02-04 21:55:29', '/gee3', 1, '2026-02-04 21:41:00');
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (38, '99', 'j44', 'custom', null, 'free-canvas', null, '{}', '{}', 10, 1, '', 0, null, '2026-02-04 21:55:51', '2026-02-04 21:55:51', null, 0, null);
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (39, '123', 'f333', 'custom', null, 'free-canvas', null, '{}', '{}', 10, 1, '', 0, null, '2026-02-04 22:33:59', '2026-02-04 22:33:59', null, 0, null);
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (40, '312', 'd321', 'custom', null, 'free-canvas', null, '{"version":"free-canvas","pageInfo":{"pageName":"312","pageCode":"d321","pageType":"custom","published":false},"canvas":{"width":1200,"height":null,"backgroundColor":"#f5f7fa","gridSize":10,"snapToGrid":true,"zoom":1,"minZoom":1,"maxZoom":1.5},"components":[{"id":"table_1770217501999","name":"表格","type":"table","position":{"x":0,"y":150,"width":700,"height":250},"config":{"dataSource":{"type":"api","api":{"url":"","method":"GET"}},"columns":[],"pagination":true,"pageSize":10},"style":{"backgroundColor":"#fff","borderRadius":"4px"},"enabled":true}]}', '{}', 10, 1, '自由画布页面', 0, null, '2026-02-04 22:54:51', '2026-02-04 23:05:03', '/free-canvas/d321', 1, '2026-02-04 23:05:06');
