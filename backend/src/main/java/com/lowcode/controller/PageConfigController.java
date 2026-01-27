package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.LowPageConfig;
import com.lowcode.service.ILowPageConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 页面配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/page")
@RequiredArgsConstructor
@Tag(name = "页面配置", description = "页面配置相关接口")
public class PageConfigController {

    private final ILowPageConfigService pageConfigService;

    @GetMapping("/list")
    @Operation(summary = "分页查询页面配置")
    public Result<PageResult<LowPageConfig>> getPageList(@RequestParam Map<String, Object> params) {
        PageResult<LowPageConfig> result = pageConfigService.getPageList(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取页面配置详情")
    public Result<LowPageConfig> getPageConfig(@PathVariable Long id) {
        LowPageConfig pageConfig = pageConfigService.getPageConfig(id);
        return Result.success(pageConfig);
    }

    @PostMapping
    @Operation(summary = "创建页面配置")
    public Result<Long> createPageConfig(@RequestBody LowPageConfig pageConfig) {
        Long id = pageConfigService.createPageConfig(pageConfig);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新页面配置")
    public Result<Void> updatePageConfig(@PathVariable Long id, @RequestBody LowPageConfig pageConfig) {
        pageConfigService.updatePageConfig(id, pageConfig);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除页面配置")
    public Result<Void> deletePageConfig(@PathVariable Long id) {
        pageConfigService.deletePageConfig(id);
        return Result.success();
    }
}
