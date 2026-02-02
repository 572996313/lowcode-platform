package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.PageLayout;
import com.lowcode.service.IPageLayoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 页面布局配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/page-layout")
@RequiredArgsConstructor
@Tag(name = "页面布局配置", description = "页面布局配置相关接口")
public class PageLayoutController {

    private final IPageLayoutService pageLayoutService;

    @GetMapping("/list")
    @Operation(summary = "分页查询页面布局")
    public Result<PageResult<PageLayout>> getPageLayoutList(@RequestParam Map<String, Object> params) {
        PageResult<PageLayout> result = pageLayoutService.getPageLayoutList(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取页面布局详情")
    public Result<PageLayout> getPageLayout(@PathVariable Long id) {
        PageLayout pageLayout = pageLayoutService.getPageLayout(id);
        return Result.success(pageLayout);
    }

    @GetMapping("/code/{layoutCode}")
    @Operation(summary = "根据编码获取页面布局")
    public Result<PageLayout> getByLayoutCode(@PathVariable String layoutCode) {
        PageLayout pageLayout = pageLayoutService.getByLayoutCode(layoutCode);
        return Result.success(pageLayout);
    }

    @PostMapping
    @Operation(summary = "创建页面布局")
    public Result<Long> createPageLayout(@RequestBody PageLayout pageLayout) {
        Long id = pageLayoutService.createPageLayout(pageLayout);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新页面布局")
    public Result<Void> updatePageLayout(@PathVariable Long id, @RequestBody PageLayout pageLayout) {
        pageLayoutService.updatePageLayout(id, pageLayout);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除页面布局")
    public Result<Void> deletePageLayout(@PathVariable Long id) {
        pageLayoutService.deletePageLayout(id);
        return Result.success();
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布页面布局")
    public Result<Void> publishPageLayout(@PathVariable Long id) {
        pageLayoutService.publishPageLayout(id);
        return Result.success();
    }

    @PostMapping("/{id}/unpublish")
    @Operation(summary = "取消发布页面布局")
    public Result<Void> unpublishPageLayout(@PathVariable Long id) {
        pageLayoutService.unpublishPageLayout(id);
        return Result.success();
    }
}
