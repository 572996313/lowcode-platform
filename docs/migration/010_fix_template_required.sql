-- =====================================================
-- 修复模板区域的 required 字段
-- 版本: 010
-- 描述: 将所有模板中主内容区的 required 改为 false，允许用户禁用主内容区
-- 作者: Claude
-- 日期: 2026-01-31
-- =====================================================

-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- 更新 V3 模板的 required 字段
-- =====================================================

-- 更新多工具栏列表模板
UPDATE low_page_template
SET config_template = JSON_SET(
    config_template,
    '$.areas[3].required', 'false'
)
WHERE template_code = 'multi-toolbar';

-- 更新主内容+底部面板模板
UPDATE low_page_template
SET config_template = JSON_SET(
    config_template,
    '$.areas[1].required', 'false'
)
WHERE template_code = 'main-bottom';

-- 更新左树右表多工具栏模板
UPDATE low_page_template
SET config_template = JSON_SET(
    config_template,
    '$.areas[4].required', 'false'
)
WHERE template_code = 'tree-table-multi';

-- 更新双面板布局模板（需要更新两个content区域）
UPDATE low_page_template
SET config_template = JSON_SET(
    JSON_SET(
        config_template,
        '$.areas[1].required', 'false'
    ),
    '$.areas[2].required', 'false'
)
WHERE template_code = 'dual-panel';

-- 更新三栏布局模板
UPDATE low_page_template
SET config_template = JSON_SET(
    config_template,
    '$.areas[2].required', 'false'
)
WHERE template_code = 'three-column';

-- =====================================================
-- 更新 V2 标准布局模板（如果有）
-- =====================================================

-- 更新上下布局模板 (V2版本，areas[1]是content区)
UPDATE low_page_template
SET config_template = JSON_SET(
    config_template,
    '$.areas[1].required', 'false'
)
WHERE template_code = 'top-bottom' AND JSON_EXTRACT(config_template, '$.version') IS NULL;

-- 更新左树右表布局模板（V2版本）
-- 这个模板有4个区域：tree-area, search-area, toolbar-area, content-area
UPDATE low_page_template
SET config_template = JSON_SET(
    config_template,
    '$.areas[3].required', 'false'
)
WHERE template_code = 'tree-table' AND JSON_EXTRACT(config_template, '$.version') IS NULL;

-- 更新标签页布局模板
UPDATE low_page_template
SET config_template = JSON_SET(
    config_template,
    '$.areas[0].required', 'false'
)
WHERE template_code = 'tabs' AND JSON_EXTRACT(config_template, '$.version') IS NULL;

-- =====================================================
-- 验证更新结果
-- =====================================================

SELECT
    id,
    template_name,
    template_code,
    layout_type,
    JSON_EXTRACT(config_template, '$.version') AS version,
    JSON_EXTRACT(config_template, '$.areas') AS areas
FROM low_page_template
WHERE template_code IN (
    'multi-toolbar',
    'main-bottom',
    'tree-table-multi',
    'dual-panel',
    'three-column',
    'top-bottom',
    'tree-table',
    'tabs'
)
ORDER BY template_code;

-- =====================================================
-- 更新记录
-- =====================================================
INSERT INTO low_migration_history (version, description, executed_at) VALUES
('010', '修复所有模板中主内容区的required字段，允许用户禁用主内容区', NOW());
