package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表单字段实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_form_field")
public class LowFormField implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表单ID
     */
    private Long formId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段标签
     */
    private String label;

    /**
     * 占位符
     */
    private String placeholder;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否必填
     */
    @TableField("is_required")
    private Boolean required;

    /**
     * 是否只读
     */
    @TableField("is_readonly")
    private Boolean readonly;

    /**
     * 是否禁用
     */
    @TableField("is_disabled")
    private Boolean disabled;

    /**
     * 是否显示
     */
    @TableField("is_visible")
    private Boolean visible;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 栅格占位(1-24)
     */
    private Integer span;

    /**
     * 标签宽度
     */
    private Integer labelWidth;

    /**
     * 选项配置JSON
     */
    private String optionsJson;

    /**
     * 校验规则JSON
     */
    private String rulesJson;

    /**
     * 组件属性JSON
     */
    private String propsJson;

    /**
     * 事件配置JSON
     */
    private String eventsJson;

    /**
     * 联动配置JSON
     */
    private String linkageJson;

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
