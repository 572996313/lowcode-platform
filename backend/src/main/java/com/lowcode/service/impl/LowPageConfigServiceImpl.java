package com.lowcode.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.LowPageConfig;
import com.lowcode.mapper.LowPageConfigMapper;
import com.lowcode.service.ILowPageConfigService;
import com.lowcode.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 页面配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LowPageConfigServiceImpl extends ServiceImpl<LowPageConfigMapper, LowPageConfig> implements ILowPageConfigService {

    private final ISysMenuService menuService;

    @Override
    public PageResult<LowPageConfig> getPageList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        String pageName = params.get("pageName") != null ? params.get("pageName").toString() : null;
        String pageType = params.get("pageType") != null ? params.get("pageType").toString() : null;

        Page<LowPageConfig> page = new Page<>(current, size);

        LambdaQueryWrapper<LowPageConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(pageName), LowPageConfig::getPageName, pageName)
               .eq(StrUtil.isNotBlank(pageType), LowPageConfig::getPageType, pageType)
               .orderByDesc(LowPageConfig::getUpdateTime);

        Page<LowPageConfig> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public LowPageConfig getPageConfig(Long id) {
        LowPageConfig pageConfig = getById(id);
        if (pageConfig == null) {
            throw new BusinessException("页面配置不存在");
        }
        return pageConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPageConfig(LowPageConfig pageConfig) {
        // 检查编码是否重复
        if (StrUtil.isNotBlank(pageConfig.getPageCode())) {
            long count = lambdaQuery()
                    .eq(LowPageConfig::getPageCode, pageConfig.getPageCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("页面编码已存在");
            }
        }

        // 设置默认值
        if (pageConfig.getStatus() == null) {
            pageConfig.setStatus(true);
        }

        save(pageConfig);
        log.info("创建页面配置成功, id: {}, code: {}", pageConfig.getId(), pageConfig.getPageCode());
        return pageConfig.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePageConfig(Long id, LowPageConfig pageConfig) {
        LowPageConfig existConfig = getById(id);
        if (existConfig == null) {
            throw new BusinessException("页面配置不存在");
        }

        // 检查编码是否重复
        if (StrUtil.isNotBlank(pageConfig.getPageCode()) && !pageConfig.getPageCode().equals(existConfig.getPageCode())) {
            long count = lambdaQuery()
                    .eq(LowPageConfig::getPageCode, pageConfig.getPageCode())
                    .ne(LowPageConfig::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("页面编码已存在");
            }
        }

        pageConfig.setId(id);
        updateById(pageConfig);
        log.info("更新页面配置成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePageConfig(Long id) {
        removeById(id);
        log.info("删除页面配置成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishPage(Long id, String routePath) {
        LowPageConfig pageConfig = getById(id);
        if (pageConfig == null) {
            throw new BusinessException("页面配置不存在");
        }

        // 检查路由路径是否已被占用
        if (StrUtil.isNotBlank(routePath)) {
            long count = lambdaQuery()
                    .eq(LowPageConfig::getRoutePath, routePath)
                    .eq(LowPageConfig::getPublished, true)
                    .ne(LowPageConfig::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("该路由路径已被使用");
            }
        }

        // 更新发布状态
        pageConfig.setRoutePath(routePath);
        pageConfig.setPublished(true);
        pageConfig.setPublishTime(LocalDateTime.now());
        updateById(pageConfig);

        log.info("发布页面成功, id: {}, routePath: {}", id, routePath);
        return pageConfig.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublishPage(Long id) {
        LowPageConfig pageConfig = getById(id);
        if (pageConfig == null) {
            throw new BusinessException("页面配置不存在");
        }

        // 取消发布
        pageConfig.setPublished(false);
        pageConfig.setRoutePath(null);
        pageConfig.setPublishTime(null);
        updateById(pageConfig);

        // 同步禁用关联的菜单
        menuService.batchDisableByPageId(id);

        log.info("取消发布页面成功, id: {}", id);
    }

    @Override
    public List<LowPageConfig> getPublishedPages() {
        return lambdaQuery()
                .eq(LowPageConfig::getPublished, true)
                .eq(LowPageConfig::getStatus, true)
                .orderByAsc(LowPageConfig::getUpdateTime)
                .list();
    }
}
