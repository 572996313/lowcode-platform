package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.LowFormConfig;
import com.lowcode.entity.LowFormField;
import com.lowcode.mapper.LowFormConfigMapper;
import com.lowcode.mapper.LowFormFieldMapper;
import com.lowcode.service.ILowFormConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 表单配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LowFormConfigServiceImpl extends ServiceImpl<LowFormConfigMapper, LowFormConfig> implements ILowFormConfigService {

    private final LowFormFieldMapper formFieldMapper;

    @Override
    public PageResult<LowFormConfig> getFormList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        String formName = params.get("formName") != null ? params.get("formName").toString() : null;
        String formType = params.get("formType") != null ? params.get("formType").toString() : null;

        Page<LowFormConfig> page = new Page<>(current, size);

        LambdaQueryWrapper<LowFormConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(formName), LowFormConfig::getFormName, formName)
               .eq(StrUtil.isNotBlank(formType), LowFormConfig::getFormType, formType)
               .orderByDesc(LowFormConfig::getUpdateTime);

        Page<LowFormConfig> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public LowFormConfig getFormConfig(Long id) {
        LowFormConfig formConfig = getById(id);
        if (formConfig == null) {
            throw new BusinessException("表单配置不存在");
        }

        // 查询表单字段
        List<LowFormField> fields = formFieldMapper.selectList(
                Wrappers.lambdaQuery(LowFormField.class)
                        .eq(LowFormField::getFormId, id)
                        .orderByAsc(LowFormField::getSortOrder)
        );
        formConfig.setFields(fields);

        return formConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFormConfig(LowFormConfig formConfig) {
        // 检查编码是否重复
        if (StrUtil.isNotBlank(formConfig.getFormCode())) {
            long count = lambdaQuery()
                    .eq(LowFormConfig::getFormCode, formConfig.getFormCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("表单编码已存在");
            }
        }

        // 设置默认值
        if (formConfig.getStatus() == null) {
            formConfig.setStatus(true);
        }
        if (formConfig.getLayoutCols() == null) {
            formConfig.setLayoutCols(2);
        }
        if (formConfig.getLabelWidth() == null) {
            formConfig.setLabelWidth(100);
        }

        save(formConfig);

        // 保存表单字段
        if (CollUtil.isNotEmpty(formConfig.getFields())) {
            saveFormFields(formConfig.getId(), formConfig.getFields());
        }

        log.info("创建表单配置成功, id: {}, code: {}", formConfig.getId(), formConfig.getFormCode());
        return formConfig.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFormConfig(Long id, LowFormConfig formConfig) {
        LowFormConfig existConfig = getById(id);
        if (existConfig == null) {
            throw new BusinessException("表单配置不存在");
        }

        // 检查编码是否重复
        if (StrUtil.isNotBlank(formConfig.getFormCode()) && !formConfig.getFormCode().equals(existConfig.getFormCode())) {
            long count = lambdaQuery()
                    .eq(LowFormConfig::getFormCode, formConfig.getFormCode())
                    .ne(LowFormConfig::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("表单编码已存在");
            }
        }

        formConfig.setId(id);
        updateById(formConfig);

        // 更新表单字段
        if (formConfig.getFields() != null) {
            // 删除原有字段
            formFieldMapper.delete(
                    Wrappers.lambdaQuery(LowFormField.class)
                            .eq(LowFormField::getFormId, id)
            );
            // 保存新字段
            if (CollUtil.isNotEmpty(formConfig.getFields())) {
                saveFormFields(id, formConfig.getFields());
            }
        }

        log.info("更新表单配置成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormConfig(Long id) {
        // 删除表单字段
        formFieldMapper.delete(
                Wrappers.lambdaQuery(LowFormField.class)
                        .eq(LowFormField::getFormId, id)
        );
        // 删除表单配置
        removeById(id);
        log.info("删除表单配置成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFormFields(Long formId, List<LowFormField> fields) {
        for (int i = 0; i < fields.size(); i++) {
            LowFormField field = fields.get(i);
            field.setFormId(formId);
            field.setSortOrder(i);
            formFieldMapper.insert(field);
        }
    }
}
