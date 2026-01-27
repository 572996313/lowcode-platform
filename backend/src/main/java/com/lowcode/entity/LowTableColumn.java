package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表格列配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_table_column")
public class LowTableColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表格ID
     */
    private Long tableId;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列编码(字段名)
     */
    private String columnCode;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * 列标题
     */
    private String label;

    /**
     * 列宽度
     */
    private Integer width;

    /**
     * 最小宽度
     */
    private Integer minWidth;

    /**
     * 是否可排序
     */
    @TableField("is_sortable")
    private Boolean sortable;

    /**
     * 排序方式
     */
    private String sortOrders;

    /**
     * 固定列(left/right)
     */
    @TableField("is_fixed")
    private String fixed;

    /**
     * 对齐方式
     */
    private String align;

    /**
     * 表头对齐方式
     */
    private String headerAlign;

    /**
     * 是否可调整宽度
     */
    @TableField("is_resizable")
    private Boolean resizable;

    /**
     * 是否显示溢出提示
     */
    @TableField("is_show_overflow_tooltip")
    private Boolean showOverflowTooltip;

    /**
     * 格式化类型
     */
    private String formatterType;

    /**
     * 格式化配置JSON
     */
    private String formatterConfig;

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否显示
     */
    @TableField("is_visible")
    private Boolean visible;

    /**
     * 是否导出
     */
    @TableField("is_export")
    private Boolean export;

    /**
     * 列属性JSON
     */
    private String propsJson;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

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
}
