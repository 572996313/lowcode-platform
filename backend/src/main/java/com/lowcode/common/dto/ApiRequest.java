package com.lowcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用请求信封
 * 所有标准 POST 数据接口统一使用此信封包装
 *
 * @param <T> 业务数据类型
 */
@Data
@Schema(description = "通用请求信封")
public class ApiRequest<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页面ID")
    private Long pageId;

    @Schema(description = "组件ID")
    private String componentId;

    @Schema(description = "业务数据")
    private T data;
}
