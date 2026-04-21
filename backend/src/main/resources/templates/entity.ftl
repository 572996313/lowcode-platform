package ${packagePath}.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "${tableNameCN}实体类")
@TableName("${tableName}")
public class ${entityName}Entity implements Serializable {

    // ========== 主键 ==========
    @Schema(description = "${primaryKeyLabel}")
    @TableId(type = ${idType})
    private ${idJavaType} id;

    // ========== 系统字段 ==========
    @Schema(description = "删除标志")
    @TableLogic
    private Boolean deleted;

    @Schema(description = "创建者")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "状态（1正常 0停用）")
    private Boolean status;

    @Schema(description = "启用状态")
    private Boolean enabled;

    // ========== 业务字段 ==========
<#list businessColumns as column>
    @Schema(description = "${column.getRemarkOrColumnName()}")
    private ${column.toJavaType()} ${column.toCamelCase()};

</#list>
}