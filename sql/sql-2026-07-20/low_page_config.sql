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
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (35, '33', '33', 'list', 1, 'top-bottom', null, '{
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
    }', '{
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
    }', 1, 1, null, 1, null, '2026-02-01 10:45:41', '2026-04-16 15:21:33', '/ggee', 0, '2026-02-03 23:09:02');
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (36, '1321', 'f324', 'list', null, 'free-canvas', null, '{}', '{}', 10, 1, '', 1, null, '2026-04-15 17:17:02', '2026-04-16 15:21:31', null, 0, null);
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (37, '4444', 'm3333', 'custom', null, 'free-canvas', null, '{"version":"free-canvas","pageInfo":{"pageName":"4444","pageCode":"m3333","pageType":"custom","published":false},"canvas":{"width":2240,"height":null,"backgroundColor":"#f5f7fa","gridSize":10,"snapToGrid":true,"zoom":1,"minZoom":1,"maxZoom":1.5},"components":[{"id":"table-standard_1776323914553_jmgwse","name":"测试表格","type":"table-standard","position":{"x":0,"y":0,"width":1732,"height":815},"config":{"pageCode":"433","pageName":"测试表格","apiUrl":"","apiMethod":"POST","toolbar":{"buttons":[{"label":"新增","btnType":"primary","icon":"Plus","action":"add","actionConfig":{"type":"openForm","targetCode":"form-standard_1776405902319_3kkgot","openMode":"dialog"},"position":"toolbar"},{"label":"新按钮","btnType":"primary","action":"edit","actionConfig":{"type":"openForm","targetCode":"form-standard_1776405902319_3kkgot","selectionMode":"single"},"position":"toolbar"},{"label":"删除","btnType":"primary","action":"delete","position":"table-column"}]},"searchFields":[{"field":"name","label":"名称","type":"input","placeholder":"请输入名称","clearable":true,"width":444},{"field":"field_1776405923068","label":"新字段","type":"date","clearable":true}],"tableColumns":[{"prop":"name","label":"名称","minWidth":150,"type":"text"},{"prop":"createTime","label":"创建时间","width":180,"type":"date"},{"prop":"status","label":"状态","width":100,"align":"center","type":"tag","tagConfig":{"mapping":{"0":{"text":"禁用","type":"danger"},"1":{"text":"启用","type":"success"}}}},{"prop":"col_5","label":"列544","type":"text","align":"left","minWidth":120},{"prop":"col_1776323933748","label":"新列","width":120,"type":"tag"}],"tableConfig":{"border":true,"stripe":true,"size":"default","showPagination":true,"pageSize":10,"pageSizes":[10,20,50,100],"showIndex":true,"showSelection":true}},"style":{"backgroundColor":"#fff","borderRadius":"4px","padding":"12px"},"enabled":true},{"id":"form-standard_1776405902319_3kkgot","name":"标准表单","type":"form-standard","role":"linked","position":{"x":0,"y":0,"width":700,"height":400},"config":{"pageCode":"","pageName":"标准表单","layout":{"columns":2,"labelWidth":"120px","labelPosition":"right","size":"default","rowGutter":20},"toolbar":{"buttons":[{"label":"保存","btnType":"primary","action":"custom"}]},"groups":[{"title":"基本信息","collapsible":false,"fields":[{"field":"field_1776407880153","label":"新字段1","type":"input","placeholder":"请输入","editable":true},{"field":"field_1776407883348","label":"新字段2","type":"input","placeholder":"请输入","editable":true}]},{"title":"新分组3","collapsible":false,"fields":[{"field":"field_1776407912101","label":"新字段333","type":"input","placeholder":"请输入"}]}]},"style":{"backgroundColor":"#fff","borderRadius":"4px","padding":"12px"},"enabled":true}]}', '{}', 10, 1, '自由画布页面', 1, null, '2026-04-16 14:34:58', '2026-04-21 09:15:07', '/free-canvas/m3333', 0, '2026-04-17 11:33:27');
INSERT INTO lowcode_platform.low_page_config (id, page_name, page_code, page_type, template_id, layout_type, layout_config, config_json, config_template, config_version, status, remark, deleted, create_by, create_time, update_time, route_path, published, publish_time) VALUES (38, '233', 'sd233', 'custom', null, 'free-canvas', null, '{}', '{}', 10, 1, '', 0, null, '2026-07-20 14:18:11', '2026-07-20 15:36:24', null, 0, null);
