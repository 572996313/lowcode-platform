package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 按钮配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_button_config")
public class LowButtonConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 按钮名称
     */
    private String buttonName;

    /**
     * 按钮编码
     */
    private String buttonCode;

    /**
     * 组件分类(common=通用组件库, business=业务组件库)
     */
    private String componentCategory;

    /**
     * 组件标签JSON: ["system","user","create"]
     */
    private String componentTags;

    /**
     * 位置(toolbar/row/form/dialog/footer)
     */
    private String position;

    /**
     * 按钮类型(primary/success/warning/danger/info/default)
     */
    private String buttonType;

    /**
     * 按钮尺寸(large/default/small)
     */
    private String buttonSize;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否朴素按钮
     */
    @TableField("is_plain")
    private Boolean plain;

    /**
     * 是否圆角按钮
     */
    @TableField("is_round")
    private Boolean round;

    /**
     * 是否圆形按钮
     */
    @TableField("is_circle")
    private Boolean circle;

    /**
     * 是否加载中
     */
    @TableField("is_loading")
    private Boolean loading;

    /**
     * 是否禁用
     */
    @TableField("is_disabled")
    private Boolean disabled;

    /**
     * 动作类型(api/dialog/drawer/route/link/custom/confirm)
     */
    private String actionType;

    /**
     * 动作配置JSON
     */
    private String actionConfig;

    /**
     * 确认框配置JSON
     */
    private String confirmConfig;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否显示
     */
    @TableField("is_visible")
    private Boolean visible;

    /**
     * 显示条件表达式
     */
    private String showCondition;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建者
     */
    private String createBy;

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
