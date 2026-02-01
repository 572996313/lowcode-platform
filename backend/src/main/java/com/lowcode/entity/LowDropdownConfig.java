package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 下拉配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_dropdown_config")
public class LowDropdownConfig {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置编码（唯一）
     */
    private String configCode;

    /**
     * 下拉类型：flat（普通）/tree（树形）/cascader（级联）
     */
    private String dropdownType;

    /**
     * SQL 模板（支持参数占位符，如 #{paramName}）
     */
    private String sqlTemplate;

    /**
     * 显示标签字段
     */
    private String labelField;

    /**
     * 值字段
     */
    private String valueField;

    /**
     * 父级字段（树形/级联用）
     */
    private String parentField;

    /**
     * 参数配置（JSON格式）
     */
    private String paramConfig;

    /**
     * 额外字段映射（逗号分隔）
     */
    private String extraFields;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建人
     */
    private String createdBy;

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
     * 逻辑删除
     */
    @TableLogic
    private Boolean deleted;
}
