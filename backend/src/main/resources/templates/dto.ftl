package ${packagePath}.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "${tableNameCN}查询DTO")
public class ${entityName}QueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页")
    private Integer current = 1;

    @Schema(description = "每页条数")
    private Integer size = 10;

<#list businessColumns as column>
    @Schema(description = "${column.getRemarkOrColumnName()}")
    private ${column.toJavaType()} ${column.toCamelCase()};

</#list>
}

@Data
@Schema(description = "${tableNameCN}保存DTO")
public class ${entityName}SaveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID（编辑时传）")
    private ${idJavaType} id;

<#list businessColumns as column>
    @Schema(description = "${column.getRemarkOrColumnName()}")
    private ${column.toJavaType()} ${column.toCamelCase()};

</#list>
}