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
 * 表格配置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("low_table_config")
public class LowTableConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表格名称
     */
    private String tableName;

    /**
     * 表格编码
     */
    private String tableCode;

    /**
     * 所属页面ID
     */
    private Long pageId;

    /**
     * 数据源类型(api/sql/static)
     */
    private String dataSourceType;

    /**
     * API地址
     */
    private String apiUrl;

    /**
     * 请求方法
     */
    private String apiMethod;

    /**
     * API参数配置JSON
     */
    private String apiParamsJson;

    /**
     * SQL语句
     */
    private String sqlContent;

    /**
     * 是否分页
     */
    @TableField("is_pagination")
    private Boolean pagination;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 每页条数选项
     */
    private String pageSizes;

    /**
     * 是否多选
     */
    @TableField("is_selection")
    private Boolean selection;

    /**
     * 选择类型(checkbox/radio)
     */
    private String selectionType;

    /**
     * 是否显示序号
     */
    @TableField("is_index")
    private Boolean showIndex;

    /**
     * 是否显示边框
     */
    @TableField("is_border")
    private Boolean border;

    /**
     * 是否斑马纹
     */
    @TableField("is_stripe")
    private Boolean stripe;

    /**
     * 行数据唯一标识
     */
    private String rowKey;

    /**
     * 空数据提示
     */
    private String emptyText;

    /**
     * 表格高度
     */
    private String height;

    /**
     * 表格最大高度
     */
    private String maxHeight;

    /**
     * 表格配置JSON
     */
    private String configJson;

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
     * 表格列列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<LowTableColumn> columns;
}
