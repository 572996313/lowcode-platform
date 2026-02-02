package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.FormStyleTemplate;
import com.lowcode.service.IFormStyleTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表单样式模板控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/form-style-template")
@RequiredArgsConstructor
@Tag(name = "表单样式模板", description = "表单样式模板相关接口")
public class FormStyleTemplateController {

    private final IFormStyleTemplateService formStyleTemplateService;

    @GetMapping("/page")
    @Operation(summary = "分页查询模板列表")
    public Result<PageResult<FormStyleTemplate>> getPage(@RequestParam Map<String, Object> params) {
        PageResult<FormStyleTemplate> result = formStyleTemplateService.getPage(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取模板详情")
    public Result<FormStyleTemplate> getById(@PathVariable Long id) {
        FormStyleTemplate template = formStyleTemplateService.getById(id);
        return Result.success(template);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有启用的模板")
    public Result<List<FormStyleTemplate>> getAllEnabled() {
        List<FormStyleTemplate> list = formStyleTemplateService.getAllEnabled();
        return Result.success(list);
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "根据编码获取模板")
    public Result<FormStyleTemplate> getByCode(@PathVariable String code) {
        FormStyleTemplate template = formStyleTemplateService.getByCode(code);
        return Result.success(template);
    }

    @PostMapping
    @Operation(summary = "创建模板")
    public Result<Long> create(@RequestBody FormStyleTemplate template) {
        formStyleTemplateService.save(template);
        return Result.success(template.getId());
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新模板")
    public Result<Void> update(@PathVariable Long id, @RequestBody FormStyleTemplate template) {
        template.setId(id);
        formStyleTemplateService.updateById(template);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除模板")
    public Result<Void> delete(@PathVariable Long id) {
        formStyleTemplateService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "切换模板状态")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        FormStyleTemplate template = formStyleTemplateService.getById(id);
        if (template != null) {
            template.setStatus(!template.getStatus());
            formStyleTemplateService.updateById(template);
        }
        return Result.success();
    }
}
