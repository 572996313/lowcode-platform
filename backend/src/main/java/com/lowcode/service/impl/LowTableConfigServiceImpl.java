package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.LowTableColumn;
import com.lowcode.entity.LowTableConfig;
import com.lowcode.mapper.LowTableColumnMapper;
import com.lowcode.mapper.LowTableConfigMapper;
import com.lowcode.service.ILowTableConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 表格配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LowTableConfigServiceImpl extends ServiceImpl<LowTableConfigMapper, LowTableConfig> implements ILowTableConfigService {

    private final LowTableColumnMapper tableColumnMapper;

    @Override
    public PageResult<LowTableConfig> getTableList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        String tableName = params.get("tableName") != null ? params.get("tableName").toString() : null;

        Page<LowTableConfig> page = new Page<>(current, size);

        LambdaQueryWrapper<LowTableConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(tableName), LowTableConfig::getTableName, tableName)
               .orderByDesc(LowTableConfig::getUpdateTime);

        Page<LowTableConfig> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public LowTableConfig getTableConfig(Long id) {
        LowTableConfig tableConfig = getById(id);
        if (tableConfig == null) {
            throw new BusinessException("表格配置不存在");
        }

        // 查询表格列配置
        List<LowTableColumn> columns = tableColumnMapper.selectList(
                Wrappers.lambdaQuery(LowTableColumn.class)
                        .eq(LowTableColumn::getTableId, id)
                        .orderByAsc(LowTableColumn::getSortOrder)
        );
        tableConfig.setColumns(columns);

        return tableConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTableConfig(LowTableConfig tableConfig) {
        // 检查编码是否重复
        if (StrUtil.isNotBlank(tableConfig.getTableCode())) {
            long count = lambdaQuery()
                    .eq(LowTableConfig::getTableCode, tableConfig.getTableCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("表格编码已存在");
            }
        }

        // 设置默认值
        if (tableConfig.getStatus() == null) {
            tableConfig.setStatus(true);
        }
        if (tableConfig.getPagination() == null) {
            tableConfig.setPagination(true);
        }
        if (tableConfig.getPageSize() == null) {
            tableConfig.setPageSize(10);
        }

        save(tableConfig);

        // 保存表格列配置
        if (CollUtil.isNotEmpty(tableConfig.getColumns())) {
            saveTableColumns(tableConfig.getId(), tableConfig.getColumns());
        }

        log.info("创建表格配置成功, id: , code: {}", tableConfig.getId(), tableConfig.getTableCode());
        return tableConfig.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTableConfig(Long id, LowTableConfig tableConfig) {
        LowTableConfig existConfig = getById(id);
        if (existConfig == null) {
            throw new BusinessException("表格配置不存在");
        }

        // 检查编码是否重复
        if (StrUtil.isNotBlank(tableConfig.getTableCode()) && !tableConfig.getTableCode().equals(existConfig.getTableCode())) {
            long count = lambdaQuery()
                    .eq(LowTableConfig::getTableCode, tableConfig.getTableCode())
                    .ne(LowTableConfig::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("表格编码已存在");
            }
        }

        tableConfig.setId(id);
        updateById(tableConfig);

        // 更新表格列配置
        if (tableConfig.getColumns() != null) {
            // 删除原有列配置
            tableColumnMapper.delete(
                    Wrappers.lambdaQuery(LowTableColumn.class)
                            .eq(LowTableColumn::getTableId, id)
            );
            // 保存新列配置
            if (CollUtil.isNotEmpty(tableConfig.getColumns())) {
                saveTableColumns(id, tableConfig.getColumns());
            }
        }

        log.info("更新表格配置成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTableConfig(Long id) {
        // 删除表格列配置
        tableColumnMapper.delete(
                Wrappers.lambdaQuery(LowTableColumn.class)
                        .eq(LowTableColumn::getTableId, id)
        );
        // 删除表格配置
        removeById(id);
        log.info("删除表格配置成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTableColumns(Long tableId, List<LowTableColumn> columns) {
        for (int i = 0; i < columns.size(); i++) {
            LowTableColumn column = columns.get(i);
            column.setTableId(tableId);
            column.setSortOrder(i);
            tableColumnMapper.insert(column);
        }
    }

    @Override
    public List<LowTableConfig> getByCategory(String category) {
        return list(
                Wrappers.lambdaQuery(LowTableConfig.class)
                        .eq(LowTableConfig::getComponentCategory, category)
                        .eq(LowTableConfig::getStatus, true)
                        .orderByDesc(LowTableConfig::getUpdateTime)
        );
    }

    @Override
    public Long countByCategory(String category) {
        return lambdaQuery()
                .eq(LowTableConfig::getComponentCategory, category)
                .count();
    }
}
