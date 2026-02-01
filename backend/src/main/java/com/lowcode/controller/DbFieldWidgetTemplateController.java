package com.lowcode.controller;

import com.lowcode.common.Result;
import com.lowcode.entity.DbFieldWidgetTemplate;
import com.lowcode.service.IDbFieldWidgetTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字段控件配置模板 Controller
 *
 * @author lowcode-platform
 * @since 2025-02-01
 */
@Tag(name = "字段控件模板管理", description = "字段控件配置模板的增删改查接口")
@RestController
@RequestMapping("/api/db-field-widget-template")
@RequiredArgsConstructor
public class DbFieldWidgetTemplateController {

    private final IDbFieldWidgetTemplateService templateService;

    @Operation(summary = "根据字段ID获取所有模板")
    @GetMapping("/field/{fieldId}")
    public Result<List<DbFieldWidgetTemplate>> getTemplatesByFieldId(
            @Parameter(description = "字段ID") @PathVariable Long fieldId) {
        List<DbFieldWidgetTemplate> templates = templateService.getTemplatesByFieldId(fieldId);
        return Result.success(templates);
    }

    @Operation(summary = "根据表ID获取所有模板")
    @GetMapping("/table/{tableId}")
    public Result<List<DbFieldWidgetTemplate>> getTemplatesByTableId(
            @Parameter(description = "表ID") @PathVariable Long tableId) {
        List<DbFieldWidgetTemplate> templates = templateService.getTemplatesByTableId(tableId);
        return Result.success(templates);
    }

    @Operation(summary = "根据字段ID和模板编码获取模板")
    @GetMapping("/field/{fieldId}/code/{templateCode}")
    public Result<DbFieldWidgetTemplate> getTemplateByCode(
            @Parameter(description = "字段ID") @PathVariable Long fieldId,
            @Parameter(description = "模板编码") @PathVariable String templateCode) {
        DbFieldWidgetTemplate template = templateService.getTemplateByCode(fieldId, templateCode);
        return Result.success(template);
    }

    @Operation(summary = "获取模板详情")
    @GetMapping("/{id}")
    public Result<DbFieldWidgetTemplate> getTemplateDetail(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        DbFieldWidgetTemplate template = templateService.getById(id);
        if (template == null) {
            return Result.fail("模板不存在");
        }
        return Result.success(template);
    }

    @Operation(summary = "创建模板")
    @PostMapping
    public Result<DbFieldWidgetTemplate> createTemplate(@RequestBody DbFieldWidgetTemplate template) {
        DbFieldWidgetTemplate created = templateService.createTemplate(template);
        return Result.success(created);
    }

    @Operation(summary = "更新模板")
    @PutMapping("/{id}")
    public Result<DbFieldWidgetTemplate> updateTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @RequestBody DbFieldWidgetTemplate template) {
        DbFieldWidgetTemplate updated = templateService.updateTemplate(id, template);
        return Result.success(updated);
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        boolean success = templateService.deleteTemplate(id);
        return success ? Result.success() : Result.fail("删除失败");
    }

    @Operation(summary = "应用模板为主配置")
    @PostMapping("/{id}/apply")
    public Result<Void> applyTemplateAsPrimary(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        boolean success = templateService.applyTemplateAsPrimary(id);
        return success ? Result.success() : Result.fail("应用失败");
    }

    @Operation(summary = "切换模板启用状态")
    @PutMapping("/{id}/toggle")
    public Result<Void> toggleEnabled(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Parameter(description = "启用状态") @RequestParam Integer enabled) {
        boolean success = templateService.toggleEnabled(id, enabled);
        return success ? Result.success() : Result.fail("切换失败");
    }
}
