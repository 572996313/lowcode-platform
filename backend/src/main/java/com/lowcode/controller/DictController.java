package com.lowcode.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.SysDictCategory;
import com.lowcode.entity.SysDictItem;
import com.lowcode.service.ISysDictCategoryService;
import com.lowcode.service.ISysDictItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理接口
 *
 * @author lowcode
 */
@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
@Tag(name = "字典管理", description = "字典配置相关接口")
public class DictController {

    private final ISysDictCategoryService dictCategoryService;
    private final ISysDictItemService dictItemService;

    // ===== 分类接口 =====

    /**
     * 获取字典分类树
     */
    @GetMapping("/category/tree")
    @Operation(summary = "获取字典分类树")
    public Result<List<SysDictCategory>> getCategoryTree() {
        return Result.success(dictCategoryService.getCategoryTree());
    }

    /**
     * 创建字典分类
     */
    @PostMapping("/category")
    @Operation(summary = "创建字典分类")
    public Result<Long> createCategory(@RequestBody SysDictCategory category) {
        return Result.success(dictCategoryService.createCategory(category));
    }

    /**
     * 更新字典分类
     */
    @PutMapping("/category/{id}")
    @Operation(summary = "更新字典分类")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody SysDictCategory category) {
        dictCategoryService.updateCategory(id, category);
        return Result.success();
    }

    /**
     * 删除字典分类
     */
    @DeleteMapping("/category/{id}")
    @Operation(summary = "删除字典分类")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        dictCategoryService.deleteCategory(id);
        return Result.success();
    }

    // ===== 字典明细接口 =====

    /**
     * 获取分类下的字典树
     */
    @GetMapping("/item/tree")
    @Operation(summary = "获取分类下的字典树")
    public Result<List<SysDictItem>> getItemTree(@RequestParam Long categoryId) {
        return Result.success(dictItemService.getItemTree(categoryId));
    }

    /**
     * 分页查询字典明细
     */
    @GetMapping("/item/page")
    @Operation(summary = "分页查询字典明细")
    public Result<PageResult<SysDictItem>> getItemPage(
            @RequestParam Long categoryId,
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<SysDictItem> page = new Page<>(current, size);
        return Result.success(dictItemService.getItemPage(categoryId, parentId, page));
    }

    /**
     * 创建字典明细
     */
    @PostMapping("/item")
    @Operation(summary = "创建字典明细")
    public Result<Long> createItem(@RequestBody SysDictItem item) {
        return Result.success(dictItemService.createItem(item));
    }

    /**
     * 更新字典明细
     */
    @PutMapping("/item/{id}")
    @Operation(summary = "更新字典明细")
    public Result<Void> updateItem(@PathVariable Long id, @RequestBody SysDictItem item) {
        dictItemService.updateItem(id, item);
        return Result.success();
    }

    /**
     * 删除字典明细
     */
    @DeleteMapping("/item/{id}")
    @Operation(summary = "删除字典明细")
    public Result<Void> deleteItem(@PathVariable Long id) {
        dictItemService.deleteItem(id);
        return Result.success();
    }

    /**
     * 批量删除字典明细
     */
    @PostMapping("/item/batch-delete")
    @Operation(summary = "批量删除字典明细")
    public Result<Void> batchDeleteItems(@RequestBody List<Long> ids) {
        dictItemService.batchDeleteItems(ids);
        return Result.success();
    }

    // ===== 控件调用接口 =====

    /**
     * 根据分类编码获取字典数据（给控件调用）
     */
    @GetMapping("/items/by-code")
    @Operation(summary = "根据分类编码获取字典数据")
    public Result<List<SysDictItem>> getItemsByCategoryCode(@RequestParam String categoryCode) {
        return Result.success(dictItemService.getItemsByCategoryCode(categoryCode));
    }

    /**
     * 根据字典编码获取字典数据（兼容旧接口）
     *
     * @deprecated 请使用 getItemsByCategoryCode 方法
     */
    @GetMapping("/{code}")
    @Operation(summary = "根据字典编码获取字典数据（兼容旧接口）")
    @Deprecated
    public Result<List<DictItem>> getDictByCode(@PathVariable String code) {
        List<SysDictItem> items = dictItemService.getItemsByCategoryCode(code);
        // 转换为旧格式
        List<DictItem> result = items.stream()
                .map(item -> new DictItem(item.getItemLabel(), item.getItemValue()))
                .toList();
        return Result.success(result);
    }

    /**
     * 字典项（兼容旧接口）
     *
     * @deprecated 请使用 SysDictItem
     */
    @Deprecated
    public record DictItem(String label, String value) {}
}
