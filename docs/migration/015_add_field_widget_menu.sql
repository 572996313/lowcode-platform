-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- 添加字段-控件绑定菜单
-- =====================================================

-- 插入字段-控件绑定菜单（作为库表管理的子菜单，父ID=12）
INSERT INTO `sys_menu` (
  `menu_name`, `menu_code`, `menu_type`, `parent_id`, `route_path`,
  `component_path`, `icon`, `sort_order`, `visible`, `status`,
  `create_time`, `update_time`
) VALUES (
  '字段-控件绑定',
  'field_widget_binding',
  2,
  12,
  'lowcode/FieldWidgetBinding',
  '/views/lowcode/FieldWidgetBinding.vue',
  'Link',
  2,
  1,
  1,
  NOW(),
  NOW()
) ON DUPLICATE KEY UPDATE
  `update_time` = NOW();

-- 验证菜单插入
SELECT
    id,
    menu_name,
    menu_code,
    parent_id,
    route_path,
    component_path,
    icon,
    sort_order
FROM sys_menu
WHERE menu_code = 'field_widget_binding';
