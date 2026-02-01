package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.DbTable;
import com.lowcode.entity.DbTableField;
import com.lowcode.mapper.DbTableFieldMapper;
import com.lowcode.mapper.DbTableMapper;
import com.lowcode.service.IDbTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 库表管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DbTableServiceImpl extends ServiceImpl<DbTableMapper, DbTable> implements IDbTableService {

    private final DbTableFieldMapper dbTableFieldMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public PageResult<DbTable> getTableList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        String tableName = params.get("tableName") != null ? params.get("tableName").toString() : null;
        String tableType = params.get("tableType") != null ? params.get("tableType").toString() : null;
        Integer syncStatus = params.get("syncStatus") != null ? Integer.parseInt(params.get("syncStatus").toString()) : null;

        Page<DbTable> page = new Page<>(current, size);

        LambdaQueryWrapper<DbTable> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(tableName), DbTable::getTableName, tableName)
               .eq(StrUtil.isNotBlank(tableType), DbTable::getTableType, tableType)
               .eq(syncStatus != null, DbTable::getSyncStatus, syncStatus)
               .orderByDesc(DbTable::getUpdateTime);

        Page<DbTable> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public DbTable getTableDetail(Long id) {
        DbTable dbTable = getById(id);
        if (dbTable == null) {
            throw new BusinessException("库表不存在");
        }

        // 查询字段列表
        List<DbTableField> fields = dbTableFieldMapper.selectList(
                Wrappers.lambdaQuery(DbTableField.class)
                        .eq(DbTableField::getTableId, id)
                        .orderByAsc(DbTableField::getOrdinalPosition)
        );
        dbTable.setFields(fields);

        return dbTable;
    }

    @Override
    public List<DbTableField> getTableFields(Long tableId) {
        return dbTableFieldMapper.selectList(
                Wrappers.lambdaQuery(DbTableField.class)
                        .eq(DbTableField::getTableId, tableId)
                        .orderByAsc(DbTableField::getOrdinalPosition)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scanAllTables() {
        log.info("开始扫描数据库表和视图...");

        Connection conn = null;
        ResultSet tablesRs = null;

        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData metaData = conn.getMetaData();

            // 获取数据库产品名称
            String catalog = conn.getCatalog(); // 数据库名
            String schema = null; // MySQL 不使用 schema

            // 获取所有表和视图
            String[] tableTypes = {"TABLE", "VIEW"};
            tablesRs = metaData.getTables(catalog, schema, "%", tableTypes);

            List<DbTable> tableList = new ArrayList<>();
            Set<String> tableKeys = new HashSet<>();

            // 查询现有的表记录
            List<DbTable> existTables = list();
            for (DbTable t : existTables) {
                tableKeys.add(t.getTableSchema() + "." + t.getTableName());
            }

            while (tablesRs.next()) {
                // MySQL 中数据库名在 TABLE_CAT，TABLE_SCHEM 为 null
                String tableSchema = tablesRs.getString("TABLE_CAT");
                if (tableSchema == null) {
                    tableSchema = conn.getCatalog(); // 备用方案
                }
                String tableName = tablesRs.getString("TABLE_NAME");
                String tableType = tablesRs.getString("TABLE_TYPE");
                String tableComment = tablesRs.getString("REMARKS");

                String tableKey = tableSchema + "." + tableName;

                // 系统表过滤（可根据需要调整）
                if (tableName.startsWith("information_schema") ||
                    tableName.startsWith("mysql") ||
                    tableName.startsWith("performance_schema") ||
                    tableName.startsWith("sys")) {
                    continue;
                }

                // 判断是否需要更新
                DbTable dbTable;
                if (tableKeys.contains(tableKey)) {
                    // 已存在，更新信息
                    dbTable = lambdaQuery()
                            .eq(DbTable::getTableSchema, tableSchema)
                            .eq(DbTable::getTableName, tableName)
                            .one();
                    if (dbTable != null) {
                        dbTable.setTableComment(tableComment);
                        updateById(dbTable);
                    }
                } else {
                    // 新增
                    dbTable = DbTable.builder()
                            .tableSchema(tableSchema)
                            .tableName(tableName)
                            .tableType(tableType)
                            .tableComment(tableComment)
                            .syncStatus(0)
                            .build();
                    tableList.add(dbTable);
                }
            }

            // 批量保存新表
            if (CollUtil.isNotEmpty(tableList)) {
                saveBatch(tableList);
                log.info("新增库表数量: {}", tableList.size());
            }

            log.info("扫描数据库完成");

        } catch (SQLException e) {
            log.error("扫描数据库失败", e);
            throw new BusinessException("扫描数据库失败: " + e.getMessage());
        } finally {
            try {
                if (tablesRs != null) tablesRs.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                log.error("关闭数据库连接失败", e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncTableFields(Long tableId) {
        DbTable dbTable = getById(tableId);
        if (dbTable == null) {
            throw new BusinessException("库表不存在");
        }

        log.info("开始同步表字段: {}.{}", dbTable.getTableSchema(), dbTable.getTableName());

        Connection conn = null;
        ResultSet columnsRs = null;
        ResultSet primaryKeysRs = null;

        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData metaData = conn.getMetaData();

            String catalog = dbTable.getTableSchema();
            String tableName = dbTable.getTableName();

            // 获取主键信息
            Set<String> primaryKeys = new HashSet<>();
            primaryKeysRs = metaData.getPrimaryKeys(catalog, null, tableName);
            while (primaryKeysRs.next()) {
                String pkName = primaryKeysRs.getString("COLUMN_NAME");
                primaryKeys.add(pkName);
            }

            // 获取列信息
            List<DbTableField> fieldList = new ArrayList<>();
            columnsRs = metaData.getColumns(catalog, null, tableName, null);

            // 获取现有字段，保留用户编辑过的 fieldLabel
            List<DbTableField> existFields = dbTableFieldMapper.selectList(
                    Wrappers.lambdaQuery(DbTableField.class)
                            .eq(DbTableField::getTableId, tableId)
            );
            Map<String, String> fieldLabelMap = new HashMap<>();
            for (DbTableField f : existFields) {
                if (StrUtil.isNotBlank(f.getFieldLabel())) {
                    fieldLabelMap.put(f.getFieldName(), f.getFieldLabel());
                }
            }

            while (columnsRs.next()) {
                String fieldName = columnsRs.getString("COLUMN_NAME");
                String fieldType = columnsRs.getString("TYPE_NAME");
                int fieldSize = columnsRs.getInt("COLUMN_SIZE");
                int decimalDigits = columnsRs.getInt("DECIMAL_DIGITS");
                int isNullable = columnsRs.getInt("NULLABLE");
                String columnDefault = columnsRs.getString("COLUMN_DEF");
                String remarks = columnsRs.getString("REMARKS");
                int ordinalPosition = columnsRs.getInt("ORDINAL_POSITION");

                DbTableField field = DbTableField.builder()
                        .tableId(tableId)
                        .fieldName(fieldName)
                        .fieldType(fieldType)
                        .fieldLength(fieldSize > 0 ? fieldSize : null)
                        .decimalDigits(decimalDigits > 0 ? decimalDigits : null)
                        .isNullable(isNullable == DatabaseMetaData.columnNullable ? 1 : 0)
                        .columnDefault(columnDefault)
                        .isPrimaryKey(primaryKeys.contains(fieldName) ? 1 : 0)
                        .isAutoIncrement(fieldType.toUpperCase().contains("AUTO_INCREMENT") ? 1 : 0)
                        .fieldComment(remarks)
                        .ordinalPosition(ordinalPosition)
                        .build();

                // 保留用户编辑过的 fieldLabel
                if (fieldLabelMap.containsKey(fieldName)) {
                    field.setFieldLabel(fieldLabelMap.get(fieldName));
                }

                fieldList.add(field);
            }

            // 删除旧字段（覆盖更新策略）
            dbTableFieldMapper.delete(
                    Wrappers.lambdaQuery(DbTableField.class)
                            .eq(DbTableField::getTableId, tableId)
            );

            // 插入新字段
            for (DbTableField field : fieldList) {
                dbTableFieldMapper.insert(field);
            }

            // 更新表的同步状态
            dbTable.setSyncStatus(1);
            dbTable.setSyncTime(LocalDateTime.now());
            updateById(dbTable);

            log.info("同步表字段完成: {}, 字段数量: {}", tableName, fieldList.size());

        } catch (SQLException e) {
            log.error("同步表字段失败", e);
            throw new BusinessException("同步表字段失败: " + e.getMessage());
        } finally {
            try {
                if (columnsRs != null) columnsRs.close();
                if (primaryKeysRs != null) primaryKeysRs.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                log.error("关闭数据库连接失败", e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTable(Long id, DbTable dbTable) {
        DbTable existTable = getById(id);
        if (existTable == null) {
            throw new BusinessException("库表不存在");
        }

        dbTable.setId(id);
        updateById(dbTable);

        log.info("更新库表信息成功, id: {}", id);
    }

    @Override
    public void updateFieldLabel(Long fieldId, String fieldLabel) {
        DbTableField existField = dbTableFieldMapper.selectById(fieldId);
        if (existField == null) {
            throw new BusinessException("字段不存在");
        }

        DbTableField updateField = new DbTableField();
        updateField.setId(fieldId);
        updateField.setFieldLabel(fieldLabel);
        dbTableFieldMapper.updateById(updateField);

        log.info("更新字段标签成功, id: {}, label: {}", fieldId, fieldLabel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateFieldLabels(List<Map<String, Object>> fieldUpdates) {
        if (CollUtil.isEmpty(fieldUpdates)) {
            return;
        }

        for (Map<String, Object> update : fieldUpdates) {
            Long fieldId = update.get("id") != null ? Long.parseLong(update.get("id").toString()) : null;
            String fieldLabel = update.get("fieldLabel") != null ? update.get("fieldLabel").toString() : null;

            if (fieldId != null) {
                DbTableField updateField = new DbTableField();
                updateField.setId(fieldId);
                updateField.setFieldLabel(fieldLabel);
                dbTableFieldMapper.updateById(updateField);
            }
        }

        log.info("批量更新字段标签成功, 数量: {}", fieldUpdates.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTable(Long id) {
        DbTable existTable = getById(id);
        if (existTable == null) {
            throw new BusinessException("库表不存在");
        }

        // 级联删除字段
        dbTableFieldMapper.delete(
                Wrappers.lambdaQuery(DbTableField.class)
                        .eq(DbTableField::getTableId, id)
        );

        // 删除表
        removeById(id);

        log.info("删除库表成功, id: {}", id);
    }
}
