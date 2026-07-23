package com.lowcode.platform.generator;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.sql.Types;

/**
 * 列信息 DTO
 * 用于存储从数据库读取的表字段信息
 */
@Data
@Builder
@Schema(description = "列信息")
public class ColumnInfo {

    @Schema(description = "列名")
    private String columnName;

    @Schema(description = "JDBC类型")
    private int columnType;

    @Schema(description = "数据库类型名")
    private String typeName;

    @Schema(description = "列大小")
    private int columnSize;

    @Schema(description = "是否可空")
    private boolean nullable;

    @Schema(description = "默认值")
    private String defaultValue;

    @Schema(description = "列注释/备注")
    private String remark;

    @Schema(description = "是否为主键")
    private boolean isPrimaryKey;

    /**
     * 判断是否为字符串类型
     */
    public boolean isStringType() {
        return columnType == Types.VARCHAR
            || columnType == Types.CHAR
            || columnType == Types.LONGVARCHAR
            || columnType == Types.NVARCHAR
            || columnType == Types.NCHAR
            || columnType == Types.LONGNVARCHAR;
    }

    /**
     * 判断是否为数字类型
     */
    public boolean isNumberType() {
        return columnType == Types.BIGINT
            || columnType == Types.INTEGER
            || columnType == Types.SMALLINT
            || columnType == Types.TINYINT
            || columnType == Types.DECIMAL
            || columnType == Types.NUMERIC
            || columnType == Types.FLOAT
            || columnType == Types.DOUBLE;
    }

    /**
     * 判断是否为日期类型
     */
    public boolean isDateType() {
        return columnType == Types.DATE
            || columnType == Types.TIME
            || columnType == Types.TIMESTAMP
            || columnType == Types.TIME_WITH_TIMEZONE
            || columnType == Types.TIMESTAMP_WITH_TIMEZONE;
    }

    /**
     * 判断是否为布尔类型
     */
    public boolean isBooleanType() {
        return columnType == Types.BOOLEAN
            || columnType == Types.BIT;
    }

    /**
     * 判断是否为 BLOB 类型
     */
    public boolean isBlobType() {
        return columnType == Types.BLOB
            || columnType == Types.BINARY
            || columnType == Types.VARBINARY
            || columnType == Types.LONGVARBINARY;
    }

    /**
     * 映射为 Java 类型
     */
    public String toJavaType() {
        if (isStringType()) {
            return "String";
        }
        if (isNumberType()) {
            String upperTypeName = typeName.toUpperCase();
            if (upperTypeName.equals("BIGINT") || upperTypeName.equals("DECIMAL")) {
                return "Long";
            }
            if (upperTypeName.equals("NUMERIC") || upperTypeName.equals("FLOAT") || upperTypeName.equals("DOUBLE")) {
                return "BigDecimal";
            }
            return "Integer";
        }
        if (isDateType()) {
            String upperTypeName = typeName.toUpperCase();
            if (upperTypeName.contains("TIME") && !upperTypeName.equals("DATE")) {
                return "LocalTime";
            }
            if (upperTypeName.contains("TIMESTAMP")) {
                return "LocalDateTime";
            }
            return "LocalDate";
        }
        if (isBooleanType()) {
            return "Boolean";
        }
        if (isBlobType()) {
            return "byte[]";
        }
        return "String";
    }

    /**
     * 转为 CamelCase 风格的字段名
     */
    public String toCamelCase() {
        return toCamelCase(this.columnName);
    }

    /**
     * 将下划线命名转为 CamelCase
     */
    public static String toCamelCase(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();
    }

    /**
     * 获取备注，如果没有备注则返回列名
     */
    public String getRemarkOrColumnName() {
        return remark != null && !remark.isEmpty() ? remark : columnName;
    }
}