package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.DbTable;
import com.lowcode.entity.DbTableGroup;
import com.lowcode.mapper.DbTableGroupMapper;
import com.lowcode.mapper.DbTableMapper;
import com.lowcode.service.IDbTableGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 库表分组服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DbTableGroupServiceImpl extends ServiceImpl<DbTableGroupMapper, DbTableGroup> implements IDbTableGroupService {

    private final DbTableMapper dbTableMapper;

    @Override
    public List<DbTableGroup> getGroupTree() {
        // 查询所有分组
        List<DbTableGroup> allGroups = list(
                Wrappers.lambdaQuery(DbTableGroup.class)
                        .orderByAsc(DbTableGroup::getSortOrder)
                        .orderByAsc(DbTableGroup::getId)
        );

        if (CollUtil.isEmpty(allGroups)) {
            return new ArrayList<>();
        }

        // 统计每个分组下的表数量
        List<DbTable> allTables = dbTableMapper.selectList(
                Wrappers.lambdaQuery(DbTable.class)
                        .isNotNull(DbTable::getGroupId)
        );
        Map<Long, Long> groupTableCount = allTables.stream()
                .filter(t -> t.getGroupId() != null)
                .collect(Collectors.groupingBy(DbTable::getGroupId, Collectors.counting()));

        // 设置表数量
        for (DbTableGroup group : allGroups) {
            group.setTableCount(groupTableCount.getOrDefault(group.getId(), 0L).intValue());
        }

        // 构建树形结构
        return buildTree(allGroups, 0L);
    }

    /**
     * 构建树形结构
     */
    private List<DbTableGroup> buildTree(List<DbTableGroup> allGroups, Long parentId) {
        List<DbTableGroup> tree = new ArrayList<>();
        for (DbTableGroup group : allGroups) {
            if (parentId.equals(group.getParentId())) {
                List<DbTableGroup> children = buildTree(allGroups, group.getId());
                if (CollUtil.isNotEmpty(children)) {
                    group.setChildren(children);
                    // 累加子分组的表数量
                    int childTableCount = children.stream()
                            .mapToInt(c -> c.getTableCount() != null ? c.getTableCount() : 0)
                            .sum();
                    group.setTableCount((group.getTableCount() != null ? group.getTableCount() : 0) + childTableCount);
                }
                tree.add(group);
            }
        }
        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGroup(DbTableGroup group) {
        // 检查分组名称是否重复
        long count = count(
                Wrappers.lambdaQuery(DbTableGroup.class)
                        .eq(DbTableGroup::getGroupName, group.getGroupName())
                        .eq(DbTableGroup::getParentId, group.getParentId() != null ? group.getParentId() : 0L)
        );
        if (count > 0) {
            throw new BusinessException("同级分组下已存在相同名称的分组");
        }

        // 设置默认值
        if (group.getParentId() == null) {
            group.setParentId(0L);
        }
        if (group.getSortOrder() == null) {
            group.setSortOrder(0);
        }

        save(group);
        log.info("创建库表分组成功, id: {}, name: {}", group.getId(), group.getGroupName());
        return group.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroup(Long id, DbTableGroup group) {
        DbTableGroup existGroup = getById(id);
        if (existGroup == null) {
            throw new BusinessException("分组不存在");
        }

        // 检查分组名称是否重复（排除自身）
        long count = count(
                Wrappers.lambdaQuery(DbTableGroup.class)
                        .eq(DbTableGroup::getGroupName, group.getGroupName())
                        .eq(DbTableGroup::getParentId, group.getParentId() != null ? group.getParentId() : existGroup.getParentId())
                        .ne(DbTableGroup::getId, id)
        );
        if (count > 0) {
            throw new BusinessException("同级分组下已存在相同名称的分组");
        }

        group.setId(id);
        updateById(group);
        log.info("更新库表分组成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(Long id) {
        DbTableGroup existGroup = getById(id);
        if (existGroup == null) {
            throw new BusinessException("分组不存在");
        }

        // 检查是否有子分组
        long childCount = count(
                Wrappers.lambdaQuery(DbTableGroup.class)
                        .eq(DbTableGroup::getParentId, id)
        );
        if (childCount > 0) {
            throw new BusinessException("该分组下存在子分组，请先删除子分组");
        }

        // 将该分组下的表设为未分组
        DbTable updateTable = new DbTable();
        updateTable.setGroupId(null);
        dbTableMapper.update(updateTable,
                Wrappers.lambdaUpdate(DbTable.class)
                        .eq(DbTable::getGroupId, id)
                        .set(DbTable::getGroupId, null)
        );

        // 删除分组
        removeById(id);
        log.info("删除库表分组成功, id: {}", id);
    }
}
