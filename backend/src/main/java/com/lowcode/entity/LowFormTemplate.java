package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表单模板实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_form_template")
public class LowFormTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码（唯一，对应前端组件名）
     */
    private String templateCode;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 预览图URL
     */
    private String previewImage;

    /**
     * 是否系统模板（0=否 1=是）
     */
    private Boolean isSystem;

    /**
     * 状态（0=禁用 1=启用）
     */
    private Boolean status;

    /**
     * 排序序号
     */
    private Integer sortOrder;

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

    /**
     * 是否删除（0=否 1=是）
     */
    @TableLogic
    private Integer deleted;
}