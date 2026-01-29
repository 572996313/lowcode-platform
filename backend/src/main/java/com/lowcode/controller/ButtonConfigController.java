package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.LowButtonConfig;
import com.lowcode.service.ILowButtonConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 按钮配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/button")
@RequiredArgsConstructor
@Tag(name = "按钮配置", description = "按钮配置相关接口")
public class ButtonConfigController {

    private final ILowButtonConfigService buttonConfigService;

    @GetMapping("/list")
    @Operation(summary = "分页查询按钮配置")
    public Result<PageResult<LowButtonConfig>> getButtonList(@RequestParam Map<String, Object> params) {
        PageResult<LowButtonConfig> result = buttonConfigService.getButtonList(params);
        return Result.success(result);
    }

    @GetMapping("/page/{pageId}")
    @Operation(summary = "根据页面ID查询按钮")
    public Result<List<LowButtonConfig>> getButtonsByPageId(@PathVariable Long pageId) {
        List<LowButtonConfig> buttons = buttonConfigService.getButtonsByPageId(pageId);
        return Result.success(buttons);
    }

    @GetMapping("/form/{formId}")
    @Operation(summary = "根据表单ID查询按钮")
    public Result<List<LowButtonConfig>> getButtonsByFormId(@PathVariable Long formId) {
        List<LowButtonConfig> buttons = buttonConfigService.getButtonsByFormId(formId);
        return Result.success(buttons);
    }

    @GetMapping("/table/{tableId}")
    @Operation(summary = "根据表格ID查询按钮")
    public Result<List<LowButtonConfig>> getButtonsByTableId(@PathVariable Long tableId) {
        List<LowButtonConfig> buttons = buttonConfigService.getButtonsByTableId(tableId);
        return Result.success(buttons);
    }

    @PostMapping
    @Operation(summary = "创建按钮配置")
    public Result<Long> createButtonConfig(@RequestBody LowButtonConfig buttonConfig) {
        Long id = buttonConfigService.createButtonConfig(buttonConfig);
        return Result.success(id);
    }

    @PostMapping("/batch/{pageId}")
    @Operation(summary = "批量保存按钮配置")
    public Result<Void> batchSaveButtons(@PathVariable Long pageId, @RequestBody List<LowButtonConfig> buttons) {
        buttonConfigService.batchSaveButtons(pageId, buttons);
        return Result.success();
    }

    @PostMapping("/batch/form/{formId}")
    @Operation(summary = "按表单ID批量保存按钮配置")
    public Result<Void> batchSaveButtonsByFormId(@PathVariable Long formId, @RequestBody List<LowButtonConfig> buttons) {
        buttonConfigService.batchSaveButtonsByFormId(formId, buttons);
        return Result.success();
    }

    @PostMapping("/batch/table/{tableId}")
    @Operation(summary = "按表格ID批量保存按钮配置")
    public Result<Void> batchSaveButtonsByTableId(@PathVariable Long tableId, @RequestBody List<LowButtonConfig> buttons) {
        buttonConfigService.batchSaveButtonsByTableId(tableId, buttons);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新按钮配置")
    public Result<Void> updateButtonConfig(@PathVariable Long id, @RequestBody LowButtonConfig buttonConfig) {
        buttonConfigService.updateButtonConfig(id, buttonConfig);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除按钮配置")
    public Result<Void> deleteButtonConfig(@PathVariable Long id) {
        buttonConfigService.deleteButtonConfig(id);
        return Result.success();
    }
}
