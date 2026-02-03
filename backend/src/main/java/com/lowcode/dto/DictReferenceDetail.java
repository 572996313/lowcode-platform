package com.lowcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字典引用详情
 *
 * @author lowcode
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictReferenceDetail {

    /**
     * 资源类型: form_field/table_column/form_template/table_template
     */
    private String resourceType;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 所属页面ID
     */
    private Long pageId;

    /**
     * 所属页面名称
     */
    private String pageName;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字段标签
     */
    private String fieldLabel;

    /**
     * 引用路径（JSON Path）
     */
    private String referencePath;

    /**
     * 资源类型描述
     */
    private String resourceTypeDesc;
}
