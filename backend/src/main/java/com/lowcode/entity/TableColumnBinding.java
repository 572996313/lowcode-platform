package com.lowcode.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表格列绑定配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("table_column_binding")
public class TableColumnBinding implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表格配置ID（关联low_table_config.id）
     */
    private Long tableConfigId;

    /**
     * 字段ID（关联db_table_field.id）
     */
    private Long fieldId;

    /**
     * 列标题
     */
    private String columnLabel;

    /**
     * 列类型（text/image/tag/switch/dict/link/date/number等）
     */
    private String columnType;

    /**
     * 列宽度
     */
    private Integer width;

    /**
     * 最小列宽度
     */
    private Integer minWidth;

    /**
     * 是否可排序
     */
    private Boolean sortable;

    /**
     * 固定列（left/right）
     */
    private String fixed;

    /**
     * 对齐方式（left/center/right）
     */
    private String align;

    /**
     * 是否溢出省略
     */
    private Boolean showOverflow;

    /**
     * 格式化配置JSON
     * 格式示例：
     * {
     *   "dictCode": "status",  // 字典编码
     *   "color": "green",      // 标签颜色
     *   "format": "YYYY-MM-DD" // 日期格式
     * }
     */
    private String formatterConfig;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 是否可见
     */
    private Boolean visible;

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
     * 关联的表格配置（非数据库字段）
     */
    @TableField(exist = false)
    private LowTableConfig tableConfig;

    /**
     * 关联的字段信息（非数据库字段）
     */
    @TableField(exist = false)
    private DbTableField field;
}
