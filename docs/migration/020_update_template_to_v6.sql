-- 更新页面模板配置为 V6 格式
-- 版本: 020_update_template_to_v6
-- 日期: 2026-02-04
-- 描述: 将页面模板配置从 V2 格式统一为 V6 格式

-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

USE lowcode_platform;

-- 1. 更新标准列表页模板 (id=10) - 转换为 V6 格式
UPDATE low_page_template
SET config_template = '{
  "version": "v6",
  "pageName": "",
  "pageCode": "",
  "description": "标准列表页（查询区 + 工具栏 + 表格）",
  "layoutType": "top-bottom",
  "toolbar": {
    "enabled": true,
    "align": "left",
    "buttons": []
  },
  "search": {
    "enabled": true,
    "collapsible": true,
    "collapsed": false,
    "labelWidth": 80,
    "labelPosition": "left",
    "fields": [],
    "layout": {
      "span": 6,
      "gap": 16
    }
  },
  "table": {
    "columns": [],
    "stripe": true,
    "border": false,
    "highlightCurrentRow": false,
    "showHeader": true,
    "pagination": true,
    "pageSize": 10,
    "pageSizes": [10, 20, 50, 100],
    "rowActions": []
  },
  "globalConfig": {
    "pageSize": 10,
    "pageSizes": [10, 20, 50, 100],
    "stripe": true,
    "border": false
  }
}',
update_time = NOW()
WHERE id = 10;

-- 2. 更新左树右表模板 (id=3) - 转换为 V6 格式
UPDATE low_page_template
SET config_template = '{
  "version": "v6",
  "pageName": "",
  "pageCode": "",
  "description": "左树右表布局（左侧树形导航 + 右侧查询区 + 表格）",
  "layoutType": "tree-table",
  "toolbar": {
    "enabled": true,
    "align": "left",
    "buttons": []
  },
  "search": {
    "enabled": true,
    "collapsible": true,
    "collapsed": false,
    "labelWidth": 80,
    "labelPosition": "left",
    "fields": [],
    "layout": {
      "span": 6,
      "gap": 16
    }
  },
  "table": {
    "columns": [],
    "stripe": true,
    "border": false,
    "highlightCurrentRow": false,
    "showHeader": true,
    "pagination": true,
    "pageSize": 10,
    "pageSizes": [10, 20, 50, 100],
    "rowActions": []
  },
  "globalConfig": {
    "pageSize": 10,
    "pageSizes": [10, 20, 50, 100],
    "stripe": true,
    "border": false
  }
}',
update_time = NOW()
WHERE id = 3;

-- 3. 更新空白页模板 (id=1) - 转换为 V6 格式（作为自由画布）
-- 注意：自由画布有独立的格式，这里保留为空配置
UPDATE low_page_template
SET template_code = 'free-canvas',
template_type = 'custom',
layout_type = 'custom',
config_template = '{
  "version": "free-canvas",
  "pageName": "",
  "pageCode": "",
  "description": "自由画布页面，完全自由配置",
  "canvas": {
    "width": 1200,
    "height": 800,
    "backgroundColor": "#ffffff",
    "backgroundImage": "",
    "grid": {
      "enabled": true,
      "size": 10,
      "color": "#e0e0e0"
    }
  },
  "components": [],
  "globalConfig": {
    "theme": "default",
    "primaryColor": "#409eff"
  }
}',
update_time = NOW()
WHERE id = 1;

-- 4. 更新表单页模板 (id=14) - 转换为 V6 格式（简化版，只保留表单和工具栏）
UPDATE low_page_template
SET template_code = 'form-page',
config_template = '{
  "version": "v6",
  "pageName": "",
  "pageCode": "",
  "description": "表单页（表单 + 工具栏）",
  "layoutType": "form",
  "toolbar": {
    "enabled": true,
    "align": "center",
    "buttons": []
  },
  "search": {
    "enabled": false,
    "collapsible": true,
    "collapsed": false,
    "labelWidth": 80,
    "labelPosition": "left",
    "fields": [],
    "layout": {
      "span": 6,
      "gap": 16
    }
  },
  "table": {
    "columns": [],
    "stripe": false,
    "border": false,
    "highlightCurrentRow": false,
    "showHeader": false,
    "pagination": false,
    "pageSize": 10,
    "pageSizes": [10, 20, 50, 100],
    "rowActions": []
  },
  "globalConfig": {
    "pageSize": 10,
    "pageSizes": [10, 20, 50, 100],
    "stripe": false,
    "border": false
  }
}',
update_time = NOW()
WHERE id = 14;

-- 验证更新结果
SELECT
    id,
    template_code,
    template_name,
    template_type,
    layout_type,
    JSON_EXTRACT(config_template, '$.version') as version,
    update_time
FROM low_page_template
WHERE id IN (1, 3, 10, 14)
ORDER BY id;

-- 查找仍然使用 V2 格式的模板（用于验证）
SELECT
    id,
    template_code,
    template_name,
    JSON_EXTRACT(config_template, '$.version') as version
FROM low_page_template
WHERE config_template LIKE '"version": 2%'
   OR config_template LIKE '"version":2%';
