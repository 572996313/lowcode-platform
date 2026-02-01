package com.lowcode.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.DbFieldWidgetConfig;
import com.lowcode.entity.DbFieldWidgetTemplate;
import com.lowcode.entity.DbTableField;
import com.lowcode.mapper.DbFieldWidgetConfigMapper;
import com.lowcode.mapper.DbFieldWidgetTemplateMapper;
import com.lowcode.mapper.DbTableFieldMapper;
import com.lowcode.service.IDbFieldWidgetTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字段控件配置模板服务实现
 *
 * @author lowcode-platform
 * @since 2025-02-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DbFieldWidgetTemplateServiceImpl extends ServiceImpl<DbFieldWidgetTemplateMapper, DbFieldWidgetTemplate>
        implements IDbFieldWidgetTemplateService {

    private final DbFieldWidgetConfigMapper widgetConfigMapper;
    private final DbTableFieldMapper fieldMapper;

    @Override
    public List<DbFieldWidgetTemplate> getTemplatesByFieldId(Long fieldId) {
        if (fieldId == null) {
            throw new BusinessException("字段ID不能为空");
        }
        return baseMapper.selectEnabledTemplatesByFieldId(fieldId);
    }

    @Override
    public List<DbFieldWidgetTemplate> getTemplatesByTableId(Long tableId) {
        if (tableId == null) {
            throw new BusinessException("表ID不能为空");
        }
        return baseMapper.selectTemplatesByTableId(tableId);
    }

    @Override
    public DbFieldWidgetTemplate getTemplateByCode(Long fieldId, String templateCode) {
        if (fieldId == null) {
            throw new BusinessException("字段ID不能为空");
        }
        if (StrUtil.isBlank(templateCode)) {
            throw new BusinessException("模板编码不能为空");
        }
        return baseMapper.selectByFieldIdAndCode(fieldId, templateCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DbFieldWidgetTemplate createTemplate(DbFieldWidgetTemplate template) {
        validateTemplate(template, false);

        // 验证字段是否存在
        DbTableField field = fieldMapper.selectById(template.getFieldId());
        if (field == null) {
            throw new BusinessException("字段不存在");
        }

        // 设置默认值
        if (template.getEnabled() == null) {
            template.setEnabled(1);
        }
        if (template.getIsSystem() == null) {
            template.setIsSystem(0);
        }
        if (template.getSortOrder() == null) {
            template.setSortOrder(0);
        }

        // 如果有模板编码，检查唯一性
        if (StrUtil.isNotBlank(template.getTemplateCode())) {
            DbFieldWidgetTemplate existing = baseMapper.selectByFieldIdAndCode(
                    template.getFieldId(),
                    template.getTemplateCode()
            );
            if (existing != null) {
                throw new BusinessException("模板编码已存在: " + template.getTemplateCode());
            }
        }

        save(template);
        log.info("创建字段控件模板成功: fieldId={}, templateName={}",
                template.getFieldId(), template.getTemplateName());
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DbFieldWidgetTemplate updateTemplate(Long id, DbFieldWidgetTemplate template) {
        DbFieldWidgetTemplate existing = getById(id);
        if (existing == null) {
            throw new BusinessException("模板不存在");
        }

        validateTemplate(template, true);

        // 系统预设模板不允许修改关键字段
        if (existing.getIsSystem() != null && existing.getIsSystem() == 1) {
            template.setFieldId(existing.getFieldId());
            template.setIsSystem(1);
        }

        // 如果修改了模板编码，检查唯一性
        if (StrUtil.isNotBlank(template.getTemplateCode()) &&
            !template.getTemplateCode().equals(existing.getTemplateCode())) {
            DbFieldWidgetTemplate duplicate = baseMapper.selectByFieldIdAndCode(
                    existing.getFieldId(),
                    template.getTemplateCode()
            );
            if (duplicate != null && !duplicate.getId().equals(id)) {
                throw new BusinessException("模板编码已存在: " + template.getTemplateCode());
            }
        }

        template.setId(id);
        template.setFieldId(existing.getFieldId());
        template.setTableId(existing.getTableId());
        updateById(template);
        log.info("更新字段控件模板成功: id={}, templateName={}", id, template.getTemplateName());
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTemplate(Long id) {
        DbFieldWidgetTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        // 系统预设模板不允许删除
        if (template.getIsSystem() != null && template.getIsSystem() == 1) {
            throw new BusinessException("系统预设模板不允许删除");
        }

        boolean result = removeById(id);
        if (result) {
            log.info("删除字段控件模板成功: id={}, templateName={}", id, template.getTemplateName());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyTemplateAsPrimary(Long templateId) {
        DbFieldWidgetTemplate template = getById(templateId);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        // 获取或创建字段的主配置
        DbFieldWidgetConfig config = widgetConfigMapper.selectOne(
                Wrappers.lambdaQuery(DbFieldWidgetConfig.class)
                        .eq(DbFieldWidgetConfig::getFieldId, template.getFieldId())
                        .eq(DbFieldWidgetConfig::getIsDefault, 0)
                        .last("LIMIT 1")
        );

        if (config == null) {
            // 创建新配置
            config = DbFieldWidgetConfig.builder()
                    .tableId(template.getTableId())
                    .fieldId(template.getFieldId())
                    .widgetType(template.getWidgetType())
                    .widgetConfig(template.getWidgetConfig())
                    .isDefault(0)
                    .enabled(1)
                    .sortOrder(0)
                    .build();
            widgetConfigMapper.insert(config);
            log.info("从模板创建字段配置: fieldId={}, templateId={}", template.getFieldId(), templateId);
        } else {
            // 更新现有配置
            config.setWidgetType(template.getWidgetType());
            config.setWidgetConfig(template.getWidgetConfig());
            widgetConfigMapper.updateById(config);
            log.info("从模板更新字段配置: configId={}, templateId={}", config.getId(), templateId);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleEnabled(Long id, Integer enabled) {
        DbFieldWidgetTemplate template = getById(id);
        if (template == null) {
            throw new BusinessException("模板不存在");
        }

        template.setEnabled(enabled != null ? enabled : 0);
        boolean result = updateById(template);
        if (result) {
            log.info("切换模板启用状态: id={}, enabled={}", id, enabled);
        }
        return result;
    }

    /**
     * 验证模板数据
     */
    private void validateTemplate(DbFieldWidgetTemplate template, boolean isUpdate) {
        if (!isUpdate) {
            // 创建时必填字段
            if (template.getFieldId() == null) {
                throw new BusinessException("字段ID不能为空");
            }
            if (template.getTableId() == null) {
                throw new BusinessException("表ID不能为空");
            }
        }

        if (StrUtil.isBlank(template.getTemplateName())) {
            throw new BusinessException("模板名称不能为空");
        }

        if (StrUtil.isBlank(template.getWidgetType())) {
            throw new BusinessException("控件类型不能为空");
        }

        // 支持的控件类型列表
        List<String> supportedTypes = List.of(
                "input", "textarea", "select", "radio", "checkbox",
                "switch", "date", "datetime", "time", "number",
                "upload", "cascader", "slider", "rate"
        );

        if (!supportedTypes.contains(template.getWidgetType())) {
            throw new BusinessException("不支持的控件类型: " + template.getWidgetType());
        }

        // 验证 JSON 格式
        if (StrUtil.isNotBlank(template.getWidgetConfig())) {
            try {
                JSONUtil.parseObj(template.getWidgetConfig());
            } catch (Exception e) {
                throw new BusinessException("控件配置 JSON 格式错误: " + e.getMessage());
            }
        }
    }
}
