package com.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.entity.TableStyleTemplate;
import com.lowcode.mapper.TableStyleTemplateMapper;
import com.lowcode.service.ITableStyleTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 表格样式模板服务实现
 */
@Service
public class TableStyleTemplateServiceImpl extends ServiceImpl<TableStyleTemplateMapper, TableStyleTemplate>
        implements ITableStyleTemplateService {

    @Override
    public PageResult<TableStyleTemplate> getPage(Map<String, Object> params) {
        Integer current = (Integer) params.getOrDefault("current", 1);
        Integer size = (Integer) params.getOrDefault("size", 10);
        String templateName = (String) params.get("templateName");
        String templateCode = (String) params.get("templateCode");
        Boolean status = params.get("status") != null ? Boolean.valueOf(params.get("status").toString()) : null;

        LambdaQueryWrapper<TableStyleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, TableStyleTemplate::getStatus, status)
               .like(StringUtils.hasText(templateName), TableStyleTemplate::getTemplateName, templateName)
               .like(StringUtils.hasText(templateCode), TableStyleTemplate::getTemplateCode, templateCode)
               .orderByAsc(TableStyleTemplate::getSortOrder)
               .orderByDesc(TableStyleTemplate::getCreateTime);

        Page<TableStyleTemplate> page = page(new Page<>(current, size), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<TableStyleTemplate> getAllEnabled() {
        LambdaQueryWrapper<TableStyleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TableStyleTemplate::getStatus, true)
               .orderByAsc(TableStyleTemplate::getSortOrder);
        return list(wrapper);
    }

    @Override
    public TableStyleTemplate getByCode(String code) {
        LambdaQueryWrapper<TableStyleTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TableStyleTemplate::getTemplateCode, code);
        return getOne(wrapper);
    }
}
