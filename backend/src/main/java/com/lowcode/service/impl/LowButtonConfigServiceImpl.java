package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.LowButtonConfig;
import com.lowcode.mapper.LowButtonConfigMapper;
import com.lowcode.service.ILowButtonConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 按钮配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LowButtonConfigServiceImpl extends ServiceImpl<LowButtonConfigMapper, LowButtonConfig> implements ILowButtonConfigService {

    @Override
    public PageResult<LowButtonConfig> getButtonList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        String buttonName = params.get("buttonName") != null ? params.get("buttonName").toString() : null;
        String buttonCode = params.get("buttonCode") != null ? params.get("buttonCode").toString() : null;
        String position = params.get("position") != null ? params.get("position").toString() : null;
        String componentCategory = params.get("componentCategory") != null ? params.get("componentCategory").toString() : null;

        Page<LowButtonConfig> page = new Page<>(current, size);

        LambdaQueryWrapper<LowButtonConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(buttonName), LowButtonConfig::getButtonName, buttonName)
                .like(StrUtil.isNotBlank(buttonCode), LowButtonConfig::getButtonCode, buttonCode)
                .eq(StrUtil.isNotBlank(componentCategory), LowButtonConfig::getComponentCategory, componentCategory)
                .eq(StrUtil.isNotBlank(position), LowButtonConfig::getPosition, position)
                .orderByAsc(LowButtonConfig::getSortOrder)
                .orderByDesc(LowButtonConfig::getUpdateTime);

        Page<LowButtonConfig> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public List<LowButtonConfig> getButtonsByPageId(Long pageId) {
        log.warn("使用了废弃的接口: getButtonsByPageId，建议使用组件库接口");
        return List.of();
    }

    @Override
    public List<LowButtonConfig> getButtonsByFormId(Long formId) {
        log.warn("使用了废弃的接口: getButtonsByFormId，建议使用组件库接口");
        return List.of();
    }

    @Override
    public List<LowButtonConfig> getButtonsByTableId(Long tableId) {
        log.warn("使用了废弃的接口: getButtonsByTableId，建议使用组件库接口");
        return List.of();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createButtonConfig(LowButtonConfig buttonConfig) {
        // 检查编码是否重复
        if (StrUtil.isNotBlank(buttonConfig.getButtonCode())) {
            long count = lambdaQuery()
                    .eq(LowButtonConfig::getButtonCode, buttonConfig.getButtonCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("按钮编码已存在");
            }
        }

        // 设置默认值
        if (buttonConfig.getButtonType() == null) {
            buttonConfig.setButtonType("default");
        }
        if (buttonConfig.getButtonSize() == null) {
            buttonConfig.setButtonSize("default");
        }
        if (buttonConfig.getPlain() == null) {
            buttonConfig.setPlain(false);
        }
        if (buttonConfig.getRound() == null) {
            buttonConfig.setRound(false);
        }
        if (buttonConfig.getCircle() == null) {
            buttonConfig.setCircle(false);
        }
        if (buttonConfig.getLoading() == null) {
            buttonConfig.setLoading(false);
        }
        if (buttonConfig.getDisabled() == null) {
            buttonConfig.setDisabled(false);
        }
        if (buttonConfig.getVisible() == null) {
            buttonConfig.setVisible(true);
        }
        if (buttonConfig.getSortOrder() == null) {
            buttonConfig.setSortOrder(0);
        }

        save(buttonConfig);

        log.info("创建按钮配置成功, id: {}, code: {}", buttonConfig.getId(), buttonConfig.getButtonCode());
        return buttonConfig.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateButtonConfig(Long id, LowButtonConfig buttonConfig) {
        LowButtonConfig existConfig = getById(id);
        if (existConfig == null) {
            throw new BusinessException("按钮配置不存在");
        }

        // 检查编码是否重复
        if (StrUtil.isNotBlank(buttonConfig.getButtonCode()) && !buttonConfig.getButtonCode().equals(existConfig.getButtonCode())) {
            long count = lambdaQuery()
                    .eq(LowButtonConfig::getButtonCode, buttonConfig.getButtonCode())
                    .ne(LowButtonConfig::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("按钮编码已存在");
            }
        }

        buttonConfig.setId(id);
        updateById(buttonConfig);

        log.info("更新按钮配置成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteButtonConfig(Long id) {
        LowButtonConfig buttonConfig = getById(id);
        if (buttonConfig == null) {
            throw new BusinessException("按钮配置不存在");
        }

        removeById(id);
        log.info("删除按钮配置成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveButtons(Long pageId, List<LowButtonConfig> buttons) {
        log.warn("使用了废弃的接口: batchSaveButtons，建议使用组件库接口");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveButtonsByFormId(Long formId, List<LowButtonConfig> buttons) {
        log.warn("使用了废弃的接口: batchSaveButtonsByFormId，建议使用组件库接口");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveButtonsByTableId(Long tableId, List<LowButtonConfig> buttons) {
        log.warn("使用了废弃的接口: batchSaveButtonsByTableId，建议使用组件库接口");
    }

    @Override
    public List<LowButtonConfig> getByCategory(String category) {
        return list(
                Wrappers.lambdaQuery(LowButtonConfig.class)
                        .eq(LowButtonConfig::getComponentCategory, category)
                        .eq(LowButtonConfig::getStatus, true)
                        .orderByAsc(LowButtonConfig::getSortOrder)
        );
    }

    @Override
    public List<LowButtonConfig> getByCategoryAndTags(String category, String tags) {
        // tags: "crud,export"
        List<String> tagList = Arrays.asList(tags.split(","));

        return list(
                Wrappers.lambdaQuery(LowButtonConfig.class)
                        .eq(LowButtonConfig::getComponentCategory, category)
                        .eq(LowButtonConfig::getStatus, true)
                        .orderByAsc(LowButtonConfig::getSortOrder)
        ).stream()
                .filter(btn -> {
                    if (btn.getComponentTags() == null) return false;
                    // 解析JSON标签
                    String tagsJson = btn.getComponentTags()
                            .replace("[", "")
                            .replace("]", "")
                            .replace("\"", "")
                            .replace(" ", "");
                    List<String> btnTags = Arrays.asList(tagsJson.split(","));
                    return btnTags.stream().anyMatch(tagList::contains);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long countByCategory(String category) {
        return lambdaQuery()
                .eq(LowButtonConfig::getComponentCategory, category)
                .count();
    }

    @Override
    public List<LowButtonConfig> getButtonsByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return List.of();
        }
        return listByIds(ids);
    }
}
