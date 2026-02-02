package com.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.entity.FormStyleTemplate;
import com.lowcode.mapper.FormStyleTemplateMapper;
import com.lowcode.service.IFormStyleTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 表单样式模板服务实现
 */
@Service
public class FormStyleTemplateServiceImpl extends ServiceImpl<FormStyleTemplateMapper, FormStyleTemplate>
        implements IFormStyleTemplateService {

    @Override
    public PageResult<FormStyleTemplate> getPage(Map<String, Object> params) {
        Integer current = (Integer) params.getOrDefault("current", 1);
        Integer size = (Integer) params.getOrDefault("size", 10);
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
}
