package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 组件模板实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_component_template")
public class LowComponentTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 组件类型(container/table/form/custom)
     */
    private String componentType;

    /**
     * 组件配置JSON模板
     */
    private String configTemplate;

    /**
     * 分类(layout/content)
     */
    private String category;

    /**
     * 预览图URL
     */
    private String previewImage;

    /**
     * 描述
     */
    private String description;

    /**
     * 关键词，逗号分隔
     */
    private String keywords;

    /**
     * 系统模板标识
     */
    private Boolean isSystem;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 删除标志
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
