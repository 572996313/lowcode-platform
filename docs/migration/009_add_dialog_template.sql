-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =============================================
-- 弹窗模板表
-- =============================================
CREATE TABLE `low_dialog_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `template_code` VARCHAR(100) NOT NULL COMMENT '模板编码（唯一）',
  `category` VARCHAR(50) NOT NULL DEFAULT 'default' COMMENT '分类：default/form/edit/view/delete/custom',
  `width` VARCHAR(20) NOT NULL DEFAULT '600px' COMMENT '弹窗宽度',
  `height` VARCHAR(20) COMMENT '弹窗高度（NULL=自适应）',
  `min_width` VARCHAR(20) NOT NULL DEFAULT '400px' COMMENT '最小宽度',
  `max_width` VARCHAR(20) COMMENT '最大宽度',
  `min_height` VARCHAR(20) NOT NULL DEFAULT '200px' COMMENT '最小高度',
  `max_height` VARCHAR(20) COMMENT '最大高度',
  `draggable` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否可拖拽',
  `resizable` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否可调整大小',
  `modal` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否显示遮罩层',
  `close_on_click_modal` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '点击遮罩是否关闭',
  `close_on_press_escape` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '按ESC是否关闭',
  `show_close` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否显示关闭按钮',
  `close_modal_icon` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否使用关闭图标',
  `fullscreen` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否全屏',
  `center` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否居中',
  `align_center` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '头部和底部是否居中',
  `destroy_on_close` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '关闭时是否销毁元素',
  `lock_scroll` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否锁定body滚动',
  `custom_class` VARCHAR(100) COMMENT '自定义类名',
  `z_index` INT NOT NULL DEFAULT 2000 COMMENT 'z-index层级',
  `append_to_body` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否挂载到body',
  `top` VARCHAR(20) COMMENT '初始top位置',
  `before_close_json` TEXT COMMENT '关闭前回调配置(JSON)',
  `opened_callback_json` TEXT COMMENT '打开后回调配置(JSON)',
  `closed_callback_json` TEXT COMMENT '关闭后回调配置(JSON)',
  `description` VARCHAR(500) COMMENT '描述',
  `preview_image` VARCHAR(500) COMMENT '预览图URL',
  `is_system` TINYINT NOT NULL DEFAULT 0 COMMENT '系统模板标识',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0=禁用，1=启用）',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='弹窗模板表';

-- =============================================
-- 预设弹窗模板数据
-- =============================================

-- 默认弹窗（600px，不可拖拽调整）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `draggable`, `resizable`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('默认弹窗', 'dialog_default', 'default', '600px', '400px', 0, 0, '默认标准弹窗，固定大小，不可拖拽调整', 1, 1, 1);

-- 小型弹窗（400px）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `draggable`, `resizable`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('小型弹窗', 'dialog_small', 'default', '400px', '300px', 0, 0, '小型弹窗，适合简单对话框', 1, 2, 1);

-- 宽屏弹窗（900px）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `max_width`, `draggable`, `resizable`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('宽屏弹窗', 'dialog_wide', 'default', '900px', '600px', '1200px', 0, 0, '宽屏弹窗，适合内容较多的场景', 1, 3, 1);

-- 表单编辑弹窗（可拖拽）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `draggable`, `resizable`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('表单编辑弹窗', 'dialog_form_edit', 'form', '700px', '500px', 1, 0, '表单编辑弹窗，支持拖拽移动', 1, 10, 1);

-- 表单查阅弹窗（只读，可拖拽）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `draggable`, `resizable`, `close_on_click_modal`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('表单查阅弹窗', 'dialog_form_view', 'view', '700px', '500px', 1, 1, 0, '表单查阅弹窗，只读模式，支持拖拽和调整大小，点击遮罩不关闭', 1, 11, 1);

-- 删除确认弹窗（固定大小）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `max_width`, `draggable`, `resizable`, `close_on_click_modal`, `close_on_press_escape`, `show_close`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('删除确认弹窗', 'dialog_delete_confirm', 'delete', '400px', '400px', '400px', 0, 0, 0, 0, 0, '删除确认弹窗，固定大小，必须选择操作', 1, 20, 1);

-- 全屏弹窗
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `draggable`, `resizable`, `fullscreen`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('全屏弹窗', 'dialog_fullscreen', 'custom', '100%', '100%', 0, 0, 1, '全屏弹窗，适合复杂编辑场景', 1, 30, 1);

-- 表格查看弹窗（1200px，可拖拽调整）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `max_width`, `min_height`, `max_height`, `draggable`, `resizable`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('表格查看弹窗', 'dialog_table_view', 'view', '1200px', '800px', '90vw', '500px', '80vh', 1, 1, '表格查看弹窗，大尺寸，支持拖拽和调整大小', 1, 40, 1);

-- 配置弹窗（700px，可调整大小）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `max_width`, `draggable`, `resizable`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('配置弹窗', 'dialog_config', 'form', '700px', '500px', '900px', 0, 1, '配置弹窗，支持调整大小', 1, 50, 1);

-- 预览弹窗（fullscreen，无遮罩）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `draggable`, `resizable`, `fullscreen`, `modal`, `modal_class`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('预览弹窗', 'dialog_preview', 'custom', '100%', '100%', 0, 0, 1, 0, '', '预览弹窗，全屏无遮罩', 1, 60, 1);

-- 编辑弹窗（中等尺寸，可拖拽调整）
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `max_width`, `draggable`, `resizable`, `destroy_on_close`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('编辑弹窗', 'dialog_edit', 'edit', '650px', '450px', '850px', 1, 1, 0, '编辑弹窗，支持拖拽和调整大小，不销毁DOM', 1, 15, 1);

-- 信息提示弹窗
INSERT INTO `low_dialog_template` (`template_name`, `template_code`, `category`, `width`, `min_width`, `draggable`, `resizable`, `center`, `align_center`, `destroy_on_close`, `description`, `is_system`, `sort_order`, `status`)
VALUES ('信息提示弹窗', 'dialog_info', 'default', '500px', '350px', 0, 0, 1, 1, 1, '信息提示弹窗，内容居中', 1, 5, 1);
