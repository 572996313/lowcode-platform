package com.lowcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 分页查询业务参数
 */
@Data
@Schema(description = "分页查询参数")
public class PageQueryData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码", example = "1")
    private Integer current = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer size = 10;

    @Schema(description = "搜索过滤参数")
    private Map<String, Object> filters;

    @Schema(description = "排序字段")
    private String sortField;

    @Schema(description = "排序方向 asc/desc")
    private String sortOrder;
}
