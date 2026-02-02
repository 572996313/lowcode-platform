package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.DbTableField;
import com.lowcode.entity.LowTableConfig;
import com.lowcode.entity.TableColumnBinding;
import com.lowcode.mapper.DbTableFieldMapper;
import com.lowcode.mapper.LowTableConfigMapper;
import com.lowcode.mapper.TableColumnBindingMapper;
import com.lowcode.service.ITableColumnBindingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 表格列绑定服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TableColumnBindingServiceImpl extends ServiceImpl<TableColumnBindingMapper, TableColumnBinding> implements ITableColumnBindingService {

    private final DbTableFieldMapper dbTableFieldMapper;
    private final LowTableConfigMapper lowTableConfigMapper;

    @Override
    public PageResult<TableColumnBinding> getTableColumnBindingList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        Long tableConfigId = params.get("tableConfigId") != null ? Long.parseLong(params.get("tableConfigId").toString()) : null;

        Page<TableColumnBinding> page = new Page<>(current, size);

        LambdaQueryWrapper<TableColumnBinding> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(tableConfigId != null, TableColumnBinding::getTableConfigId, tableConfigId)
               .orderByAsc(TableColumnBinding::getSortOrder);

        Page<TableColumnBinding> result = page(page, wrapper);

        // 加载关联信息
        result.getRecords().forEach(this::loadRelatedInfo);

        return PageResult.of(result);
    }

    @Override
    public List<TableColumnBinding> getBindingsByTableConfigId(Long tableConfigId) {
        return list(
                Wrappers.lambdaQuery(TableColumnBinding.class)
                        .eq(TableColumnBinding::getTableConfigId, tableConfigId)
                        .orderByAsc(TableColumnBinding::getSortOrder)
        );
    }

    @Override
    public List<TableColumnBinding> getBindingsWithFieldsByTableConfigId(Long tableConfigId) {
        List<TableColumnBinding> bindings = getBindingsByTableConfigId(tableConfigId);
        bindings.forEach(this::loadRelatedInfo);
        return bindings;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTableColumnBinding(TableColumnBinding binding) {
        // 验证表格配置是否存在
        LowTableConfig tableConfig = lowTableConfigMapper.selectById(binding.getTableConfigId());
        if (tableConfig == null) {
            throw new BusinessException("表格配置不存在");
        }

        // 验证字段是否存在
        DbTableField field = dbTableFieldMapper.selectById(binding.getFieldId());
        if (field == null) {
            throw new BusinessException("字段不存在");
        }

        // 检查是否已绑定
        TableColumnBinding existBinding = getBindingByTableField(binding.getTableConfigId(), binding.getFieldId());
        if (existBinding != null) {
            throw new BusinessException("该字段已绑定到表格");
        }

        // 设置默认值
        if (binding.getSortable() == null) {
            binding.setSortable(false);
        }
        if (binding.getShowOverflow() == null) {
            binding.setShowOverflow(true);
        }
        if (binding.getVisible() == null) {
            binding.setVisible(true);
        }
        if (binding.getAlign() == null) {
            binding.setAlign("center");
        }
        if (binding.getColumnType() == null) {
            binding.setColumnType("text");
        }
        if (binding.getSortOrder() == null) {
            binding.setSortOrder(0);
        }

        save(binding);

        log.info("创建表格列绑定成功, id: {}, tableConfigId: {}, fieldId: {}",
                binding.getId(), binding.getTableConfigId(), binding.getFieldId());
        return binding.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveBindings(Long tableConfigId, List<TableColumnBinding> bindings) {
        // 验证表格配置是否存在
        LowTableConfig tableConfig = lowTableConfigMapper.selectById(tableConfigId);
        if (tableConfig == null) {
            throw new BusinessException("表格配置不存在");
        }

        // 删除原有绑定
        deleteBindingsByTableConfigId(tableConfigId);

        // 保存新绑定
        if (CollUtil.isNotEmpty(bindings)) {
            for (int i = 0; i < bindings.size(); i++) {
                TableColumnBinding binding = bindings.get(i);
                binding.setTableConfigId(tableConfigId);
                binding.setSortOrder(i);

                // 设置默认值
                if (binding.getSortable() == null) {
                    binding.setSortable(false);
                }
                if (binding.getShowOverflow() == null) {
                    binding.setShowOverflow(true);
                }
                if (binding.getVisible() == null) {
                    binding.setVisible(true);
                }
                if (binding.getAlign() == null) {
                    binding.setAlign("center");
                }
                if (binding.getColumnType() == null) {
                    binding.setColumnType("text");
                }

                save(binding);
            }
        }

        log.info("批量保存表格列绑定成功, tableConfigId: {}, count: {}", tableConfigId, bindings.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTableColumnBinding(Long id, TableColumnBinding binding) {
        TableColumnBinding existBinding = getById(id);
        if (existBinding == null) {
            throw new BusinessException("列绑定不存在");
        }

        binding.setId(id);
        updateById(binding);

        log.info("更新表格列绑定成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTableColumnBinding(Long id) {
        removeById(id);
        log.info("删除表格列绑定成功, id: {}", id);
    }

    @Override
    public List<DbTableField> getAvailableFieldsByTableId(Long tableId) {
        return dbTableFieldMapper.selectList(
                Wrappers.lambdaQuery(DbTableField.class)
                        .eq(DbTableField::getTableId, tableId)
                        .orderByAsc(DbTableField::getOrdinalPosition)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBindingsByTableConfigId(Long tableConfigId) {
        remove(
                Wrappers.lambdaQuery(TableColumnBinding.class)
                        .eq(TableColumnBinding::getTableConfigId, tableConfigId)
        );
        log.info("删除表格配置的所有列绑定成功, tableConfigId: {}", tableConfigId);
    }

    @Override
    public TableColumnBinding getBindingByTableField(Long tableConfigId, Long fieldId) {
        return getOne(
                Wrappers.lambdaQuery(TableColumnBinding.class)
                        .eq(TableColumnBinding::getTableConfigId, tableConfigId)
                        .eq(TableColumnBinding::getFieldId, fieldId)
        );
    }

    /**
     * 加载关联信息
     */
    private void loadRelatedInfo(TableColumnBinding binding) {
        // 加载字段信息
        if (binding.getFieldId() != null) {
            DbTableField field = dbTableFieldMapper.selectById(binding.getFieldId());
            binding.setField(field);
        }
    }
}
