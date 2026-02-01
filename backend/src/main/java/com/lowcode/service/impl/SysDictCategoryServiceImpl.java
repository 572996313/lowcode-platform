package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.SysDictCategory;
import com.lowcode.mapper.SysDictCategoryMapper;
import com.lowcode.service.ISysDictCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典分类服务实现
 *
 * @author lowcode
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictCategoryServiceImpl extends ServiceImpl<SysDictCategoryMapper, SysDictCategory>
        implements ISysDictCategoryService {

    @Override
    public List<SysDictCategory> getCategoryTree() {
        // 查询所有启用的分类
        List<SysDictCategory> categoryList = lambdaQuery()
                .eq(SysDictCategory::getStatus, true)
                .orderByAsc(SysDictCategory::getSortOrder)
                .list();

        // 构建树形结构
        return buildCategoryTree(categoryList, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(SysDictCategory category) {
        // 检查分类编码是否重复
        if (category.getCategoryCode() != null) {
            long count = lambdaQuery()
                    .eq(SysDictCategory::getCategoryCode, category.getCategoryCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("分类编码已存在");
            }
        }

        // 设置默认值
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(true);
        }

        save(category);
        log.info("创建字典分类成功, id: {}, name: {}, code: {}", category.getId(), category.getCategoryName(), category.getCategoryCode());
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, SysDictCategory category) {
        SysDictCategory existCategory = getById(id);
        if (existCategory == null) {
            throw new BusinessException("字典分类不存在");
        }

        // 检查分类编码是否重复
        if (category.getCategoryCode() != null && !category.getCategoryCode().equals(existCategory.getCategoryCode())) {
            long count = lambdaQuery()
                    .eq(SysDictCategory::getCategoryCode, category.getCategoryCode())
                    .ne(SysDictCategory::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("分类编码已存在");
            }
        }

        category.setId(id);
        updateById(category);
        log.info("更新字典分类成功, id: {}", id);
    }

    @Override
    public void deleteCategory(Long id) {
        // 检查是否有子分类、检查是否有关联的字典明细
        // 这里为了简化，直接允许删除，可以通过级联删除或提示用户处理关联数据
        removeById(id);
        log.info("删除字典分类成功, id: {}", id);
    }

    @Override
    public SysDictCategory getByCategoryCode(String categoryCode) {
        return lambdaQuery()
                .eq(SysDictCategory::getCategoryCode, categoryCode)
                .one();
    }

    /**
     * 构建分类树
     */
    private List<SysDictCategory> buildCategoryTree(List<SysDictCategory> categoryList, Long parentId) {
        if (CollUtil.isEmpty(categoryList)) {
            return new ArrayList<>();
        }

        // 按父ID分组
        Map<Long, List<SysDictCategory>> parentMap = categoryList.stream()
                .collect(Collectors.groupingBy(SysDictCategory::getParentId));

        // 递归构建树
        return buildTreeRecursive(parentMap, parentId);
    }

    private List<SysDictCategory> buildTreeRecursive(Map<Long, List<SysDictCategory>> parentMap, Long parentId) {
        List<SysDictCategory> children = parentMap.get(parentId);
        if (CollUtil.isEmpty(children)) {
            return new ArrayList<>();
        }

        for (SysDictCategory category : children) {
            List<SysDictCategory> subChildren = buildTreeRecursive(parentMap, category.getId());
            category.setChildren(subChildren);
        }

        return children;
    }
}
