package com.lowcode.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL脚本执行工具
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SqlScriptExecutor {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 执行SQL文件
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeScript(String filePath) throws Exception {
        log.info("开始执行SQL脚本: {}", filePath);

        // 读取SQL文件内容
        String content = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);

        // 分割SQL语句
        List<String> statements = parseSqlStatements(content);

        log.info("共解析到 {} 条SQL语句", statements.size());

        // 执行每条SQL
        int successCount = 0;
        for (String statement : statements) {
            try {
                if (statement.trim().isEmpty()) {
                    continue;
                }
                jdbcTemplate.execute(statement);
                successCount++;
                log.debug("执行成功: {}", statement.substring(0, Math.min(100, statement.length())));
            } catch (Exception e) {
                log.error("执行SQL失败: {}", statement.substring(0, Math.min(200, statement.length())), e);
                throw e;
            }
        }

        log.info("SQL脚本执行完成，成功执行 {} 条语句", successCount);
    }

    /**
     * 解析SQL语句
     */
    private List<String> parseSqlStatements(String content) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();
        boolean inDelimiter = false;

        String[] lines = content.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();

            // 跳过注释行
            if (trimmedLine.startsWith("--") || trimmedLine.startsWith("#")) {
                continue;
            }

            // 跳过空行
            if (trimmedLine.isEmpty()) {
                continue;
            }

            // 检查是否是SET命令（在delimiter外）
            if (trimmedLine.toUpperCase().startsWith("SET ")) {
                statements.add(trimmedLine);
                continue;
            }

            // 检查是否是DELIMITER命令
            if (trimmedLine.toUpperCase().startsWith("DELIMITER")) {
                inDelimiter = trimmedLine.length() > 9;
                continue;
            }

            // 追加当前行
            currentStatement.append(line).append("\n");

            // 检查语句是否结束（以分号结尾）
            if (trimmedLine.endsWith(";") && !inDelimiter) {
                statements.add(currentStatement.toString());
                currentStatement = new StringBuilder();
            }
        }

        // 添加最后一条语句
        if (currentStatement.length() > 0) {
            statements.add(currentStatement.toString());
        }

        return statements;
    }

    /**
     * 检查表是否存在
     */
    public boolean tableExists(String tableName) {
        try {
            String sql = "SHOW TABLES LIKE ?";
            List<String> tables = jdbcTemplate.queryForList(sql, String.class, tableName);
            return !tables.isEmpty();
        } catch (Exception e) {
            log.error("检查表是否存在失败: {}", tableName, e);
            return false;
        }
    }

    /**
     * 检查数据库是否存在
     */
    public boolean databaseExists(String databaseName) {
        try {
            String sql = "SHOW DATABASES LIKE ?";
            List<String> databases = jdbcTemplate.queryForList(sql, String.class, databaseName);
            return !databases.isEmpty();
        } catch (Exception e) {
            log.error("检查数据库是否存在失败: {}", databaseName, e);
            return false;
        }
    }
}
