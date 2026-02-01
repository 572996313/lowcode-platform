-- 删除低代码平台复杂模板的 SQL 脚本
-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 查看删除前的模板
SELECT '=== 删除前的模板列表 ===' AS info;
SELECT template_code, template_name, template_type, layout_type
FROM low_page_template
ORDER BY template_code;

-- 删除 V2 复杂模板
DELETE FROM low_page_template WHERE template_code IN (
    'tabs-layout',
    'dashboard',
    'form-page',
    'detail-page',
    'blank-page'
);

-- 删除 V3 所有模板
DELETE FROM low_page_template WHERE template_code IN (
    'multi-toolbar',
    'main-bottom',
    'tree-table-multi',
    'dual-panel',
    'three-column'
);

-- 验证删除结果
SELECT '=== 删除后的模板列表（应该只剩 standard-list 和 tree-table） ===' AS info;
SELECT template_code, template_name, template_type, layout_type
FROM low_page_template
ORDER BY template_code;
