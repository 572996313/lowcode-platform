package com.lowcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 同步结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总页数
     */
    private Integer totalCount;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failedCount;

    /**
     * 错误列表
     */
    private List<SyncError> errors;
}
