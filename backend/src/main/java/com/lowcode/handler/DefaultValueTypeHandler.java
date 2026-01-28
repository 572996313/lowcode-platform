package com.lowcode.handler;

import cn.hutool.json.JSONUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 默认值类型处理器
 * 用于将 Object 类型与数据库 VARCHAR 类型进行转换
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Object.class)
public class DefaultValueTypeHandler extends BaseTypeHandler<Object> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, JdbcType.VARCHAR.TYPE_CODE);
        } else {
            // 将 Object 转换为 JSON 字符串存储
            ps.setString(i, JSONUtil.toJsonStr(parameter));
        }
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return parseValue(value);
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return parseValue(value);
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return parseValue(value);
    }

    /**
     * 解析 JSON 字符串为 Object
     * 自动识别 String、Array、Boolean、Number 等类型
     */
    private Object parseValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return JSONUtil.parse(value);
    }
}
