package com.lowcode.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.entity.FormStyleTemplate;
import com.lowcode.mapper.FormStyleTemplateMapper;
import com.lowcode.service.IFormStyleTemplateService;
import com.lowcode.service.ISysDictReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表单样式模板服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormStyleTemplateServiceImpl extends ServiceImpl<FormStyleTemplateMapper, FormStyleTemplate>
        implements IFormStyleTemplateService {

    private final ISysDictReferenceService dictReferenceService;

    @Override
    public PageResult<FormStyleTemplate> getPage(Map<String, Object> params) {
        Integer current = Integer.valueOf(params.getOrDefault("current", "1").toString());
        Integer size = Integer.valueOf(params.getOrDefault("size", "10").toString());
        String templateName = (String) params.get("templateName");
        String templateCode = (String) params.get("templateCode");
        Boolean status = params.get("status") != null ? Boolean.valueOf(params.get("status").toString()) : null;

        LambdaQueryWrapper<FormStyleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, FormStyleTemplate::getStatus, status)
               .like(StringUtils.hasText(templateName), FormStyleTemplate::getTemplateName, templateName)
               .like(StringUtils.hasText(templateCode), FormStyleTemplate::getTemplateCode, templateCode)
               .orderByAsc(FormStyleTemplate::getSortOrder)
               .orderByDesc(FormStyleTemplate::getCreateTime);

        Page<FormStyleTemplate> page = page(new Page<>(current, size), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<FormStyleTemplate> getAllEnabled() {
        LambdaQueryWrapper<FormStyleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FormStyleTemplate::getStatus, true)
               .orderByAsc(FormStyleTemplate::getSortOrder);
        return list(wrapper);
    }

    @Override
    public FormStyleTemplate getByCode(String code) {
        LambdaQueryWrapper<FormStyleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FormStyleTemplate::getTemplateCode, code);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(FormStyleTemplate entity) {
        boolean result = super.save(entity);
        if (result) {
            updateDictReferences(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(FormStyleTemplate entity) {
        boolean result = super.updateById(entity);
        if (result) {
            updateDictReferences(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        // 删除关联的字典引用
        dictReferenceService.removeByResource(
                ISysDictReferenceService.RESOURCE_TYPE_FORM_TEMPLATE, (Long) id);
        return super.removeById(id);
    }

    /**
     * 更新模板相关的字典引用
     */
    private void updateDictReferences(FormStyleTemplate template) {
        // 删除旧的引用
        dictReferenceService.removeByResource(
                ISysDictReferenceService.RESOURCE_TYPE_FORM_TEMPLATE, template.getId());

        // 解析 styleConfig 中的字典引用
        if (StrUtil.isNotBlank(template.getStyleConfig())) {
            try {
                List<String> dictCodes = extractDictCodesFromConfig(template.getStyleConfig());
                for (String dictCode : dictCodes) {
                    dictReferenceService.addReference(
                            dictCode,
                            ISysDictReferenceService.RESOURCE_TYPE_FORM_TEMPLATE,
                            template.getId(),
                            template.getTemplateName(),
                            null,
                            null,
                            null,
                            null,
                            "styleConfig"
                    );
                }
                log.debug("更新表单模板字典引用: template={}, dicts={}", template.getTemplateName(), dictCodes);
            } catch (Exception e) {
                log.warn("解析模板字典引用失败: template={}", template.getTemplateName(), e);
            }
        }
    }

    /**
     * 从配置 JSON 中提取字典编码
     */
    private List<String> extractDictCodesFromConfig(String config) {
        List<String> dictCodes = new ArrayList<>();
        if (StrUtil.isBlank(config)) {
            return dictCodes;
        }

        try {
            JSONObject json = JSONUtil.parseObj(config);
            extractDictCodesFromJson(json, dictCodes);
        } catch (Exception e) {
            // 忽略解析错误
        }

        return dictCodes;
    }

    /**
     * 递归提取 JSON 中的字典编码
     */
    private void extractDictCodesFromJson(JSONObject json, List<String> dictCodes) {
        for (String key : json.keySet()) {
            Object value = json.get(key);
            if (value instanceof JSONObject) {
                extractDictCodesFromJson((JSONObject) value, dictCodes);
            } else if (value instanceof String) {
                String strValue = (String) value;
                // 检查是否是字典相关字段
                if ("dictCode".equals(key) || "dict_category_code".equals(key) ||
                        "categoryCode".equals(key) || "dictionary".equals(key)) {
                    if (StrUtil.isNotBlank(strValue)) {
                        dictCodes.add(strValue);
                    }
                }
            }
        }
    }
}
