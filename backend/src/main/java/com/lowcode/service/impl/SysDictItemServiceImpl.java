package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.SysDictCategory;
import com.lowcode.entity.SysDictItem;
import com.lowcode.mapper.SysDictItemMapper;
import com.lowcode.service.ISysDictCategoryService;
import com.lowcode.service.ISysDictItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典明细服务实现
 *
 * @author lowcode
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem>
        implements ISysDictItemService {

    private final ISysDictCategoryService dictCategoryService;

    @Override
    public List<SysDictItem> getItemTree(Long categoryId) {
        // 查询该分类下所有启用的字典项
        List<SysDictItem> itemList = lambdaQuery()
                .eq(SysDictItem::getCategoryId, categoryId)
                .eq(SysDictItem::getStatus, true)
                .orderByAsc(SysDictItem::getSortOrder)
                .list();

        // 构建树形结构
        return buildItemTree(itemList, 0L);
    }

    @Override
    public PageResult<SysDictItem> getItemPage(Long categoryId, Long parentId, Page<SysDictItem> page) {
        IPage<SysDictItem> result = lambdaQuery()
                .eq(SysDictItem::getCategoryId, categoryId)
                .eq(parentId != null, SysDictItem::getParentId, parentId)
                .eq(SysDictItem::getStatus, true)
                .orderByAsc(SysDictItem::getSortOrder)
                .page(page);

        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createItem(SysDictItem item) {
        // 检查分类是否存在
        if (item.getCategoryId() != null) {
            SysDictCategory category = dictCategoryService.getById(item.getCategoryId());
            if (category == null) {
                throw new BusinessException("字典分类不存在");
            }
        }

        // 检查同一分类下的值是否重复（同级）
        if (item.getCategoryId() != null && item.getItemValue() != null) {
            long count = lambdaQuery()
                    .eq(SysDictItem::getCategoryId, item.getCategoryId())
                    .eq(SysDictItem::getItemValue, item.getItemValue())
                    .eq(SysDictItem::getParentId, item.getParentId() == null ? 0L : item.getParentId())
                    .count();
            if (count > 0) {
                throw new BusinessException("同一分类下该字典值已存在");
            }
        }

        // 设置默认值
        if (item.getParentId() == null) {
            item.setParentId(0L);
        }
        if (item.getSortOrder() == null) {
            item.setSortOrder(0);
        }
        if (item.getStatus() == null) {
            item.setStatus(true);
        }

        save(item);
        log.info("创建字典明细成功, id: {}, label: {}, value: {}", item.getId(), item.getItemLabel(), item.getItemValue());
        return item.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(Long id, SysDictItem item) {
        SysDictItem existItem = getById(id);
        if (existItem == null) {
            throw new BusinessException("字典明细不存在");
        }

        // 检查同一分类下的值是否重复
        if (item.getItemValue() != null && !item.getItemValue().equals(existItem.getItemValue())) {
            long count = lambdaQuery()
                    .eq(SysDictItem::getCategoryId, existItem.getCategoryId())
                    .eq(SysDictItem::getItemValue, item.getItemValue())
                    .eq(SysDictItem::getParentId, existItem.getParentId())
                    .ne(SysDictItem::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("同一分类下该字典值已存在");
            }
        }

        item.setId(id);
        updateById(item);
        log.info("更新字典明细成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long id) {
        // 检查是否有子项
        long childCount = lambdaQuery()
                .eq(SysDictItem::getParentId, id)
                .count();
        if (childCount > 0) {
            throw new BusinessException("存在子项，无法删除");
        }

        removeById(id);
        log.info("删除字典明细成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteItems(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }

        // 检查是否有子项
        for (Long id : ids) {
            long childCount = lambdaQuery()
                    .eq(SysDictItem::getParentId, id)
                    .count();
            if (childCount > 0) {
                SysDictItem item = getById(id);
                throw new BusinessException("字典项[" + (item != null ? item.getItemLabel() : id) + "]存在子项，无法删除");
            }
        }

        removeByIds(ids);
        log.info("批量删除字典明细成功, count: {}", ids.size());
    }

    @Override
    public List<SysDictItem> getItemsByCategoryCode(String categoryCode) {
        // 根据分类编码获取分类
        SysDictCategory category = dictCategoryService.getByCategoryCode(categoryCode);
        if (category == null) {
            log.warn("字典分类不存在: {}", categoryCode);
            return new ArrayList<>();
        }

        // 查询该分类下所有启用的字典项（树形结构）
        return getItemTree(category.getId());
    }

    /**
     * 构建字典项树
     */
    private List<SysDictItem> buildItemTree(List<SysDictItem> itemList, Long parentId) {
        if (CollUtil.isEmpty(itemList)) {
            return new ArrayList<>();
        }

        // 按父ID分组
        Map<Long, List<SysDictItem>> parentMap = itemList.stream()
                .collect(Collectors.groupingBy(SysDictItem::getParentId));

        // 递归构建树
        return buildTreeRecursive(parentMap, parentId);
    }

    private List<SysDictItem> buildTreeRecursive(Map<Long, List<SysDictItem>> parentMap, Long parentId) {
        List<SysDictItem> children = parentMap.get(parentId);
        if (CollUtil.isEmpty(children)) {
            return new ArrayList<>();
        }

        for (SysDictItem item : children) {
            List<SysDictItem> subChildren = buildTreeRecursive(parentMap, item.getId());
            item.setChildren(subChildren);
        }

        return children;
    }
}