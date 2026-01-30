package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.SysMenu;
import com.lowcode.mapper.SysMenuMapper;
import com.lowcode.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Override
    public List<SysMenu> getMenuTree() {
        // 查询所有启用的菜单
        List<SysMenu> menuList = lambdaQuery()
                .eq(SysMenu::getStatus, true)
                .orderByAsc(SysMenu::getSortOrder)
                .list();

        // 构建树形结构
        return buildMenuTree(menuList, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMenu(SysMenu menu) {
        // 检查菜单编码是否重复
        if (menu.getMenuCode() != null) {
            long count = lambdaQuery()
                    .eq(SysMenu::getMenuCode, menu.getMenuCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("菜单编码已存在");
            }
        }

        // 设置默认值
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getSortOrder() == null) {
            menu.setSortOrder(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(true);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(true);
        }

        save(menu);
        log.info("创建菜单成功, id: {}, name: {}", menu.getId(), menu.getMenuName());
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Long id, SysMenu menu) {
        SysMenu existMenu = getById(id);
        if (existMenu == null) {
            throw new BusinessException("菜单不存在");
        }

        // 检查菜单编码是否重复
        if (menu.getMenuCode() != null && !menu.getMenuCode().equals(existMenu.getMenuCode())) {
            long count = lambdaQuery()
                    .eq(SysMenu::getMenuCode, menu.getMenuCode())
                    .ne(SysMenu::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("菜单编码已存在");
            }
        }

        menu.setId(id);
        updateById(menu);
        log.info("更新菜单成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        // 检查是否有子菜单
        long childCount = lambdaQuery()
                .eq(SysMenu::getParentId, id)
                .count();
        if (childCount > 0) {
            throw new BusinessException("存在子菜单，无法删除");
        }

        removeById(id);
        log.info("删除菜单成功, id: {}", id);
    }

    @Override
    public List<SysMenu> getMenusByPageId(Long pageId) {
        return lambdaQuery()
                .eq(SysMenu::getPageId, pageId)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateRoutePath(Long pageId, String newRoutePath) {
        List<SysMenu> menus = getMenusByPageId(pageId);
        if (menus.isEmpty()) {
            log.info("页面 {} 没有关联的菜单，无需更新路由", pageId);
            return;
        }

        menus.forEach(menu -> {
            menu.setRoutePath(newRoutePath);
        });

        updateBatchById(menus);
        log.info("批量更新菜单路由成功, pageId: {}, count: {}, newRoutePath: {}",
                pageId, menus.size(), newRoutePath);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDisableByPageId(Long pageId) {
        List<SysMenu> menus = getMenusByPageId(pageId);
        if (menus.isEmpty()) {
            log.info("页面 {} 没有关联的菜单，无需禁用", pageId);
            return;
        }

        menus.forEach(menu -> {
            menu.setStatus(false);
        });

        updateBatchById(menus);
        log.info("批量禁用菜单成功, pageId: {}, count: {}", pageId, menus.size());
    }

    /**
     * 构建菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menuList, Long parentId) {
        if (CollUtil.isEmpty(menuList)) {
            return new ArrayList<>();
        }

        // 按父ID分组
        Map<Long, List<SysMenu>> parentMap = menuList.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        // 递归构建树
        return buildTreeRecursive(parentMap, parentId);
    }

    private List<SysMenu> buildTreeRecursive(Map<Long, List<SysMenu>> parentMap, Long parentId) {
        List<SysMenu> children = parentMap.get(parentId);
        if (CollUtil.isEmpty(children)) {
            return new ArrayList<>();
        }

        for (SysMenu menu : children) {
            List<SysMenu> subChildren = buildTreeRecursive(parentMap, menu.getId());
            menu.setChildren(subChildren);
        }

        return children;
    }
}
