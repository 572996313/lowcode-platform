package com.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.entity.LowPageTemplate;
import com.lowcode.mapper.LowPageTemplateMapper;
import com.lowcode.service.IPageTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 页面模板服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PageTemplateServiceImpl extends ServiceImpl<LowPageTemplateMapper, LowPageTemplate>
        implements IPageTemplateService {

    @Override
    public List<LowPageTemplate> getList() {
        LambdaQueryWrapper<LowPageTemplate> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LowPageTemplate::getStatus, 1)
                .orderByAsc(LowPageTemplate::getId);
        return list(wrapper);
    }

    @Override
    public Page<LowPageTemplate> getPage(Long current, Long size, String keyword, String templateType, String layoutType) {
        LambdaQueryWrapper<LowPageTemplate> wrapper = Wrappers.lambdaQuery();

        // 关键字搜索
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(LowPageTemplate::getTemplateName, keyword)
                    .or()
                    .like(LowPageTemplate::getTemplateCode, keyword)
                    .or()
                    .like(LowPageTemplate::getDescription, keyword));
        }

        // 模板类型筛选
        if (StringUtils.isNotBlank(templateType)) {
            wrapper.eq(LowPageTemplate::getTemplateType, templateType);
        }

        // 布局类型筛选
        if (StringUtils.isNotBlank(layoutType)) {
            wrapper.eq(LowPageTemplate::getLayoutType, layoutType);
        }

        wrapper.orderByAsc(LowPageTemplate::getId);

        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<LowPageTemplate> getAll() {
        LambdaQueryWrapper<LowPageTemplate> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LowPageTemplate::getStatus, 1)
                .orderByAsc(LowPageTemplate::getId);
        return list(wrapper);
    }

    @Override
    public LowPageTemplate getByCode(String templateCode) {
        return lambdaQuery()
                .eq(LowPageTemplate::getTemplateCode, templateCode)
                .eq(LowPageTemplate::getStatus, 1)
                .one();
    }
}
