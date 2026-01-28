package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.LowComponentTemplate;
import com.lowcode.service.IComponentTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 组件模板控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/component-template")
@RequiredArgsConstructor
@Tag(name = "组件模板", description = "组件模板相关接口")
public class ComponentTemplateController {

    private final IComponentTemplateService componentTemplateService;

    @GetMapping("/list")
    @Operation(summary = "分页查询组件模板")
    public Result<PageResult<LowComponentTemplate>> getPageList(@RequestParam Map<String, Object> params) {
        PageResult<LowComponentTemplate> result = componentTemplateService.getPageList(params);
        return Result.success(result);
    }

    @GetMapping("/all")
    @Operation(summary = "获取组件模板列表（不分页）")
    public Result<List<LowComponentTemplate>> getList(@RequestParam Map<String, Object> params) {
        List<LowComponentTemplate> result = componentTemplateService.getList(params);
        return Result.success(result);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "根据分类获取组件模板")
    public Result<List<LowComponentTemplate>> getByCategory(@PathVariable String category) {
        List<LowComponentTemplate> result = componentTemplateService.getByCategory(category);
        return Result.success(result);
    }

    @GetMapping("/code/{templateCode}")
    @Operation(summary = "根据模板编码获取模板")
    public Result<LowComponentTemplate> getByCode(@PathVariable String templateCode) {
        LowComponentTemplate result = componentTemplateService.getByCode(templateCode);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取组件模板详情")
    public Result<LowComponentTemplate> getTemplate(@PathVariable Long id) {
        LowComponentTemplate result = componentTemplateService.getById(id);
        return Result.success(result);
    }
}
