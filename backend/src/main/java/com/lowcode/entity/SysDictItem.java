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
 * 字典明细实体类
 *
 * @author lowcode
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_dict_item")
public class SysDictItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 父项ID，0表示根项
     */
    private Long parentId;

    /**
     * 字典项显示文本
     */
    private String itemLabel;

    /**
     * 字典项值
     */
    private String itemValue;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Boolean status;

    /**
     * 描述
     */
    private String description;

    /**
     * CSS样式类
     */
    private String cssClass;

    /**
     * 图标
     */
    private String icon;

    /**
     * 逻辑删除：0-正常，1-删除
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

    /**
     * 子项列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysDictItem> children;
}
