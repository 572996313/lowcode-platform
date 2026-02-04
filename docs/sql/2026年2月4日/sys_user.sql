create table sys_user
(
    id          bigint auto_increment comment '用户ID'
        primary key,
    username    varchar(50)                        not null comment '用户名',
    password    varchar(100)                       not null comment '密码',
    nickname    varchar(50)                        null comment '昵称',
    email       varchar(100)                       null comment '邮箱',
    phone       varchar(20)                        null comment '手机号',
    avatar      varchar(500)                       null comment '头像',
    status      tinyint  default 1                 null comment '状态(0禁用 1启用)',
    deleted     tinyint  default 0                 null comment '是否删除',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint username
        unique (username)
)
    comment '系统用户表';

create index idx_status
    on sys_user (status);

create index idx_username
    on sys_user (username);

INSERT INTO lowcode_platform.sys_user (id, username, password, nickname, email, phone, avatar, status, deleted, create_time, update_time) VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', null, null, null, 1, 0, '2026-01-28 17:01:18', '2026-01-28 17:01:18');
