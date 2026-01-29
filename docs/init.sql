-- =============================================
-- 低代码平台数据库初始化脚本
-- 数据库: MySQL 8.0+
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS lowcode_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lowcode_platform;

-- =============================================
-- 1. 系统用户表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- =============================================
-- 2. 菜单配置表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_code VARCHAR(50) COMMENT '菜单编码',
    menu_type TINYINT COMMENT '菜单类型(1目录 2菜单 3按钮)',
    icon VARCHAR(100) COMMENT '菜单图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    route_path VARCHAR(200) COMMENT '路由地址',
    component_path VARCHAR(200) COMMENT '组件路径',
    page_id BIGINT COMMENT '关联页面ID',
    is_external TINYINT DEFAULT 0 COMMENT '是否外链(0否 1是)',
    external_url VARCHAR(500) COMMENT '外链地址',
    visible TINYINT DEFAULT 1 COMMENT '是否可见',
    status TINYINT DEFAULT 1 COMMENT '状态(0停用 1启用)',
    perms VARCHAR(100) COMMENT '权限标识',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单配置表';

-- =============================================
-- 3. 页面模板表
-- =============================================
CREATE TABLE IF NOT EXISTS low_page_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_code VARCHAR(100) UNIQUE COMMENT '模板编码',
    template_type VARCHAR(50) COMMENT '模板类型(list/form/detail/dashboard)',
    layout_type VARCHAR(50) COMMENT '布局类型(tree-table/top-bottom/left-right/tabs/custom)',
    preview_image VARCHAR(500) COMMENT '预览图',
    config_json TEXT COMMENT '模板配置JSON',
    description VARCHAR(500) COMMENT '模板描述',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统模板',
    status TINYINT DEFAULT 1 COMMENT '状态',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页面模板表';

-- =============================================
-- 4. 页面配置表
-- =============================================
CREATE TABLE IF NOT EXISTS low_page_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '页面ID',
    page_name VARCHAR(100) NOT NULL COMMENT '页面名称',
    page_code VARCHAR(100) UNIQUE COMMENT '页面编码',
    page_type VARCHAR(50) COMMENT '页面类型(list/form/detail/custom)',
    template_id BIGINT COMMENT '模板ID',
    layout_type VARCHAR(50) COMMENT '布局类型(tree-table/top-bottom/left-right/tabs/custom)',
    layout_config TEXT COMMENT '布局配置JSON',
    config_json TEXT COMMENT '页面配置JSON',
    status TINYINT DEFAULT 1 COMMENT '状态',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_page_code (page_code),
    INDEX idx_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='页面配置表';

-- =============================================
-- 5. 表单配置表
-- =============================================
CREATE TABLE IF NOT EXISTS low_form_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '表单ID',
    form_name VARCHAR(100) NOT NULL COMMENT '表单名称',
    form_code VARCHAR(100) UNIQUE COMMENT '表单编码',
    page_id BIGINT COMMENT '所属页面ID',
    form_type VARCHAR(50) COMMENT '表单类型(add/edit/search/detail/dialog)',
    layout_type VARCHAR(50) DEFAULT 'horizontal' COMMENT '布局类型(horizontal/vertical/inline)',
    layout_cols INT DEFAULT 2 COMMENT '表单列数',
    label_width INT DEFAULT 100 COMMENT '标签宽度',
    label_position VARCHAR(20) DEFAULT 'right' COMMENT '标签位置(left/right/top)',
    size VARCHAR(20) DEFAULT 'default' COMMENT '组件尺寸(large/default/small)',
    config_json TEXT COMMENT '表单配置JSON',
    rules_json TEXT COMMENT '校验规则JSON',
    status TINYINT DEFAULT 1 COMMENT '状态',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_page_id (page_id),
    INDEX idx_form_code (form_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单配置表';

-- =============================================
-- 6. 表单字段表
-- =============================================
CREATE TABLE IF NOT EXISTS low_form_field (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字段ID',
    form_id BIGINT NOT NULL COMMENT '表单ID',
    field_name VARCHAR(100) COMMENT '字段名称',
    field_code VARCHAR(100) COMMENT '字段编码',
    field_type VARCHAR(50) COMMENT '字段类型(input/textarea/select/radio/checkbox/date/time/datetime/upload/editor/cascader/tree-select/switch/slider/rate/color-picker)',
    label VARCHAR(100) COMMENT '字段标签',
    placeholder VARCHAR(200) COMMENT '占位符',
    default_value VARCHAR(500) COMMENT '默认值',
    is_required TINYINT DEFAULT 0 COMMENT '是否必填',
    is_readonly TINYINT DEFAULT 0 COMMENT '是否只读',
    is_disabled TINYINT DEFAULT 0 COMMENT '是否禁用',
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示',
    sort_order INT DEFAULT 0 COMMENT '排序',
    span INT DEFAULT 12 COMMENT '栅格占位(1-24)',
    label_width INT COMMENT '标签宽度(覆盖表单设置)',
    options_json TEXT COMMENT '选项配置JSON(下拉框/单选/多选等)',
    rules_json TEXT COMMENT '校验规则JSON',
    props_json TEXT COMMENT '组件属性JSON',
    events_json TEXT COMMENT '事件配置JSON',
    linkage_json TEXT COMMENT '联动配置JSON',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_form_id (form_id),
    INDEX idx_field_code (field_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单字段表';

-- =============================================
-- 7. 表格配置表
-- =============================================
CREATE TABLE IF NOT EXISTS low_table_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '表格ID',
    table_name VARCHAR(100) NOT NULL COMMENT '表格名称',
    table_code VARCHAR(100) UNIQUE COMMENT '表格编码',
    page_id BIGINT COMMENT '所属页面ID',
    data_source_type VARCHAR(50) DEFAULT 'api' COMMENT '数据源类型(api/sql/static)',
    api_url VARCHAR(500) COMMENT 'API地址',
    api_method VARCHAR(10) DEFAULT 'GET' COMMENT '请求方法',
    api_params_json TEXT COMMENT 'API参数配置JSON',
    sql_content TEXT COMMENT 'SQL语句(当数据源为sql时)',
    is_pagination TINYINT DEFAULT 1 COMMENT '是否分页',
    page_size INT DEFAULT 10 COMMENT '每页条数',
    page_sizes VARCHAR(100) DEFAULT '10,20,50,100' COMMENT '每页条数选项',
    is_selection TINYINT DEFAULT 0 COMMENT '是否多选',
    selection_type VARCHAR(20) DEFAULT 'checkbox' COMMENT '选择类型(checkbox/radio)',
    is_index TINYINT DEFAULT 1 COMMENT '是否显示序号',
    is_border TINYINT DEFAULT 1 COMMENT '是否显示边框',
    is_stripe TINYINT DEFAULT 1 COMMENT '是否斑马纹',
    row_key VARCHAR(50) DEFAULT 'id' COMMENT '行数据唯一标识',
    empty_text VARCHAR(100) DEFAULT '暂无数据' COMMENT '空数据提示',
    height VARCHAR(50) COMMENT '表格高度',
    max_height VARCHAR(50) COMMENT '表格最大高度',
    config_json TEXT COMMENT '表格配置JSON',
    status TINYINT DEFAULT 1 COMMENT '状态',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_page_id (page_id),
    INDEX idx_table_code (table_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表格配置表';

-- =============================================
-- 8. 表格列配置表
-- =============================================
CREATE TABLE IF NOT EXISTS low_table_column (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '列ID',
    table_id BIGINT NOT NULL COMMENT '表格ID',
    column_name VARCHAR(100) COMMENT '列名称',
    column_code VARCHAR(100) COMMENT '列编码(字段名)',
    column_type VARCHAR(50) DEFAULT 'text' COMMENT '列类型(text/image/link/tag/badge/progress/switch/button/slot/index/selection/expand)',
    label VARCHAR(100) COMMENT '列标题',
    width INT COMMENT '列宽度',
    min_width INT COMMENT '最小宽度',
    is_sortable TINYINT DEFAULT 0 COMMENT '是否可排序',
    sort_orders VARCHAR(50) COMMENT '排序方式(ascending/descending/null)',
    is_fixed VARCHAR(10) COMMENT '固定列(left/right)',
    align VARCHAR(10) DEFAULT 'left' COMMENT '对齐方式(left/center/right)',
    header_align VARCHAR(10) COMMENT '表头对齐方式',
    is_resizable TINYINT DEFAULT 1 COMMENT '是否可调整宽度',
    is_show_overflow_tooltip TINYINT DEFAULT 1 COMMENT '是否显示溢出提示',
    formatter_type VARCHAR(50) COMMENT '格式化类型(date/datetime/money/percent/dict/custom)',
    formatter_config TEXT COMMENT '格式化配置JSON',
    dict_code VARCHAR(100) COMMENT '字典编码(当格式化类型为dict时)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示',
    is_export TINYINT DEFAULT 1 COMMENT '是否导出',
    props_json TEXT COMMENT '列属性JSON',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表格列配置表';

-- =============================================
-- 9. 按钮配置表
-- =============================================
CREATE TABLE IF NOT EXISTS low_button_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '按钮ID',
    button_name VARCHAR(100) COMMENT '按钮名称',
    button_code VARCHAR(100) COMMENT '按钮编码',
    page_id BIGINT COMMENT '所属页面ID',
    form_id BIGINT COMMENT '关联表单ID',
    table_id BIGINT COMMENT '关联表格ID',
    position VARCHAR(50) COMMENT '位置(toolbar/row/form/dialog/footer)',
    button_type VARCHAR(50) DEFAULT 'default' COMMENT '按钮类型(primary/success/warning/danger/info/default)',
    button_size VARCHAR(20) DEFAULT 'default' COMMENT '按钮尺寸(large/default/small)',
    icon VARCHAR(100) COMMENT '图标',
    is_plain TINYINT DEFAULT 0 COMMENT '是否朴素按钮',
    is_round TINYINT DEFAULT 0 COMMENT '是否圆角按钮',
    is_circle TINYINT DEFAULT 0 COMMENT '是否圆形按钮',
    is_loading TINYINT DEFAULT 0 COMMENT '是否加载中',
    is_disabled TINYINT DEFAULT 0 COMMENT '是否禁用',
    action_type VARCHAR(50) COMMENT '动作类型(api/dialog/drawer/route/link/custom/confirm)',
    action_config TEXT COMMENT '动作配置JSON',
    confirm_config TEXT COMMENT '确认框配置JSON',
    perms VARCHAR(100) COMMENT '权限标识',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示',
    show_condition TEXT COMMENT '显示条件表达式',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_page_id (page_id),
    INDEX idx_form_id (form_id),
    INDEX idx_table_id (table_id),
    INDEX idx_button_code (button_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='按钮配置表';

-- =============================================
-- 10. 查询条件配置表
-- =============================================
CREATE TABLE IF NOT EXISTS low_search_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '查询ID',
    page_id BIGINT COMMENT '所属页面ID',
    table_id BIGINT COMMENT '关联表格ID',
    field_code VARCHAR(100) COMMENT '字段编码',
    field_label VARCHAR(100) COMMENT '字段标签',
    field_type VARCHAR(50) COMMENT '字段类型(input/select/date/daterange/cascader等)',
    search_type VARCHAR(50) DEFAULT 'eq' COMMENT '查询类型(eq/ne/like/likeLeft/likeRight/gt/ge/lt/le/between/in/notIn/isNull/isNotNull)',
    default_value VARCHAR(500) COMMENT '默认值',
    placeholder VARCHAR(200) COMMENT '占位符',
    options_json TEXT COMMENT '选项配置JSON',
    props_json TEXT COMMENT '组件属性JSON',
    span INT DEFAULT 6 COMMENT '栅格占位',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示',
    is_advanced TINYINT DEFAULT 0 COMMENT '是否高级查询(折叠)',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_page_id (page_id),
    INDEX idx_table_id (table_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='查询条件配置表';

-- =============================================
-- 11. 数据字典表
-- =============================================
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID',
    dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
    dict_code VARCHAR(100) NOT NULL UNIQUE COMMENT '字典编码',
    status TINYINT DEFAULT 1 COMMENT '状态',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典类型表';

CREATE TABLE IF NOT EXISTS sys_dict_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典数据ID',
    dict_type_id BIGINT NOT NULL COMMENT '字典类型ID',
    dict_code VARCHAR(100) NOT NULL COMMENT '字典编码',
    dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value VARCHAR(100) NOT NULL COMMENT '字典值',
    css_class VARCHAR(100) COMMENT 'CSS类名',
    list_class VARCHAR(100) COMMENT '表格回显样式',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    remark VARCHAR(500) COMMENT '备注',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_dict_type_id (dict_type_id),
    INDEX idx_dict_code (dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典数据表';

-- =============================================
-- 12. 数据源配置表
-- =============================================
CREATE TABLE IF NOT EXISTS low_datasource_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '数据源ID',
    ds_name VARCHAR(100) NOT NULL COMMENT '数据源名称',
    ds_code VARCHAR(100) UNIQUE COMMENT '数据源编码',
    ds_type VARCHAR(50) COMMENT '数据源类型(mysql/postgresql/oracle/sqlserver/api)',
    host VARCHAR(200) COMMENT '主机地址',
    port INT COMMENT '端口',
    database_name VARCHAR(100) COMMENT '数据库名',
    username VARCHAR(100) COMMENT '用户名',
    password VARCHAR(200) COMMENT '密码(加密存储)',
    api_url VARCHAR(500) COMMENT 'API地址',
    api_method VARCHAR(10) COMMENT 'API方法',
    api_headers TEXT COMMENT 'API请求头JSON',
    config_json TEXT COMMENT '其他配置JSON',
    status TINYINT DEFAULT 1 COMMENT '状态',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据源配置表';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化管理员用户 (密码: admin123)
INSERT INTO sys_user (username, password, nickname, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', 1);

-- 初始化页面模板
INSERT INTO low_page_template (template_name, template_code, template_type, layout_type, description, is_system) VALUES
('空白模板', 'blank', 'custom', 'custom', '空白页面模板，自由布局', 1),
('列表页模板', 'list', 'list', 'top-bottom', '标准列表页，上方查询条件+按钮，下方表格', 1),
('左树右表模板', 'tree-table', 'list', 'tree-table', '左侧树形菜单，右侧列表页', 1),
('表单页模板', 'form', 'form', 'custom', '标准表单页面', 1),
('详情页模板', 'detail', 'detail', 'custom', '数据详情展示页面', 1),
('Tab页签模板', 'tabs', 'custom', 'tabs', '多Tab页签布局', 1),
('仪表盘模板', 'dashboard', 'dashboard', 'custom', '数据仪表盘页面', 1);

-- 初始化数据字典
INSERT INTO sys_dict_type (dict_name, dict_code, remark) VALUES
('状态', 'sys_status', '通用状态字典'),
('是否', 'sys_yes_no', '是否选项'),
('性别', 'sys_gender', '性别选项'),
('菜单类型', 'sys_menu_type', '菜单类型'),
('按钮类型', 'sys_button_type', '按钮样式类型'),
('表单字段类型', 'form_field_type', '表单字段组件类型'),
('表格列类型', 'table_column_type', '表格列展示类型'),
('查询类型', 'search_type', '查询条件类型'),
('布局类型', 'layout_type', '页面布局类型');

INSERT INTO sys_dict_data (dict_type_id, dict_code, dict_label, dict_value, sort_order) VALUES
(1, 'sys_status', '启用', '1', 1),
(1, 'sys_status', '禁用', '0', 2),
(2, 'sys_yes_no', '是', '1', 1),
(2, 'sys_yes_no', '否', '0', 2),
(3, 'sys_gender', '男', '1', 1),
(3, 'sys_gender', '女', '2', 2),
(3, 'sys_gender', '未知', '0', 3),
(4, 'sys_menu_type', '目录', '1', 1),
(4, 'sys_menu_type', '菜单', '2', 2),
(4, 'sys_menu_type', '按钮', '3', 3),
(5, 'sys_button_type', '主要', 'primary', 1),
(5, 'sys_button_type', '成功', 'success', 2),
(5, 'sys_button_type', '警告', 'warning', 3),
(5, 'sys_button_type', '危险', 'danger', 4),
(5, 'sys_button_type', '信息', 'info', 5),
(5, 'sys_button_type', '默认', 'default', 6);

-- 初始化菜单
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, icon, sort_order, route_path, component_path) VALUES
(0, '系统管理', 'system', 1, 'Setting', 1, '/system', NULL),
(0, '低代码配置', 'lowcode', 1, 'Edit', 2, '/lowcode', NULL),
(1, '用户管理', 'system:user', 2, 'User', 1, '/system/user', '/views/system/UserManage.vue'),
(1, '菜单管理', 'system:menu', 2, 'Menu', 2, '/system/menu', '/views/system/MenuManage.vue'),
(1, '字典管理', 'system:dict', 2, 'Collection', 3, '/system/dict', '/views/system/DictManage.vue'),
(2, '页面管理', 'lowcode:page', 2, 'Document', 1, '/lowcode/page', '/views/lowcode/PageManage.vue'),
(2, '表单列表', 'lowcode:form', 2, 'EditPen', 2, '/lowcode/form', '/views/lowcode/FormList.vue'),
(2, '表格列表', 'lowcode:table', 2, 'Grid', 3, '/lowcode/table', '/views/lowcode/TableList.vue'),
(2, '模板管理', 'lowcode:template', 2, 'Files', 4, '/lowcode/template', '/views/lowcode/TemplateManage.vue');

SELECT '数据库初始化完成!' AS message;
