-- =====================================================
-- V3 布局模板初始化脚本
-- 版本: 009
-- 描述: 添加灵活布局架构的 V3 布局模板
-- 作者: Claude
-- 日期: 2025-01-31
-- =====================================================

-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- V3 布局模板
-- =====================================================

-- 模板1: 多工具栏列表布局
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, is_system) VALUES
('多工具栏列表', 'multi-toolbar', 'list', 'top-bottom', '{
  "version": 3,
  "templateCode": "multi-toolbar",
  "layoutType": "top-bottom",
  "layoutConfig": {
    "gap": "16px",
    "padding": "0"
  },
  "areas": [
    {
      "id": "toolbar-top",
      "type": "toolbar",
      "name": "顶部工具栏",
      "enabled": true,
      "required": false,
      "position": "top",
      "role": "auxiliary",
      "layoutHints": {
        "order": 0
      },
      "config": {
        "buttons": [],
        "align": "left"
      }
    },
    {
      "id": "search",
      "type": "search",
      "name": "查询区",
      "enabled": true,
      "required": false,
      "position": "top",
      "role": "secondary",
      "layoutHints": {
        "collapsible": true,
        "collapsed": false
      },
      "config": {
        "title": "查询条件",
        "fields": [],
        "collapsible": true
      }
    },
    {
      "id": "toolbar-content",
      "type": "toolbar",
      "name": "表格工具栏",
      "enabled": true,
      "required": false,
      "position": "main",
      "role": "auxiliary",
      "layoutHints": {
        "order": -1
      },
      "config": {
        "buttons": [],
        "align": "left"
      }
    },
    {
      "id": "content-main",
      "type": "content",
      "name": "数据表格",
      "enabled": true,
      "required": false,
      "position": "main",
      "role": "primary",
      "layoutHints": {
        "flex": 1,
        "scrollable": true
      },
      "config": {
        "title": "数据列表",
        "componentType": "table",
        "configId": null,
        "showToolbar": false
      }
    }
  ]
}', 1);

-- 模板2: 主内容+底部面板布局
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, is_system) VALUES
('主内容+底部面板', 'main-bottom', 'list', 'top-bottom', '{
  "version": 3,
  "templateCode": "main-bottom",
  "layoutType": "top-bottom",
  "layoutConfig": {
    "gap": "16px",
    "padding": "0"
  },
  "areas": [
    {
      "id": "toolbar-top",
      "type": "toolbar",
      "name": "顶部工具栏",
      "enabled": true,
      "required": false,
      "position": "top",
      "role": "auxiliary",
      "config": {
        "buttons": [],
        "align": "left"
      }
    },
    {
      "id": "content-main",
      "type": "content",
      "name": "主内容区",
      "enabled": true,
      "required": false,
      "position": "main",
      "role": "primary",
      "layoutHints": {
        "flex": 1,
        "scrollable": true
      },
      "config": {
        "title": "数据列表",
        "componentType": "table",
        "configId": null
      }
    },
    {
      "id": "content-bottom",
      "type": "content",
      "name": "底部面板",
      "enabled": true,
      "required": false,
      "position": "bottom",
      "role": "secondary",
      "layoutHints": {
        "height": "300px",
        "resizable": true
      },
      "config": {
        "title": "详细信息",
        "componentType": "form",
        "configId": null
      }
    }
  ]
}', 1);

-- 模板3: 左树右表+多工具栏布局
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, is_system) VALUES
('左树右表多工具栏', 'tree-table-multi', 'list', 'tree-table', '{
  "version": 3,
  "templateCode": "tree-table-multi",
  "layoutType": "tree-table",
  "layoutConfig": {
    "gap": "16px",
    "padding": "0"
  },
  "areas": [
    {
      "id": "tree",
      "type": "tree",
      "name": "分类树",
      "enabled": true,
      "required": false,
      "position": "left",
      "role": "secondary",
      "layoutHints": {
        "width": "280px",
        "resizable": true
      },
      "config": {
        "title": "分类",
        "dataUrl": "",
        "labelField": "name",
        "childrenField": "children",
        "idField": "id"
      }
    },
    {
      "id": "toolbar-top",
      "type": "toolbar",
      "name": "顶部工具栏",
      "enabled": true,
      "required": false,
      "position": "top",
      "role": "auxiliary",
      "config": {
        "buttons": [],
        "align": "left"
      }
    },
    {
      "id": "search",
      "type": "search",
      "name": "查询区",
      "enabled": true,
      "required": false,
      "position": "top",
      "role": "secondary",
      "layoutHints": {
        "collapsible": true
      },
      "config": {
        "title": "查询条件",
        "fields": []
      }
    },
    {
      "id": "toolbar-content",
      "type": "toolbar",
      "name": "表格工具栏",
      "enabled": true,
      "required": false,
      "position": "main",
      "role": "auxiliary",
      "layoutHints": {
        "order": -1
      },
      "config": {
        "buttons": [],
        "align": "left"
      }
    },
    {
      "id": "content-main",
      "type": "content",
      "name": "数据表格",
      "enabled": true,
      "required": false,
      "position": "main",
      "role": "primary",
      "layoutHints": {
        "flex": 1,
        "scrollable": true
      },
      "config": {
        "title": "数据列表",
        "componentType": "table",
        "configId": null
      }
    }
  ]
}', 1);

-- 模板4: 双面板布局（左表单右表格）
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, is_system) VALUES
('双面板布局', 'dual-panel', 'form', 'top-bottom', '{
  "version": 3,
  "templateCode": "dual-panel",
  "layoutType": "top-bottom",
  "layoutConfig": {
    "gap": "16px",
    "padding": "0"
  },
  "areas": [
    {
      "id": "toolbar-top",
      "type": "toolbar",
      "name": "顶部工具栏",
      "enabled": true,
      "required": false,
      "position": "top",
      "role": "auxiliary",
      "config": {
        "buttons": [],
        "align": "left"
      }
    },
    {
      "id": "content-left",
      "type": "content",
      "name": "左侧表单",
      "enabled": true,
      "required": false,
      "position": "left",
      "role": "secondary",
      "layoutHints": {
        "width": "400px",
        "resizable": true
      },
      "config": {
        "title": "编辑表单",
        "componentType": "form",
        "configId": null
      }
    },
    {
      "id": "content-right",
      "type": "content",
      "name": "右侧表格",
      "enabled": true,
      "required": false,
      "position": "main",
      "role": "primary",
      "layoutHints": {
        "flex": 1,
        "scrollable": true
      },
      "config": {
        "title": "数据列表",
        "componentType": "table",
        "configId": null
      }
    }
  ]
}', 1);

-- 模板5: 三栏布局（左树中表右详情）
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, config_template, is_system) VALUES
('三栏布局', 'three-column', 'list', 'top-bottom', '{
  "version": 3,
  "templateCode": "three-column",
  "layoutType": "top-bottom",
  "layoutConfig": {
    "gap": "16px",
    "padding": "0"
  },
  "areas": [
    {
      "id": "toolbar-top",
      "type": "toolbar",
      "name": "顶部工具栏",
      "enabled": true,
      "required": false,
      "position": "top",
      "role": "auxiliary",
      "config": {
        "buttons": [],
        "align": "left"
      }
    },
    {
      "id": "tree",
      "type": "tree",
      "name": "导航树",
      "enabled": true,
      "required": false,
      "position": "left",
      "role": "secondary",
      "layoutHints": {
        "width": "240px",
        "resizable": true
      },
      "config": {
        "title": "导航",
        "dataUrl": "",
        "labelField": "name",
        "childrenField": "children",
        "idField": "id"
      }
    },
    {
      "id": "content-main",
      "type": "content",
      "name": "主内容区",
      "enabled": true,
      "required": false,
      "position": "main",
      "role": "primary",
      "layoutHints": {
        "flex": 1,
        "scrollable": true
      },
      "config": {
        "title": "数据列表",
        "componentType": "table",
        "configId": null
      }
    },
    {
      "id": "content-right",
      "type": "content",
      "name": "详情面板",
      "enabled": true,
      "required": false,
      "position": "right",
      "role": "secondary",
      "layoutHints": {
        "width": "320px",
        "resizable": true
      },
      "config": {
        "title": "详细信息",
        "componentType": "form",
        "configId": null
      }
    }
  ]
}', 1);

-- =====================================================
-- 验证插入结果
-- =====================================================

-- 查询新增的 V3 模板
SELECT
    id,
    template_name,
    template_code,
    layout_type,
    CASE
        WHEN JSON_EXTRACT(config_template, '$.version') = 3 THEN 'V3'
        WHEN JSON_EXTRACT(config_template, '$.version') = 2 THEN 'V2'
        ELSE '未知'
    END AS config_version,
    JSON_EXTRACT(config_template, '$.areas') AS areas_count
FROM low_page_template
WHERE template_code IN (
    'multi-toolbar',
    'main-bottom',
    'tree-table-multi',
    'dual-panel',
    'three-column'
);

-- =====================================================
-- 更新记录
-- =====================================================
-- 记录迁移历史
INSERT INTO low_migration_history (version, description, executed_at) VALUES
('009', '添加V3灵活布局模板（多工具栏、主内容+底部面板、双面板、三栏布局）', NOW());
