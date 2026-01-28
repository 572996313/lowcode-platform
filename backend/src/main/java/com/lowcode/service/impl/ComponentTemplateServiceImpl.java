package com.lowcode.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.entity.LowComponentTemplate;
import com.lowcode.mapper.LowComponentTemplateMapper;
import com.lowcode.service.IComponentTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 组件模板服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ComponentTemplateServiceImpl extends ServiceImpl<LowComponentTemplateMapper, LowComponentTemplate>
        implements IComponentTemplateService {

    @Override
    public PageResult<LowComponentTemplate> getPageList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        String keyword = params.get("keyword") != null ? params.get("keyword").toString() : null;
        String category = params.get("category") != null ? params.get("category").toString() : null;
        String componentType = params.get("componentType") != null ? params.get("componentType").toString() : null;

        Page<LowComponentTemplate> page = new Page<>(current, size);

        LambdaQueryWrapper<LowComponentTemplate> wrapper = Wrappers.lambdaQuery();
        wrapper.and(StrUtil.isNotBlank(keyword), w ->
                        w.like(LowComponentTemplate::getTemplateName, keyword)
                                .or().like(LowComponentTemplate::getDescription, keyword)
                                .or().like(LowComponentTemplate::getKeywords, keyword)
                )
                .eq(StrUtil.isNotBlank(category), LowComponentTemplate::getCategory, category)
                .eq(StrUtil.isNotBlank(componentType), LowComponentTemplate::getComponentType, componentType)
                .eq(LowComponentTemplate::getStatus, 1)
                .orderByAsc(LowComponentTemplate::getSortOrder)
                .orderByDesc(LowComponentTemplate::getUpdateTime);

        Page<LowComponentTemplate> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public List<LowComponentTemplate> getList(Map<String, Object> params) {
        String category = params.get("category") != null ? params.get("category").toString() : null;
        String componentType = params.get("componentType") != null ? params.get("componentType").toString() : null;

        LambdaQueryWrapper<LowComponentTemplate> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StrUtil.isNotBlank(category), LowComponentTemplate::getCategory, category)
                .eq(StrUtil.isNotBlank(componentType), LowComponentTemplate::getComponentType, componentType)
                .eq(LowComponentTemplate::getStatus, 1)
                .orderByAsc(LowComponentTemplate::getSortOrder);

        return list(wrapper);
    }

    @Override
    public List<LowComponentTemplate> getByCategory(String category) {
        LambdaQueryWrapper<LowComponentTemplate> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LowComponentTemplate::getCategory, category)
                .eq(LowComponentTemplate::getStatus, 1)
                .orderByAsc(LowComponentTemplate::getSortOrder);
        return list(wrapper);
    }

    @Override
    public LowComponentTemplate getByCode(String templateCode) {
        return lambdaQuery()
                .eq(LowComponentTemplate::getTemplateCode, templateCode)
                .eq(LowComponentTemplate::getStatus, 1)
                .one();
    }
}
