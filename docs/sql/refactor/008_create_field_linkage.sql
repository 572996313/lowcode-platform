-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- =====================================================
-- 字段联动配置表
-- =====================================================

DROP TABLE IF EXISTS `low_field_linkage`;
CREATE TABLE `low_field_linkage` (
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '联动规则ID',
    `rule_name`       VARCHAR(100) NOT NULL COMMENT '规则名称',
    `rule_code`       VARCHAR(100) UNIQUE NOT NULL COMMENT '规则编码（唯一）',
    `source_field`    VARCHAR(100) NOT NULL COMMENT '源字段（触发字段）',
    `target_field`    VARCHAR(100) NOT NULL COMMENT '目标字段（被联动字段）',
    `linkage_type`    ENUM('visibility', 'value', 'options', 'required', 'readonly') NOT NULL COMMENT '联动类型',
    `condition_config` JSON NOT NULL COMMENT '条件配置',
    `action_config`   JSON NOT NULL COMMENT '动作配置',
    `description`     TEXT COMMENT '描述',
    `status`          TINYINT DEFAULT 1 COMMENT '状态（1=启用，0=禁用）',
    `deleted`         TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `create_by`       VARCHAR(64) COMMENT '创建者',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_source_field (source_field),
    INDEX idx_target_field (target_field),
    INDEX idx_rule_code (rule_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字段联动配置表';

-- =====================================================
-- 测试数据
-- =====================================================

-- 示例1：当用户类型为VIP时，显示VIP等级字段
INSERT INTO `low_field_linkage` (`rule_name`, `rule_code`, `source_field`, `target_field`, `linkage_type`, `condition_config`, `action_config`, `description`) VALUES
(
    'VIP用户显示等级',
    'vip_show_level',
    'userType',
    'vipLevel',
    'visibility',
    '{"operator": "equals", "value": "vip"}',
    '{"visible": true}',
    '当用户类型为VIP时，显示VIP等级字段'
);

-- 示例2：当选择国家后，省份下拉框加载对应省份
INSERT INTO `low_field_linkage` (`rule_name`, `rule_code`, `source_field`, `target_field`, `linkage_type`, `condition_config`, `action_config`, `description`) VALUES
(
    '国家省份联动',
    'country_province',
    'country',
    'province',
    'options',
    '{"operator": "not_empty"}',
    '{"dataSource": "dict", "dictCode": "province_{country}"}',
    '根据选择的国家加载对应的省份数据'
);

-- 示例3：当启用邮箱通知时，邮箱地址变为必填
INSERT INTO `low_field_linkage` (`rule_name`, `rule_code`, `source_field`, `target_field`, `linkage_type`, `condition_config`, `action_config`, `description`) VALUES
(
    '邮箱通知必填',
    'email_required',
    'enableEmailNotification',
    'email',
    'required',
    '{"operator": "equals", "value": true}',
    '{"required": true}',
    '当启用邮箱通知时，邮箱地址变为必填项'
);

-- 示例4：当状态为已审核时，字段变为只读
INSERT INTO `low_field_linkage` (`rule_name`, `rule_code`, `source_field`, `target_field`, `linkage_type`, `condition_config`, `action_config`, `description`) VALUES
(
    '审核后只读',
    'audited_readonly',
    'status',
    'auditTime',
    'readonly',
    '{"operator": "in", "value": ["approved", "rejected"]}',
    '{"readonly": true}',
    '状态为已审核或已拒绝时，审核时间字段变为只读'
);

-- =====================================================
-- 查看验证
-- =====================================================

SELECT
    rule_name AS '规则名称',
    rule_code AS '规则编码',
    source_field AS '源字段',
    target_field AS '目标字段',
    linkage_type AS '联动类型',
    description AS '描述'
FROM low_field_linkage;
