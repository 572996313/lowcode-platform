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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 页面配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LowPageConfigServiceImpl extends ServiceImpl<LowPageConfigMapper, LowPageConfig> implements ILowPageConfigService {

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
}
