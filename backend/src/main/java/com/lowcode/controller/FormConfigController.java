package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.LowFormConfig;
import com.lowcode.service.ILowFormConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 表单配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/form")
@RequiredArgsConstructor
@Tag(name = "表单配置", description = "表单配置相关接口")
public class FormConfigController {

    private final ILowFormConfigService formConfigService;

    @GetMapping("/list")
    @Operation(summary = "分页查询表单配置")
    public Result<PageResult<LowFormConfig>> getFormList(@RequestParam Map<String, Object> params) {
        PageResult<LowFormConfig> result = formConfigService.getFormList(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取表单配置详情（包含字段）")
    public Result<LowFormConfig> getFormConfig(@PathVariable Long id) {
        LowFormConfig formConfig = formConfigService.getFormConfig(id);
        return Result.success(formConfig);
    }

    @PostMapping
    @Operation(summary = "创建表单配置")
    public Result<Long> createFormConfig(@RequestBody LowFormConfig formConfig) {
        Long id = formConfigService.createFormConfig(formConfig);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新表单配置")
    public Result<Void> updateFormConfig(@PathVariable Long id, @RequestBody LowFormConfig formConfig) {
        formConfigService.updateFormConfig(id, formConfig);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除表单配置")
    public Result<Void> deleteFormConfig(@PathVariable Long id) {
        formConfigService.deleteFormConfig(id);
        return Result.success();
    }
}
