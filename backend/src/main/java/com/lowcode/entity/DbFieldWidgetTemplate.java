package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字段控件配置模板实体类
 * 用于为同一个数据库字段配置多套控件模板
 *
 * @author lowcode-platform
 * @since 2025-02-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("db_field_widget_template")
public class DbFieldWidgetTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属表ID
     */
    private Long tableId;

    /**
     * 字段ID
     */
    private Long fieldId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码（可选，用于程序化引用）
     */
    private String templateCode;

    /**
     * 控件类型（input, select, switch, date-picker 等）
     */
    private String widgetType;

    /**
     * 控件配置JSON
     */
    private String widgetConfig;

    /**
     * 是否系统预设（0-用户创建，1-系统预设）
     */
    private Integer isSystem;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 是否启用（0-禁用，1-启用）
     */
    private Integer enabled;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 逻辑删除（0-未删除，1-已删除）
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
     * 关联的字段信息（不映射到数据库）
     */
    @TableField(exist = false)
    private DbTableField field;
}
