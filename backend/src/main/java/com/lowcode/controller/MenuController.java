package com.lowcode.controller;

import com.lowcode.common.Result;
import com.lowcode.entity.SysMenu;
import com.lowcode.service.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@Tag(name = "菜单管理", description = "菜单配置相关接口")
public class MenuController {

    private final ISysMenuService menuService;

    @GetMapping("/tree")
    @Operation(summary = "获取菜单树")
    public Result<List<SysMenu>> getMenuTree() {
        List<SysMenu> menuTree = menuService.getMenuTree();
        return Result.success(menuTree);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取菜单详情")
    public Result<SysMenu> getMenu(@PathVariable Long id) {
        SysMenu menu = menuService.getById(id);
        return Result.success(menu);
    }

    @PostMapping
    @Operation(summary = "创建菜单")
    public Result<Long> createMenu(@RequestBody SysMenu menu) {
        Long id = menuService.createMenu(menu);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新菜单")
    public Result<Void> updateMenu(@PathVariable Long id, @RequestBody SysMenu menu) {
        menuService.updateMenu(id, menu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "获取菜单列表")
    public Result<List<SysMenu>> getMenuList() {
        List<SysMenu> menuList = menuService.list();
        return Result.success(menuList);
    }

    @GetMapping("/by-page/{pageId}")
    @Operation(summary = "根据页面ID查询关联的菜单")
    public Result<List<SysMenu>> getMenusByPageId(@PathVariable Long pageId) {
        List<SysMenu> menus = menuService.getMenusByPageId(pageId);
        return Result.success(menus);
    }

    @PutMapping("/sync-route")
    @Operation(summary = "批量更新菜单路由地址")
    public Result<String> syncRoutePath(
            @RequestParam Long pageId,
            @RequestParam String newRoutePath) {
        menuService.batchUpdateRoutePath(pageId, newRoutePath);
        return Result.success("路由同步成功");
    }

    @PutMapping("/disable-by-page/{pageId}")
    @Operation(summary = "批量禁用菜单（根据页面ID）")
    public Result<String> disableByPageId(@PathVariable Long pageId) {
        menuService.batchDisableByPageId(pageId);
        return Result.success("关联菜单已禁用");
    }
}
