package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.DbTableField;
import com.lowcode.entity.FormFieldBinding;
import com.lowcode.service.IFormFieldBindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表单字段绑定控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/form-field-binding")
@RequiredArgsConstructor
@Tag(name = "表单字段绑定", description = "表单字段绑定相关接口")
public class FormFieldBindingController {

    private final IFormFieldBindingService formFieldBindingService;

    @GetMapping("/list")
    @Operation(summary = "分页查询表单字段绑定")
    public Result<PageResult<FormFieldBinding>> getFormFieldBindingList(@RequestParam Map<String, Object> params) {
        PageResult<FormFieldBinding> result = formFieldBindingService.getFormFieldBindingList(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取表单字段绑定详情")
    public Result<FormFieldBinding> getFormFieldBinding(@PathVariable Long id) {
        FormFieldBinding binding = formFieldBindingService.getById(id);
        return Result.success(binding);
    }

    @GetMapping("/form/{formId}")
    @Operation(summary = "根据表单ID获取字段绑定列表（包含字段信息）")
    public Result<List<FormFieldBinding>> getBindingsByFormId(@PathVariable Long formId) {
        List<FormFieldBinding> bindings = formFieldBindingService.getBindingsWithFieldsByFormId(formId);
        return Result.success(bindings);
    }

    @GetMapping("/table/{tableId}/fields")
    @Operation(summary = "根据数据库表ID获取可绑定的字段列表")
    public Result<List<DbTableField>> getAvailableFields(@PathVariable Long tableId) {
        List<DbTableField> fields = formFieldBindingService.getAvailableFieldsByTableId(tableId);
        return Result.success(fields);
    }

    @PostMapping
    @Operation(summary = "创建表单字段绑定")
    public Result<Long> createFormFieldBinding(@RequestBody FormFieldBinding binding) {
        Long id = formFieldBindingService.createFormFieldBinding(binding);
        return Result.success(id);
    }

    @PostMapping("/batch/{formId}")
    @Operation(summary = "批量保存表单字段绑定")
    public Result<Void> batchSaveBindings(@PathVariable Long formId, @RequestBody List<FormFieldBinding> bindings) {
        formFieldBindingService.batchSaveBindings(formId, bindings);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新表单字段绑定")
    public Result<Void> updateFormFieldBinding(@PathVariable Long id, @RequestBody FormFieldBinding binding) {
        formFieldBindingService.updateFormFieldBinding(id, binding);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除表单字段绑定")
    public Result<Void> deleteFormFieldBinding(@PathVariable Long id) {
        formFieldBindingService.deleteFormFieldBinding(id);
        return Result.success();
    }

    @DeleteMapping("/form/{formId}")
    @Operation(summary = "删除表单的所有字段绑定")
    public Result<Void> deleteBindingsByFormId(@PathVariable Long formId) {
        formFieldBindingService.deleteBindingsByFormId(formId);
        return Result.success();
    }
}
