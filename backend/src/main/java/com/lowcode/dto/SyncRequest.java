package com.lowcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 同步请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequest implements Serializable {

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
     * 要同步的页面ID列表
     */
    private List<Long> pageIds;

    /**
     * 同步策略 (merge=合并保留overwrite, replace=替换清空overwrite)
     */
    private String strategy;
}
