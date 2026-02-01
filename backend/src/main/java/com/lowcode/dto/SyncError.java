package com.lowcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 同步错误信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncError implements Serializable {

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
     * 错误消息
     */
    private String errorMessage;
}
