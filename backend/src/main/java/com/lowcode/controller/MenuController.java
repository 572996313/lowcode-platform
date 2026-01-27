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
}
