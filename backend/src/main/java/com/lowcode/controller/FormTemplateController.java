package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.LowFormTemplate;
import com.lowcode.service.IFormTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表单模板控制器
 */
@RestController
@RequestMapping("/api/form-template")
@Tag(name = "表单模板", description = "表单模板相关接口")
public class FormTemplateController {

    private final IFormTemplateService formTemplateService;

    public FormTemplateController(IFormTemplateService formTemplateService) {
        this.formTemplateService = formTemplateService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询模板列表")
    public Result<PageResult<LowFormTemplate>> getPage(@RequestParam Map<String, Object> params) {
        PageResult<LowFormTemplate> result = formTemplateService.getPage(params);
        return Result.success(result);
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有启用的模板")
    public Result<List<LowFormTemplate>> getAll() {
        List<LowFormTemplate> templates = formTemplateService.getAll();
        return Result.success(templates);
    }

    @GetMapping("/code/{templateCode}")
    @Operation(summary = "根据模板编码获取模板")
    public Result<LowFormTemplate> getByCode(@PathVariable String templateCode) {
        LowFormTemplate template = formTemplateService.getByCode(templateCode);
        if (template == null) {
            return Result.fail("模板不存在");
        }
        return Result.success(template);
    }

    @PostMapping
    @Operation(summary = "创建模板")
    public Result<Long> createTemplate(@RequestBody LowFormTemplate template) {
        // 检查模板编码是否已存在
        if (formTemplateService.getByCode(template.getTemplateCode()) != null) {
            return Result.fail("模板编码已存在");
        }
        boolean success = formTemplateService.save(template);
        return success ? Result.success(template.getId()) : Result.fail("创建失败");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新模板")
    public Result<Void> updateTemplate(@PathVariable Long id, @RequestBody LowFormTemplate template) {
        if (!formTemplateService.getById(id).getIsSystem()) {
            template.setId(id);
            boolean success = formTemplateService.updateById(template);
            return success ? Result.success() : Result.fail("更新失败");
        }
        return Result.fail("系统模板不能修改");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除模板")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        LowFormTemplate template = formTemplateService.getById(id);
        if (template != null && template.getIsSystem()) {
            return Result.fail("系统模板不能删除");
        }
        boolean success = formTemplateService.removeById(id);
        return success ? Result.success() : Result.fail("删除失败");
    }
}