package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.DbFieldWidgetConfig;
import com.lowcode.entity.DbFieldWidgetTemplate;
import com.lowcode.service.IDbFieldWidgetConfigService;
import com.lowcode.service.IDbFieldWidgetTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字段-控件绑定配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/db-field-widget")
@RequiredArgsConstructor
@Tag(name = "字段-控件绑定", description = "字段-控件绑定配置相关接口")
public class DbFieldWidgetConfigController {

    private final IDbFieldWidgetConfigService dbFieldWidgetConfigService;
    private final IDbFieldWidgetTemplateService dbFieldWidgetTemplateService;

    @GetMapping("/list")
    @Operation(summary = "分页查询绑定配置列表")
    public Result<PageResult<DbFieldWidgetConfig>> getConfigList(@RequestParam Map<String, Object> params) {
        PageResult<DbFieldWidgetConfig> result = dbFieldWidgetConfigService.getConfigList(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取配置详情")
    public Result<DbFieldWidgetConfig> getConfigDetail(@PathVariable Long id) {
        DbFieldWidgetConfig config = dbFieldWidgetConfigService.getConfigDetail(id);
        return Result.success(config);
    }

    @GetMapping("/table/{tableId}")
    @Operation(summary = "获取表的所有绑定配置")
    public Result<List<DbFieldWidgetConfig>> getConfigsByTableId(@PathVariable Long tableId) {
        List<DbFieldWidgetConfig> configs = dbFieldWidgetConfigService.getConfigsByTableId(tableId);
        return Result.success(configs);
    }

    @GetMapping("/field/{fieldId}")
    @Operation(summary = "获取字段的绑定配置")
    public Result<DbFieldWidgetConfig> getConfigByFieldId(@PathVariable Long fieldId) {
        DbFieldWidgetConfig config = dbFieldWidgetConfigService.getConfigByFieldId(fieldId);
        return Result.success(config);
    }

    @GetMapping("/default-mappings")
    @Operation(summary = "获取所有默认映射规则")
    public Result<List<DbFieldWidgetConfig>> getDefaultMappings() {
        List<DbFieldWidgetConfig> mappings = dbFieldWidgetConfigService.getDefaultMappings();
        return Result.success(mappings);
    }

    @PostMapping
    @Operation(summary = "创建绑定配置")
    public Result<Void> createConfig(@RequestBody DbFieldWidgetConfig config) {
        dbFieldWidgetConfigService.createConfig(config);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新绑定配置")
    public Result<Void> updateConfig(@PathVariable Long id, @RequestBody DbFieldWidgetConfig config) {
        dbFieldWidgetConfigService.updateConfig(id, config);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除绑定配置")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        dbFieldWidgetConfigService.deleteConfig(id);
        return Result.success();
    }

    @PostMapping("/table/{tableId}/batch")
    @Operation(summary = "批量保存表的字段绑定配置")
    public Result<Void> batchSaveConfigs(@PathVariable Long tableId, @RequestBody List<DbFieldWidgetConfig> configs) {
        dbFieldWidgetConfigService.batchSaveConfigs(tableId, configs);
        return Result.success();
    }

    @PostMapping("/table/{tableId}/apply-defaults")
    @Operation(summary = "应用默认映射到表字段")
    public Result<Void> applyDefaultsToTable(@PathVariable Long tableId) {
        dbFieldWidgetConfigService.applyDefaultsToTable(tableId);
        return Result.success();
    }

    @PostMapping("/init-defaults")
    @Operation(summary = "初始化系统默认映射规则")
    public Result<Void> initDefaultMappings() {
        dbFieldWidgetConfigService.initDefaultMappings();
        return Result.success();
    }

    @GetMapping("/field/{fieldId}/with-templates")
    @Operation(summary = "获取字段配置及所有可用模板")
    public Result<Map<String, Object>> getFieldConfigWithTemplates(@PathVariable Long fieldId) {
        DbFieldWidgetConfig config = dbFieldWidgetConfigService.getConfigByFieldId(fieldId);
        List<DbFieldWidgetTemplate> templates = dbFieldWidgetTemplateService.getTemplatesByFieldId(fieldId);

        Map<String, Object> result = new HashMap<>();
        result.put("config", config);
        result.put("templates", templates);
        return Result.success(result);
    }
}
