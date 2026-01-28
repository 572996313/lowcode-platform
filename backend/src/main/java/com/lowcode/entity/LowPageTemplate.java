package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 页面模板实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_page_template")
public class LowPageTemplate implements Serializable {

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
     * 模板类型(list/form/detail/dashboard)
     */
    private String templateType;

    /**
     * 布局类型(tree-table/top-bottom/left-right/tabs/custom)
     */
    private String layoutType;

    /**
     * 预览图
     */
    private String previewImage;

    /**
     * 模板配置JSON
     */
    private String configJson;

    /**
     * 模板配置JSON(v2版本)
     */
    private String configTemplate;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 是否系统模板
     */
    private Boolean isSystem;

    /**
     * 状态
     */
    private Integer status;

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
