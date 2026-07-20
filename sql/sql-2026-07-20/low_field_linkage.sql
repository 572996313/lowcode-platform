create table low_field_linkage
(
    id               bigint auto_increment comment '联动规则ID'
        primary key,
    rule_name        varchar(100)                                                    not null comment '规则名称',
    rule_code        varchar(100)                                                    not null comment '规则编码（唯一）',
    source_field     varchar(100)                                                    not null comment '源字段（触发字段）',
    target_field     varchar(100)                                                    not null comment '目标字段（被联动字段）',
    linkage_type     enum ('visibility', 'value', 'options', 'required', 'readonly') not null comment '联动类型',
    condition_config json                                                            not null comment '条件配置',
    action_config    json                                                            not null comment '动作配置',
    description      text                                                            null comment '描述',
    status           tinyint  default 1                                              null comment '状态（1=启用，0=禁用）',
    deleted          tinyint  default 0                                              null comment '逻辑删除',
    create_by        varchar(64)                                                     null comment '创建者',
    create_time      datetime default CURRENT_TIMESTAMP                              null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP                              null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint rule_code
        unique (rule_code)
)
    comment '字段联动配置表';

create index idx_rule_code
    on low_field_linkage (rule_code);

create index idx_source_field
    on low_field_linkage (source_field);

create index idx_target_field
    on low_field_linkage (target_field);

INSERT INTO lowcode_platform.low_field_linkage (id, rule_name, rule_code, source_field, target_field, linkage_type, condition_config, action_config, description, status, deleted, create_by, create_time, update_time) VALUES (5, 'VIP用户显示等级', 'vip_show_level', 'userType', 'vipLevel', 'visibility', '{"value": "vip", "operator": "equals"}', '{"visible": true}', '当用户类型为VIP时，显示VIP等级字段', 1, 0, null, '2026-03-04 18:31:05', '2026-03-04 18:31:05');
INSERT INTO lowcode_platform.low_field_linkage (id, rule_name, rule_code, source_field, target_field, linkage_type, condition_config, action_config, description, status, deleted, create_by, create_time, update_time) VALUES (6, '国家省份联动', 'country_province', 'country', 'province', 'options', '{"operator": "not_empty"}', '{"dictCode": "province_{country}", "dataSource": "dict"}', '根据选择的国家加载对应的省份数据', 1, 0, null, '2026-03-04 18:31:05', '2026-03-04 18:31:05');
INSERT INTO lowcode_platform.low_field_linkage (id, rule_name, rule_code, source_field, target_field, linkage_type, condition_config, action_config, description, status, deleted, create_by, create_time, update_time) VALUES (7, '邮箱通知必填', 'email_required', 'enableEmailNotification', 'email', 'required', '{"value": true, "operator": "equals"}', '{"required": true}', '当启用邮箱通知时，邮箱地址变为必填项', 1, 0, null, '2026-03-04 18:31:05', '2026-03-04 18:31:05');
INSERT INTO lowcode_platform.low_field_linkage (id, rule_name, rule_code, source_field, target_field, linkage_type, condition_config, action_config, description, status, deleted, create_by, create_time, update_time) VALUES (8, '审核后只读', 'audited_readonly', 'status', 'auditTime', 'readonly', '{"value": ["approved", "rejected"], "operator": "in"}', '{"readonly": true}', '状态为已审核或已拒绝时，审核时间字段变为只读', 1, 0, null, '2026-03-04 18:31:05', '2026-03-04 18:31:05');
