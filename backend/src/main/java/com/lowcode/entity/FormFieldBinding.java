package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表单字段绑定配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("form_field_binding")
public class FormFieldBinding implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表单ID（关联low_form_config.id）
     */
    private Long formId;

    /**
     * 字段ID（关联db_table_field.id）
     */
    private Long fieldId;

    /**
     * 控件类型（input/textarea/select/radio/checkbox/switch/date/datetime/number/upload/cascader等）
     */
    private String widgetType;

    /**
     * 控件配置JSON
     * 格式示例：
     * {
     *   "placeholder": "请输入",
     *   "defaultValue": "",
     *   "rules": [{"required": true, "message": "必填"}],
     *   "props": {},
     *   "options": []
     * }
     */
    private String widgetConfig;

    /**
     * 栅格占位格数（1-24）
     */
    private Integer span;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 是否可见
     */
    private Boolean visible;

    /**
     * 是否只读
     */
    private Boolean readonly;

    /**
     * 是否禁用
     */
    private Boolean disabled;

    /**
     * 排序序号
     */
    private Integer sortOrder;

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
     * 关联的表单配置（非数据库字段）
     */
    @TableField(exist = false)
    private LowFormConfig form;

    /**
     * 关联的字段信息（非数据库字段）
     */
    @TableField(exist = false)
    private DbTableField field;
}
