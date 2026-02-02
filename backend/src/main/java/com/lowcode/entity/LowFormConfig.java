package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 表单配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_form_config")
public class LowFormConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的模板ID
     */
    private Long templateId;

    /**
     * 表单模板编码（关联前端 TemplateRegistry）
     */
    private String templateCode;

    /**
     * 来源类型（template=模板 custom=自定义）
     */
    private String sourceType;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 表单编码
     */
    private String formCode;

    /**
     * 关联数据库表ID（关联db_table.id）
     */
    private Long tableId;

    /**
     * 样式模板ID（关联form_style_template.id）
     */
    private Long styleTemplateId;

    /**
     * 组件分类(common=通用组件库, business=业务组件库)
     */
    private String componentCategory;

    /**
     * 组件标签JSON: ["system","user"]
     */
    private String componentTags;

    /**
     * 表单类型(add/edit/search/detail/dialog)
     */
    private String formType;

    /**
     * 布局类型(horizontal/vertical/inline)
     */
    private String layoutType;

    /**
     * 表单列数
     */
    private Integer layoutCols;

    /**
     * 标签宽度
     */
    private Integer labelWidth;

    /**
     * 标签位置(left/right/top)
     */
    private String labelPosition;

    /**
     * 组件尺寸(large/default/small)
     */
    private String size;

    /**
     * 表单配置JSON
     */
    private String configJson;

    /**
     * 校验规则JSON
     */
    private String rulesJson;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 表单字段列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<LowFormField> fields;
}
