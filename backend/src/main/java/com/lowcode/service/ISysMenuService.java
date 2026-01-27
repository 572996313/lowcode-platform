package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务接口
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单树
     */
    List<SysMenu> getMenuTree();

    /**
     * 创建菜单
     */
    Long createMenu(SysMenu menu);

    /**
     * 更新菜单
     */
    void updateMenu(Long id, SysMenu menu);

    /**
     * 删除菜单
     */
    void deleteMenu(Long id);
}
