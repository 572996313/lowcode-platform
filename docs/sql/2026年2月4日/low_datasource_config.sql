create table low_datasource_config
(
    id            bigint auto_increment comment '数据源ID'
        primary key,
    ds_name       varchar(100)                       not null comment '数据源名称',
    ds_code       varchar(100)                       null comment '数据源编码',
    ds_type       varchar(50)                        null comment '数据源类型(mysql/postgresql/oracle/sqlserver/api)',
    host          varchar(200)                       null comment '主机地址',
    port          int                                null comment '端口',
    database_name varchar(100)                       null comment '数据库名',
    username      varchar(100)                       null comment '用户名',
    password      varchar(200)                       null comment '密码(加密存储)',
    api_url       varchar(500)                       null comment 'API地址',
    api_method    varchar(10)                        null comment 'API方法',
    api_headers   text                               null comment 'API请求头JSON',
    config_json   text                               null comment '其他配置JSON',
    status        tinyint  default 1                 null comment '状态',
    deleted       tinyint  default 0                 null comment '是否删除',
    create_by     varchar(64)                        null comment '创建者',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint ds_code
        unique (ds_code)
)
    comment '数据源配置表';

