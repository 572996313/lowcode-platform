-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 更新菜单，指向新的 V6 版本组件
UPDATE sys_menu
SET component_path = '/views/lowcode-v6/PageManageV6.vue',
    route_path = '/lowcode/PageManage',
    update_time = NOW()
WHERE id = 6 AND menu_code = 'lowcode:page';

-- 验证更新
SELECT id, menu_name, menu_code, route_path, component_path
FROM sys_menu
WHERE id = 6;
