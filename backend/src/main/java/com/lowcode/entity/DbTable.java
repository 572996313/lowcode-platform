package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 库表管理实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("db_table")
public class DbTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 数据库/模式名称
     */
    private String tableSchema;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表类型：TABLE-表，VIEW-视图
     */
    private String tableType;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 同步状态：0-未同步，1-已同步
     */
    private Integer syncStatus;

    /**
     * 最后同步时间
     */
    private LocalDateTime syncTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 字段列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<DbTableField> fields;
}
