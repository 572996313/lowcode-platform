package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字段-控件绑定配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("db_field_widget_config")
public class DbFieldWidgetConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属表ID（关联db_table.id），为空表示默认映射规则
     */
    private Long tableId;

    /**
     * 字段ID（关联db_table_field.id），为空表示默认映射规则
     */
    private Long fieldId;

    /**
     * 控件类型（input/textarea/select/radio/checkbox/switch/date/datetime/number/upload/cascader等）
     */
    private String widgetType;

    /**
     * 控件配置JSON（placeholder/默认值/校验规则/控件属性等）
     */
    private String widgetConfig;

    /**
     * 是否为默认映射规则：1-是，0-否
     */
    private Integer isDefault;

    /**
     * 作为默认映射的字段类型（VARCHAR/INT/DATETIME等）
     */
    private String defaultForFieldType;

    /**
     * 是否启用：1-启用，0-禁用
     */
    private Integer enabled;

    /**
     * 排序序号
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
     * 关联的表信息（非数据库字段）
     */
    @TableField(exist = false)
    private DbTable table;

    /**
     * 关联的字段信息（非数据库字段）
     */
    @TableField(exist = false)
    private DbTableField field;
}
