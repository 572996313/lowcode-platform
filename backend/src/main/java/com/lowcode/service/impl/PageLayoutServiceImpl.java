package com.lowcode.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.PageLayout;
import com.lowcode.mapper.PageLayoutMapper;
import com.lowcode.service.IPageLayoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 页面布局配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PageLayoutServiceImpl extends ServiceImpl<PageLayoutMapper, PageLayout> implements IPageLayoutService {

    @Override
    public PageResult<PageLayout> getPageLayoutList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        String layoutName = params.get("layoutName") != null ? params.get("layoutName").toString() : null;
        String layoutCode = params.get("layoutCode") != null ? params.get("layoutCode").toString() : null;
        String layoutType = params.get("layoutType") != null ? params.get("layoutType").toString() : null;
        Boolean published = params.get("published") != null ? Boolean.parseBoolean(params.get("published").toString()) : null;

        Page<PageLayout> page = new Page<>(current, size);

        LambdaQueryWrapper<PageLayout> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(layoutName), PageLayout::getLayoutName, layoutName)
               .like(StrUtil.isNotBlank(layoutCode), PageLayout::getLayoutCode, layoutCode)
               .eq(StrUtil.isNotBlank(layoutType), PageLayout::getLayoutType, layoutType)
               .eq(published != null, PageLayout::getPublished, published)
               .orderByDesc(PageLayout::getUpdateTime);

        Page<PageLayout> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public PageLayout getPageLayout(Long id) {
        PageLayout pageLayout = getById(id);
        if (pageLayout == null) {
            throw new BusinessException("页面布局不存在");
        }
        return pageLayout;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPageLayout(PageLayout pageLayout) {
        // 检查编码是否重复
        if (StrUtil.isNotBlank(pageLayout.getLayoutCode())) {
            long count = lambdaQuery()
                    .eq(PageLayout::getLayoutCode, pageLayout.getLayoutCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("布局编码已存在");
            }
        }

        // 设置默认值
        if (pageLayout.getStatus() == null) {
            pageLayout.setStatus(true);
        }
        if (pageLayout.getPublished() == null) {
            pageLayout.setPublished(false);
        }

        save(pageLayout);

        log.info("创建页面布局成功, id: {}, code: {}", pageLayout.getId(), pageLayout.getLayoutCode());
        return pageLayout.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePageLayout(Long id, PageLayout pageLayout) {
        PageLayout existLayout = getById(id);
        if (existLayout == null) {
            throw new BusinessException("页面布局不存在");
        }

        // 检查编码是否重复
        if (StrUtil.isNotBlank(pageLayout.getLayoutCode()) && !pageLayout.getLayoutCode().equals(existLayout.getLayoutCode())) {
            long count = lambdaQuery()
                    .eq(PageLayout::getLayoutCode, pageLayout.getLayoutCode())
                    .ne(PageLayout::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("布局编码已存在");
            }
        }

        pageLayout.setId(id);
        updateById(pageLayout);

        log.info("更新页面布局成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePageLayout(Long id) {
        PageLayout pageLayout = getById(id);
        if (pageLayout == null) {
            throw new BusinessException("页面布局不存在");
        }

        // 检查是否已发布
        if (Boolean.TRUE.equals(pageLayout.getPublished())) {
            throw new BusinessException("已发布的布局不能删除，请先取消发布");
        }

        removeById(id);
        log.info("删除页面布局成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishPageLayout(Long id) {
        PageLayout pageLayout = getById(id);
        if (pageLayout == null) {
            throw new BusinessException("页面布局不存在");
        }

        pageLayout.setPublished(true);
        updateById(pageLayout);

        log.info("发布页面布局成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublishPageLayout(Long id) {
        PageLayout pageLayout = getById(id);
        if (pageLayout == null) {
            throw new BusinessException("页面布局不存在");
        }

        pageLayout.setPublished(false);
        updateById(pageLayout);

        log.info("取消发布页面布局成功, id: {}", id);
    }

    @Override
    public PageLayout getByLayoutCode(String layoutCode) {
        return getOne(
                Wrappers.lambdaQuery(PageLayout.class)
                        .eq(PageLayout::getLayoutCode, layoutCode)
        );
    }
}
