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
 * 库表分组实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("db_table_group")
public class DbTableGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父分组ID，0表示顶级分组
     */
    private Long parentId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分组编码（可选）
     */
    private String groupCode;

    /**
     * 分组描述
     */
    private String description;

    /**
     * 排序号
     */
    private Integer sortOrder;

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
     * 子分组列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<DbTableGroup> children;

    /**
     * 分组下的表数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer tableCount;
}
