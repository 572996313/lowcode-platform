package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 页面配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_page_config")
public class LowPageConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面编码
     */
    private String pageCode;

    /**
     * 页面类型(list/form/detail/custom)
     */
    private String pageType;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 布局类型
     */
    private String layoutType;

    /**
     * 布局配置JSON
     */
    private String layoutConfig;

    /**
     * 页面配置JSON
     */
    private String configJson;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 备注
     */
    private String remark;

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
