package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字段管理实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("db_table_field")
public class DbTableField implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属表ID（关联db_table.id）
     */
    private Long tableId;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型（VARCHAR, INT, DATETIME等）
     */
    private String fieldType;

    /**
     * 字段长度
     */
    private Integer fieldLength;

    /**
     * 小数位数
     */
    private Integer decimalDigits;

    /**
     * 是否可空：1-可空，0-不可空
     */
    private Integer isNullable;

    /**
     * 默认值
     */
    private String columnDefault;

    /**
     * 是否主键：1-是，0-否
     */
    private Integer isPrimaryKey;

    /**
     * 是否自增：1-是，0-否
     */
    private Integer isAutoIncrement;

    /**
     * 字段注释
     */
    private String fieldComment;

    /**
     * 字段显示标签（用户可编辑）
     */
    private String fieldLabel;

    /**
     * 字段在表中的位置（从1开始）
     */
    private Integer ordinalPosition;

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
}
