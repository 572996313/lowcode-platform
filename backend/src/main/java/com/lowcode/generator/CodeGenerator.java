package com.lowcode.generator;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 代码生成器 - 可直接运行 main 方法
 *
 * 使用方式：直接运行 main 方法，修改配置区的表名等参数即可
 */
@Slf4j
public class CodeGenerator {

    // ========== 配置区域 - 修改这里指定要生成的代码 ==========

    private static final String MODULE_NAME = "sys";             // 模块名
    private static final String TABLE_NAME = "sys_dict_item";    // 数据库表名
    private static final String ENTITY_NAME = "DictItem";       // 实体类名
    private static final String TABLE_NAME_CN = "字典项";        // 表中文名
    private static final String PRIMARY_KEY_LABEL = "字典项ID";  // 主键说明

    // 输出路径（空字符串表示默认路径）
    private static final String OUTPUT_PATH = "";

    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/lowcode_platform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    // ========== main 方法 ==========

    public static void main(String[] args) {
        log.info("========== 代码生成开始 ==========");
        log.info("表名: {}", TABLE_NAME);
        log.info("实体名: {}", ENTITY_NAME);

        try {
            // 1. 读取表结构
            List<ColumnInfo> allColumns = readTableColumns(TABLE_NAME);
            List<String> primaryKeys = readPrimaryKeys(TABLE_NAME);

            // 标记主键
            for (ColumnInfo column : allColumns) {
                if (primaryKeys.contains(column.getColumnName())) {
                    column.setPrimaryKey(true);
                }
            }

            // 过滤业务字段
            List<ColumnInfo> businessColumns = filterBusinessColumns(allColumns);

            // 2. 判断主键类型
            boolean isStringPrimaryKey = isPrimaryKeyStringType(primaryKeys, allColumns);
            String idType = isStringPrimaryKey ? "IdType.ASSIGN_ID" : "IdType.AUTO";
            String idJavaType = isStringPrimaryKey ? "String" : "Long";

            // 3. 构建渲染数据
            Map<String, Object> data = buildTemplateData(idType, idJavaType, businessColumns);

            // 4. 获取输出路径
            String outputPath = resolveOutputPath(OUTPUT_PATH);
            log.info("输出路径: {}", outputPath);

            // 5. 创建目录
            createDirectories(outputPath);

            // 6. 创建模板引擎并生成代码
            TemplateEngine engine = new TemplateEngine();
            generateFiles(engine, data, outputPath);

            log.info("========== 代码生成完成 ==========");

        } catch (Exception e) {
            log.error("代码生成失败", e);
            System.exit(1);
        }
    }

    // ========== 构建模板数据 ==========

    private static Map<String, Object> buildTemplateData(String idType, String idJavaType,
                                                       List<ColumnInfo> businessColumns) {
        Map<String, Object> data = new HashMap<>();

        // 基本信息
        data.put("moduleName", MODULE_NAME);
        data.put("tableName", TABLE_NAME);
        data.put("entityName", ENTITY_NAME);
        data.put("tableNameCN", TABLE_NAME_CN);
        data.put("primaryKeyLabel", PRIMARY_KEY_LABEL);

        // 包路径和 API 路径
        String packagePath = "com.lowcode.modules." + MODULE_NAME;
        data.put("packagePath", packagePath);
        data.put("apiPath", "/api/" + MODULE_NAME + "/" + toKebabCase(ENTITY_NAME));

        // 名称变体
        data.put("entityNameLower", ENTITY_NAME.toLowerCase());
        data.put("entityNameKebab", toKebabCase(ENTITY_NAME));

        // 主键类型
        data.put("idType", idType);
        data.put("idJavaType", idJavaType);

        // 字段列表
        data.put("businessColumns", businessColumns);

        return data;
    }

    // ========== 生成文件 ==========

    private static void generateFiles(TemplateEngine engine, Map<String, Object> data, String outputPath) {
        // 渲染模板
        String entityCode = engine.render("entity.ftl", data);
        String mapperCode = engine.render("mapper.ftl", data);
        String serviceCode = engine.render("service.ftl", data);
        String serviceImplCode = engine.render("serviceImpl.ftl", data);
        String controllerCode = engine.render("controller.ftl", data);
        String dtoCode = engine.render("dto.ftl", data);

        // 写入文件
        writeFile(outputPath + "/entity", ENTITY_NAME + "Entity.java", entityCode);
        writeFile(outputPath + "/mapper", ENTITY_NAME + "Mapper.java", mapperCode);
        writeFile(outputPath + "/service", "I" + ENTITY_NAME + "Service.java", serviceCode);
        writeFile(outputPath + "/service/impl", ENTITY_NAME + "ServiceImpl.java", serviceImplCode);
        writeFile(outputPath + "/controller", ENTITY_NAME + "Controller.java", controllerCode);
        writeFile(outputPath + "/dto", ENTITY_NAME + "DTO.java", dtoCode);
    }

    // ========== 表结构读取 ==========

    private static List<ColumnInfo> readTableColumns(String tableName) {
        List<ColumnInfo> columns = new ArrayList<>();

        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName, null);

            while (rs.next()) {
                ColumnInfo column = ColumnInfo.builder()
                        .columnName(rs.getString("COLUMN_NAME"))
                        .columnType(rs.getInt("DATA_TYPE"))
                        .typeName(rs.getString("TYPE_NAME"))
                        .columnSize(rs.getInt("COLUMN_SIZE"))
                        .nullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable)
                        .defaultValue(rs.getString("COLUMN_DEF"))
                        .remark(rs.getString("REMARK"))
                        .isPrimaryKey(false)
                        .build();
                columns.add(column);
            }
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("读取表结构失败: " + tableName, e);
        }

        return columns;
    }

    private static List<String> readPrimaryKeys(String tableName) {
        List<String> primaryKeys = new ArrayList<>();

        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getPrimaryKeys(null, null, tableName);

            while (rs.next()) {
                primaryKeys.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("读取主键失败: " + tableName, e);
        }

        return primaryKeys;
    }

    private static boolean isPrimaryKeyStringType(List<String> primaryKeys, List<ColumnInfo> allColumns) {
        for (ColumnInfo column : allColumns) {
            if (primaryKeys.contains(column.getColumnName()) && column.isStringType()) {
                return true;
            }
        }
        return false;
    }

    private static List<ColumnInfo> filterBusinessColumns(List<ColumnInfo> columns) {
        return columns.stream()
                .filter(c -> !isSystemColumn(c.getColumnName()))
                .toList();
    }

    private static boolean isSystemColumn(String columnName) {
        String upper = columnName.toLowerCase();
        return upper.equals("deleted")
            || upper.equals("create_by")
            || upper.equals("create_time")
            || upper.equals("update_by")
            || upper.equals("update_time")
            || upper.equals("status")
            || upper.equals("enabled")
            || upper.equals("id")
            || upper.equals("create_date")
            || upper.equals("update_date");
    }

    // ========== 数据库连接 ==========

    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            throw new RuntimeException("获取数据库连接失败", e);
        }
    }

    // ========== 路径处理 ==========

    private static String resolveOutputPath(String configPath) {
        if (configPath != null && !configPath.isEmpty()) {
            return configPath;
        }
        // 默认输出到当前项目
        String userDir = System.getProperty("user.dir");
        return userDir + "/src/main/java/com/lowcode/modules/" + MODULE_NAME;
    }

    private static void createDirectories(String basePath) {
        String[] dirs = {"entity", "mapper", "service", "service/impl", "controller", "dto"};
        for (String dir : dirs) {
            File directory = new File(basePath + "/" + dir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
    }

    private static void writeFile(String dir, String fileName, String content) {
        try {
            File file = new File(dir, fileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }
            log.info("生成文件: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("写入文件失败: {}/{}", dir, fileName, e);
            throw new RuntimeException("写入文件失败: " + dir + "/" + fileName, e);
        }
    }

    // ========== 工具方法 ==========

    private static String toKebabCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('-');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}