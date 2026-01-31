-- 初始化页面模板数据
-- 版本: 005
-- 日期: 2026-01-30
-- 描述: 插入7个系统预置模板到 low_page_template 表

USE lowcode_platform;

-- 1. 标准列表页 (standard-list)
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, description, is_system, create_time, update_time)
VALUES (
    '标准列表页',
    'standard-list',
    'list',
    'top-bottom',
    '{
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
    }',
    '标准的列表页面布局，顶部为查询区，中间为工具栏，底部为数据表格。适用于数据展示、筛选和操作场景。',
    1,
    NOW(),
    NOW()
);

-- 2. 左树右表 (tree-table)
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, description, is_system, create_time, update_time)
VALUES (
    '左树右表',
    'tree-table',
    'list',
    'tree-table',
    '{
        "version": 2,
        "templateCode": "tree-table",
        "layoutType": "tree-table",
        "pageInfo": {
            "title": "左树右表",
            "description": "左侧树形导航，右侧数据表格"
        },
        "areas": [
            {
                "id": "tree-area",
                "type": "tree",
                "name": "树形导航区",
                "enabled": true,
                "required": true,
                "config": {
                    "componentType": "tree",
                    "configId": null,
                    "title": "导航树"
                }
            },
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
    }',
    '左侧显示树形导航结构，右侧显示对应的数据表格。适用于分类管理、层级数据展示等场景。',
    1,
    NOW(),
    NOW()
);

-- 3. 多标签页 (tabs-layout)
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, description, is_system, create_time, update_time)
VALUES (
    '多标签页',
    'tabs-layout',
    'complex',
    'tabs',
    '{
        "version": 2,
        "templateCode": "tabs-layout",
        "layoutType": "tabs",
        "pageInfo": {
            "title": "多标签页",
            "description": "多个标签页展示不同内容"
        },
        "areas": [
            {
                "id": "tabs-area",
                "type": "tabs",
                "name": "标签页区",
                "enabled": true,
                "required": true,
                "config": {
                    "tabs": [],
                    "defaultTab": 0
                }
            }
        ]
    }',
    '支持多个标签页切换展示，每个标签页可以包含独立的表单、表格或自定义内容。适用于复杂业务场景。',
    1,
    NOW(),
    NOW()
);

-- 4. 数据仪表盘 (dashboard)
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, description, is_system, create_time, update_time)
VALUES (
    '数据仪表盘',
    'dashboard',
    'dashboard',
    'dashboard',
    '{
        "version": 2,
        "templateCode": "dashboard",
        "layoutType": "dashboard",
        "pageInfo": {
            "title": "数据仪表盘",
            "description": "数据统计和可视化展示"
        },
        "areas": [
            {
                "id": "stats-area",
                "type": "stats",
                "name": "统计卡片区",
                "enabled": true,
                "required": false,
                "config": {
                    "cards": [],
                    "columns": 4
                }
            },
            {
                "id": "charts-area",
                "type": "charts",
                "name": "图表展示区",
                "enabled": true,
                "required": true,
                "config": {
                    "charts": [],
                    "layout": "auto"
                }
            },
            {
                "id": "toolbar-area",
                "type": "toolbar",
                "name": "工具栏",
                "enabled": true,
                "required": false,
                "props": { "buttons": [] }
            }
        ]
    }',
    '数据统计和可视化仪表盘，支持统计卡片、图表等多种数据展示组件。适用于数据分析和监控场景。',
    1,
    NOW(),
    NOW()
);

-- 5. 表单页 (form-page)
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, description, is_system, create_time, update_time)
VALUES (
    '表单页',
    'form-page',
    'form',
    'form',
    '{
        "version": 2,
        "templateCode": "form-page",
        "layoutType": "form",
        "pageInfo": {
            "title": "表单页",
            "description": "数据录入和编辑表单"
        },
        "areas": [
            {
                "id": "content-area",
                "type": "content",
                "name": "表单内容区",
                "enabled": true,
                "required": true,
                "config": {
                    "componentType": "form",
                    "configId": null,
                    "title": "表单"
                }
            },
            {
                "id": "toolbar-area",
                "type": "toolbar",
                "name": "操作按钮区",
                "enabled": true,
                "required": true,
                "props": { "buttons": [] }
            }
        ]
    }',
    '单表单页面布局，适用于数据新增、编辑等场景。',
    1,
    NOW(),
    NOW()
);

-- 6. 详情页 (detail-page)
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, description, is_system, create_time, update_time)
VALUES (
    '详情页',
    'detail-page',
    'detail',
    'detail',
    '{
        "version": 2,
        "templateCode": "detail-page",
        "layoutType": "detail",
        "pageInfo": {
            "title": "详情页",
            "description": "数据详情展示和操作"
        },
        "areas": [
            {
                "id": "header-area",
                "type": "header",
                "name": "详情头部区",
                "enabled": true,
                "required": false,
                "config": {
                    "showTitle": true,
                    "showBack": true
                }
            },
            {
                "id": "content-area",
                "type": "content",
                "name": "详情内容区",
                "enabled": true,
                "required": true,
                "config": {
                    "componentType": "form",
                    "configId": null,
                    "readonly": true,
                    "title": "详情信息"
                }
            },
            {
                "id": "toolbar-area",
                "type": "toolbar",
                "name": "操作按钮区",
                "enabled": true,
                "required": true,
                "props": { "buttons": [] }
            }
        ]
    }',
    '数据详情查看页面，支持只读展示和操作按钮。适用于数据详情展示场景。',
    1,
    NOW(),
    NOW()
);

-- 7. 空白页 (blank-page)
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, description, is_system, create_time, update_time)
VALUES (
    '空白页',
    'blank-page',
    'custom',
    'custom',
    '{
        "version": 2,
        "templateCode": "blank-page",
        "layoutType": "custom",
        "pageInfo": {
            "title": "空白页",
            "description": "自定义页面，完全自由配置"
        },
        "areas": [
            {
                "id": "custom-area",
                "type": "custom",
                "name": "自定义区域",
                "enabled": true,
                "required": true,
                "config": {
                    "allowComponents": ["form", "table", "chart", "button", "text", "image"],
                    "layout": "free"
                }
            }
        ]
    }',
    '完全空白页面，用户可以自由拖拽和配置任意组件。适用于高度自定义的场景。',
    1,
    NOW(),
    NOW()
);

-- 验证插入结果
SELECT
    id,
    template_name,
    template_code,
    template_type,
    layout_type,
    is_system,
    status
FROM low_page_template
WHERE is_system = 1
ORDER BY id;
