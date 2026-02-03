package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典引用关系实体类
 *
 * @author lowcode
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_dict_reference")
public class SysDictReference implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典分类编码
     */
    private String dictCategoryCode;

    /**
     * 资源类型: form_field/table_column/form_template/table_template
     */
    private String resourceType;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 资源名称（冗余）
     */
    private String resourceName;

    /**
     * 所属页面ID
     */
    private Long pageId;

    /**
     * 所属页面名称（冗余）
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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
