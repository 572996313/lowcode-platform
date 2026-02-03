package com.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.entity.LowFormTemplate;
import com.lowcode.mapper.LowFormTemplateMapper;
import com.lowcode.service.IFormTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 表单模板服务实现
 */
@Service
public class FormTemplateServiceImpl extends ServiceImpl<LowFormTemplateMapper, LowFormTemplate>
        implements IFormTemplateService {

    @Override
    public PageResult<LowFormTemplate> getPage(Map<String, Object> params) {
        Integer current = Integer.valueOf(params.getOrDefault("current", "1").toString());
        Integer size = Integer.valueOf(params.getOrDefault("size", "10").toString());
        String templateName = (String) params.get("templateName");
        Boolean status = params.get("status") != null ? Boolean.valueOf(params.get("status").toString()) : null;

        LambdaQueryWrapper<LowFormTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, LowFormTemplate::getStatus, status)
               .like(StringUtils.hasText(templateName), LowFormTemplate::getTemplateName, templateName)
               .orderByAsc(LowFormTemplate::getSortOrder)
               .orderByDesc(LowFormTemplate::getCreateTime);

        Page<LowFormTemplate> page = page(new Page<>(current, size), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<LowFormTemplate> getAll() {
        LambdaQueryWrapper<LowFormTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LowFormTemplate::getStatus, true)
               .orderByAsc(LowFormTemplate::getSortOrder);
        return list(wrapper);
    }

    @Override
    public LowFormTemplate getByCode(String templateCode) {
        LambdaQueryWrapper<LowFormTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LowFormTemplate::getTemplateCode, templateCode);
        return getOne(wrapper);
    }
}