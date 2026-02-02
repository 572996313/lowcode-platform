package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.TableStyleTemplate;
import com.lowcode.service.ITableStyleTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表格样式模板控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/table-style-template")
@RequiredArgsConstructor
@Tag(name = "表格样式模板", description = "表格样式模板相关接口")
public class TableStyleTemplateController {

    private final ITableStyleTemplateService tableStyleTemplateService;

    @GetMapping("/page")
    @Operation(summary = "分页查询模板列表")
    public Result<PageResult<TableStyleTemplate>> getPage(@RequestParam Map<String, Object> params) {
        PageResult<TableStyleTemplate> result = tableStyleTemplateService.getPage(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取模板详情")
    public Result<TableStyleTemplate> getById(@PathVariable Long id) {
        TableStyleTemplate template = tableStyleTemplateService.getById(id);
        return Result.success(template);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有启用的模板")
    public Result<List<TableStyleTemplate>> getAllEnabled() {
        List<TableStyleTemplate> list = tableStyleTemplateService.getAllEnabled();
        return Result.success(list);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据编码获取模板")
    public Result<TableStyleTemplate> getByCode(@PathVariable String code) {
        TableStyleTemplate template = tableStyleTemplateService.getByCode(code);
        return Result.success(template);
    }

    @PostMapping
    @Operation(summary = "创建模板")
    public Result<Long> create(@RequestBody TableStyleTemplate template) {
        tableStyleTemplateService.save(template);
        return Result.success(template.getId());
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新模板")
    public Result<Void> update(@PathVariable Long id, @RequestBody TableStyleTemplate template) {
        template.setId(id);
        tableStyleTemplateService.updateById(template);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除模板")
    public Result<Void> delete(@PathVariable Long id) {
        tableStyleTemplateService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "切换模板状态")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        TableStyleTemplate template = tableStyleTemplateService.getById(id);
        if (template != null) {
            template.setStatus(!template.getStatus());
            tableStyleTemplateService.updateById(template);
        }
        return Result.success();
    }
}
