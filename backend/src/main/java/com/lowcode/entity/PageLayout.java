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
 * 页面布局配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("page_layout")
public class PageLayout implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 布局名称
     */
    private String layoutName;

    /**
     * 布局编码（唯一）
     */
    private String layoutCode;

    /**
     * 布局类型(top-bottom/left-right/tree-table/tabs)
     */
    private String layoutType;

    /**
     * 区域配置JSON
     * 格式示例：
     * {
     *   "areas": [
     *     {"id": "toolbar", "title": "工具栏", "componentType": "button"},
     *     {"id": "search", "title": "搜索区", "componentType": "form", "formId": 1},
     *     {"id": "content", "title": "内容区", "componentType": "table", "tableId": 1}
     *   ]
     * }
     */
    private String areaConfig;

    /**
     * 是否已发布
     */
    private Boolean published;

    /**
     * 路由路径
     */
    private String routePath;

    /**
     * 关联菜单ID
     */
    private Long menuId;

    /**
     * 状态（1=启用，0=禁用）
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
     * 关联的表单列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<LowFormConfig> forms;

    /**
     * 关联的表格列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<LowTableConfig> tables;
}
