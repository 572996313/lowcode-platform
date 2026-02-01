package com.lowcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 页面引用信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageReference implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面编码
     */
    private String pageCode;

    /**
     * 引用路径（如：toolbar.buttons[0]）
     */
    private String referencePath;

    /**
     * 是否保持引用
     */
    private Boolean isLinked;

    /**
     * 更新时间
     */
    private String updatedAt;
}
