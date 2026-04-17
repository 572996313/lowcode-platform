package com.lowcode.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 组件详情/数据详情请求参数
 */
@Data
@Schema(description = "组件详情请求参数")
public class ComponentDetailData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "组件ID")
    private String componentId;

    @Schema(description = "操作模式: add/edit/view")
    private String mode;

    @Schema(description = "行数据ID（编辑/查看时传）")
    private Object id;
}
