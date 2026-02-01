package com.lowcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 模板引用查询结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateReferenceResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 模板类型
     */
    private String templateType;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 引用该模板的页面列表
     */
    private List<PageReference> pages;

    /**
     * 总引用数
     */
    private Integer totalCount;
}
