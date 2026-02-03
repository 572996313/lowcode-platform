package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.dto.DictReferenceDetail;
import com.lowcode.dto.DictReferenceResult;
import com.lowcode.entity.SysDictCategory;
import com.lowcode.entity.SysDictReference;
import com.lowcode.mapper.SysDictReferenceMapper;
import com.lowcode.service.ISysDictCategoryService;
import com.lowcode.service.ISysDictReferenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典引用关系服务实现
 *
 * @author lowcode
 */
@Slf4j
@Service
public class SysDictReferenceServiceImpl extends ServiceImpl<SysDictReferenceMapper, SysDictReference>
        implements ISysDictReferenceService {

    private final ISysDictCategoryService dictCategoryService;

    public SysDictReferenceServiceImpl(@Lazy ISysDictCategoryService dictCategoryService) {
        this.dictCategoryService = dictCategoryService;
    }

    @Override
    public DictReferenceResult findReferences(String dictCode) {
        // 获取字典名称
        String dictName = "";
        SysDictCategory category = dictCategoryService.getByCategoryCode(dictCode);
        if (category != null) {
            dictName = category.getCategoryName();
        }

        // 查询引用记录
        List<SysDictReference> references = list(
                Wrappers.lambdaQuery(SysDictReference.class)
                        .eq(SysDictReference::getDictCategoryCode, dictCode)
        );

        // 转换为DTO
        List<DictReferenceDetail> details = references.stream()
                .map(this::convertToDetail)
                .collect(Collectors.toList());

        return DictReferenceResult.builder()
                .dictCode(dictCode)
                .dictName(dictName)
                .totalCount(details.size())
                .references(details)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addReference(String dictCategoryCode, String resourceType, Long resourceId,
                             String resourceName, Long pageId, String pageName,
                             String fieldCode, String fieldLabel, String referencePath) {
        if (StrUtil.isBlank(dictCategoryCode) || resourceId == null) {
            return;
        }

        // 检查是否已存在
        SysDictReference exist = getOne(
                Wrappers.lambdaQuery(SysDictReference.class)
                        .eq(SysDictReference::getDictCategoryCode, dictCategoryCode)
                        .eq(SysDictReference::getResourceType, resourceType)
                        .eq(SysDictReference::getResourceId, resourceId)
                        .eq(SysDictReference::getFieldCode, fieldCode)
        );

        if (exist != null) {
            // 更新现有记录
            exist.setResourceName(resourceName);
            exist.setPageId(pageId);
            exist.setPageName(pageName);
            exist.setFieldLabel(fieldLabel);
            exist.setReferencePath(referencePath);
            updateById(exist);
            log.debug("更新字典引用: dict={}, resourceType={}, resourceId={}", dictCategoryCode, resourceType, resourceId);
        } else {
            // 创建新记录
            SysDictReference reference = SysDictReference.builder()
                    .dictCategoryCode(dictCategoryCode)
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .resourceName(resourceName)
                    .pageId(pageId)
                    .pageName(pageName)
                    .fieldCode(fieldCode)
                    .fieldLabel(fieldLabel)
                    .referencePath(referencePath)
                    .build();
            save(reference);
            log.debug("添加字典引用: dict={}, resourceType={}, resourceId={}", dictCategoryCode, resourceType, resourceId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeReference(String dictCategoryCode, String resourceType, Long resourceId) {
        remove(
                Wrappers.lambdaQuery(SysDictReference.class)
                        .eq(SysDictReference::getDictCategoryCode, dictCategoryCode)
                        .eq(SysDictReference::getResourceType, resourceType)
                        .eq(SysDictReference::getResourceId, resourceId)
        );
        log.debug("移除字典引用: dict={}, resourceType={}, resourceId={}", dictCategoryCode, resourceType, resourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByResource(String resourceType, Long resourceId) {
        remove(
                Wrappers.lambdaQuery(SysDictReference.class)
                        .eq(SysDictReference::getResourceType, resourceType)
                        .eq(SysDictReference::getResourceId, resourceId)
        );
        log.debug("移除资源的所有字典引用: resourceType={}, resourceId={}", resourceType, resourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReference(String dictCategoryCode, String resourceType, Long resourceId,
                                String resourceName, Long pageId, String pageName,
                                String fieldCode, String fieldLabel, String referencePath) {
        // 先删除旧的引用
        remove(
                Wrappers.lambdaQuery(SysDictReference.class)
                        .eq(SysDictReference::getResourceType, resourceType)
                        .eq(SysDictReference::getResourceId, resourceId)
        );

        // 如果有字典编码，添加新引用
        if (StrUtil.isNotBlank(dictCategoryCode)) {
            addReference(dictCategoryCode, resourceType, resourceId, resourceName, pageId, pageName,
                    fieldCode, fieldLabel, referencePath);
        }
    }

    @Override
    public boolean isReferenced(String dictCode) {
        return count(
                Wrappers.lambdaQuery(SysDictReference.class)
                        .eq(SysDictReference::getDictCategoryCode, dictCode)
        ) > 0;
    }

    @Override
    public int getReferenceCount(String dictCode) {
        return Math.toIntExact(count(
                Wrappers.lambdaQuery(SysDictReference.class)
                        .eq(SysDictReference::getDictCategoryCode, dictCode)
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rebuildAllReferences() {
        log.info("开始重建所有字典引用索引");

        // 清空现有引用
        remove(Wrappers.lambdaQuery());
        int count = 0;

        // TODO: 遍历所有资源并重建引用
        // 1. 扫描 LowFormField 的 optionsJson
        // 2. 扫描 LowTableColumn 的 dictCode
        // 3. 扫描 FormStyleTemplate 的 styleConfig
        // 4. 扫描 TableStyleTemplate 的 styleConfig

        log.info("完成重建字典引用索引, 总数: {}", count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanUnusedReferences() {
        log.info("开始清理无效的字典引用");

        int count = 0;

        // 清理资源已不存在的引用
        // TODO: 检查每个引用对应的资源是否存在

        log.info("完成清理无效引用, 清理数量: {}", count);
        return count;
    }

    @Override
    public List<String> getUnusedDictCodes() {
        // 获取所有字典编码
        List<SysDictCategory> allCategories = dictCategoryService.list();
        List<String> allCodes = allCategories.stream()
                .map(SysDictCategory::getCategoryCode)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());

        // 获取被引用的字典编码
        List<String> usedCodes = list().stream()
                .map(SysDictReference::getDictCategoryCode)
                .distinct()
                .collect(Collectors.toList());

        // 返回未使用的字典编码
        return allCodes.stream()
                .filter(code -> !usedCodes.contains(code))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getAllDictReferenceCounts() {
        List<SysDictReference> allReferences = list();

        Map<String, Integer> counts = new HashMap<>();
        for (SysDictReference ref : allReferences) {
            counts.put(ref.getDictCategoryCode(), counts.getOrDefault(ref.getDictCategoryCode(), 0) + 1);
        }

        return counts;
    }

    /**
     * 转换为详情DTO
     */
    private DictReferenceDetail convertToDetail(SysDictReference ref) {
        return DictReferenceDetail.builder()
                .resourceType(ref.getResourceType())
                .resourceId(ref.getResourceId())
                .resourceName(ref.getResourceName())
                .pageId(ref.getPageId())
                .pageName(ref.getPageName())
                .fieldCode(ref.getFieldCode())
                .fieldLabel(ref.getFieldLabel())
                .referencePath(ref.getReferencePath())
                .resourceTypeDesc(getResourceTypeDesc(ref.getResourceType()))
                .build();
    }

    /**
     * 获取资源类型描述
     */
    private String getResourceTypeDesc(String resourceType) {
        return switch (resourceType) {
            case RESOURCE_TYPE_FORM_FIELD -> "表单字段";
            case RESOURCE_TYPE_TABLE_COLUMN -> "表格列";
            case RESOURCE_TYPE_FORM_TEMPLATE -> "表单模板";
            case RESOURCE_TYPE_TABLE_TEMPLATE -> "表格模板";
            default -> "未知类型";
        };
    }
}
