package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.DbTableField;
import com.lowcode.entity.FormFieldBinding;
import com.lowcode.entity.LowFormConfig;
import com.lowcode.mapper.DbTableFieldMapper;
import com.lowcode.mapper.FormFieldBindingMapper;
import com.lowcode.mapper.LowFormConfigMapper;
import com.lowcode.service.IFormFieldBindingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 表单字段绑定服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormFieldBindingServiceImpl extends ServiceImpl<FormFieldBindingMapper, FormFieldBinding> implements IFormFieldBindingService {

    private final DbTableFieldMapper dbTableFieldMapper;
    private final LowFormConfigMapper lowFormConfigMapper;

    @Override
    public PageResult<FormFieldBinding> getFormFieldBindingList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        Long formId = params.get("formId") != null ? Long.parseLong(params.get("formId").toString()) : null;

        Page<FormFieldBinding> page = new Page<>(current, size);

        LambdaQueryWrapper<FormFieldBinding> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(formId != null, FormFieldBinding::getFormId, formId)
               .orderByAsc(FormFieldBinding::getSortOrder);

        Page<FormFieldBinding> result = page(page, wrapper);

        // 加载关联信息
        result.getRecords().forEach(this::loadRelatedInfo);

        return PageResult.of(result);
    }

    @Override
    public List<FormFieldBinding> getBindingsByFormId(Long formId) {
        return list(
                Wrappers.lambdaQuery(FormFieldBinding.class)
                        .eq(FormFieldBinding::getFormId, formId)
                        .orderByAsc(FormFieldBinding::getSortOrder)
        );
    }

    @Override
    public List<FormFieldBinding> getBindingsWithFieldsByFormId(Long formId) {
        List<FormFieldBinding> bindings = getBindingsByFormId(formId);
        bindings.forEach(this::loadRelatedInfo);
        return bindings;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFormFieldBinding(FormFieldBinding binding) {
        // 验证表单是否存在
        LowFormConfig formConfig = lowFormConfigMapper.selectById(binding.getFormId());
        if (formConfig == null) {
            throw new BusinessException("表单配置不存在");
        }

        // 验证字段是否存在
        DbTableField field = dbTableFieldMapper.selectById(binding.getFieldId());
        if (field == null) {
            throw new BusinessException("字段不存在");
        }

        // 检查是否已绑定
        FormFieldBinding existBinding = getBindingByFormField(binding.getFormId(), binding.getFieldId());
        if (existBinding != null) {
            throw new BusinessException("该字段已绑定到表单");
        }

        // 设置默认值
        if (binding.getSpan() == null) {
            binding.setSpan(12);
        }
        if (binding.getRequired() == null) {
            binding.setRequired(false);
        }
        if (binding.getVisible() == null) {
            binding.setVisible(true);
        }
        if (binding.getReadonly() == null) {
            binding.setReadonly(false);
        }
        if (binding.getDisabled() == null) {
            binding.setDisabled(false);
        }
        if (binding.getSortOrder() == null) {
            binding.setSortOrder(0);
        }

        save(binding);

        log.info("创建表单字段绑定成功, id: {}, formId: {}, fieldId: {}",
                binding.getId(), binding.getFormId(), binding.getFieldId());
        return binding.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveBindings(Long formId, List<FormFieldBinding> bindings) {
        // 验证表单是否存在
        LowFormConfig formConfig = lowFormConfigMapper.selectById(formId);
        if (formConfig == null) {
            throw new BusinessException("表单配置不存在");
        }

        // 删除原有绑定
        deleteBindingsByFormId(formId);

        // 保存新绑定
        if (CollUtil.isNotEmpty(bindings)) {
            for (int i = 0; i < bindings.size(); i++) {
                FormFieldBinding binding = bindings.get(i);
                binding.setFormId(formId);
                binding.setSortOrder(i);

                // 设置默认值
                if (binding.getSpan() == null) {
                    binding.setSpan(12);
                }
                if (binding.getRequired() == null) {
                    binding.setRequired(false);
                }
                if (binding.getVisible() == null) {
                    binding.setVisible(true);
                }

                save(binding);
            }
        }

        log.info("批量保存表单字段绑定成功, formId: {}, count: {}", formId, bindings.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFormFieldBinding(Long id, FormFieldBinding binding) {
        FormFieldBinding existBinding = getById(id);
        if (existBinding == null) {
            throw new BusinessException("字段绑定不存在");
        }

        binding.setId(id);
        updateById(binding);

        log.info("更新表单字段绑定成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormFieldBinding(Long id) {
        removeById(id);
        log.info("删除表单字段绑定成功, id: {}", id);
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
    public void deleteBindingsByFormId(Long formId) {
        remove(
                Wrappers.lambdaQuery(FormFieldBinding.class)
                        .eq(FormFieldBinding::getFormId, formId)
        );
        log.info("删除表单的所有字段绑定成功, formId: {}", formId);
    }

    @Override
    public FormFieldBinding getBindingByFormField(Long formId, Long fieldId) {
        return getOne(
                Wrappers.lambdaQuery(FormFieldBinding.class)
                        .eq(FormFieldBinding::getFormId, formId)
                        .eq(FormFieldBinding::getFieldId, fieldId)
        );
    }

    /**
     * 加载关联信息
     */
    private void loadRelatedInfo(FormFieldBinding binding) {
        // 加载字段信息
        if (binding.getFieldId() != null) {
            DbTableField field = dbTableFieldMapper.selectById(binding.getFieldId());
            binding.setField(field);
        }
    }
}
