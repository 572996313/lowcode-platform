-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 字典分类表
CREATE TABLE IF NOT EXISTS sys_dict_category (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示根节点',
  category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
  category_code VARCHAR(50) NOT NULL COMMENT '分类编码，唯一标识',
  sort_order INT DEFAULT 0 COMMENT '排序序号',
  status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  description VARCHAR(255) DEFAULT NULL COMMENT '描述',
  deleted INT DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_category_code (category_code, deleted),
  KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典分类表';

-- 字典明细表
CREATE TABLE IF NOT EXISTS sys_dict_item (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  category_id BIGINT NOT NULL COMMENT '所属分类ID',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父项ID，0表示根项',
  item_label VARCHAR(100) NOT NULL COMMENT '字典项显示文本',
  item_value VARCHAR(100) NOT NULL COMMENT '字典项值',
  sort_order INT DEFAULT 0 COMMENT '排序序号',
  status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  description VARCHAR(255) DEFAULT NULL COMMENT '描述',
  css_class VARCHAR(50) DEFAULT NULL COMMENT 'CSS样式类',
  icon VARCHAR(100) DEFAULT NULL COMMENT '图标',
  deleted INT DEFAULT 0 COMMENT '逻辑删除：0-正常，1-删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_category_id (category_id),
  KEY idx_parent_id (parent_id),
  UNIQUE KEY uk_category_value (category_id, item_value, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典明细表';

-- 插入示例数据
-- 字典分类
INSERT INTO sys_dict_category (parent_id, category_name, category_code, sort_order, status, description) VALUES
(0, '系统状态', 'sys_status', 1, 1, '系统通用状态字典'),
(0, '用户类型', 'user_type', 2, 1, '用户类型分类'),
(0, '数据权限', 'data_scope', 3, 1, '数据权限范围'),
(0, '性别', 'sys_gender', 4, 1, '性别分类');

-- 字典明细
INSERT INTO sys_dict_item (category_id, parent_id, item_label, item_value, sort_order, status, css_class, description) VALUES
-- 系统状态
(1, 0, '启用', '1', 1, 1, 'tag-success', '状态启用'),
(1, 0, '禁用', '0', 2, 1, 'tag-info', '状态禁用'),
(1, 0, '删除', '-1', 3, 1, 'tag-danger', '已删除'),
-- 用户类型
(2, 0, '系统管理员', 'admin', 1, 1, 'tag-primary', '系统管理员角色'),
(2, 0, '普通用户', 'user', 2, 1, 'tag-default', '普通用户'),
(2, 0, '访客', 'guest', 3, 1, 'tag-warning', '访客用户'),
-- 数据权限
(3, 0, '全部数据', 'all', 1, 1, 'tag-success', '全部数据权限'),
(3, 0, '本部门及以下', 'dept_and_child', 2, 1, 'tag-primary', '本部门及子部门数据'),
(3, 0, '本部门', 'dept', 3, 1, 'tag-info', '本部门数据'),
(3, 0, '仅本人', 'self', 4, 1, 'tag-warning', '仅本人数据'),
-- 性别
(4, 0, '男', 'male', 1, 1, 'tag-primary', '男性'),
(4, 0, '女', 'female', 2, 1, 'tag-danger', '女性'),
(4, 0, '保密', 'secret', 3, 1, 'tag-info', '性别保密');
