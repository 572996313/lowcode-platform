package com.lowcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 字典引用查询结果
 *
 * @author lowcode
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictReferenceResult {

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 总引用数
     */
    private Integer totalCount;

    /**
     * 引用详情列表
     */
    private List<DictReferenceDetail> references;
}
