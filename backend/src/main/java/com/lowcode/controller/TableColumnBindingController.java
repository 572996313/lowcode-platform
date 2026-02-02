package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.DbTableField;
import com.lowcode.entity.TableColumnBinding;
import com.lowcode.service.ITableColumnBindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表格列绑定控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/table-column-binding")
@RequiredArgsConstructor
@Tag(name = "表格列绑定", description = "表格列绑定相关接口")
public class TableColumnBindingController {

    private final ITableColumnBindingService tableColumnBindingService;

    @GetMapping("/list")
    @Operation(summary = "分页查询表格列绑定")
    public Result<PageResult<TableColumnBinding>> getTableColumnBindingList(@RequestParam Map<String, Object> params) {
        PageResult<TableColumnBinding> result = tableColumnBindingService.getTableColumnBindingList(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取表格列绑定详情")
    public Result<TableColumnBinding> getTableColumnBinding(@PathVariable Long id) {
        TableColumnBinding binding = tableColumnBindingService.getById(id);
        return Result.success(binding);
    }

    @GetMapping("/table/{tableConfigId}")
    @Operation(summary = "根据表格配置ID获取列绑定列表（包含字段信息）")
    public Result<List<TableColumnBinding>> getBindingsByTableConfigId(@PathVariable Long tableConfigId) {
        List<TableColumnBinding> bindings = tableColumnBindingService.getBindingsWithFieldsByTableConfigId(tableConfigId);
        return Result.success(bindings);
    }

    @GetMapping("/table/{tableId}/fields")
    @Operation(summary = "根据数据库表ID获取可绑定的字段列表")
    public Result<List<DbTableField>> getAvailableFields(@PathVariable Long tableId) {
        List<DbTableField> fields = tableColumnBindingService.getAvailableFieldsByTableId(tableId);
        return Result.success(fields);
    }

    @PostMapping
    @Operation(summary = "创建表格列绑定")
    public Result<Long> createTableColumnBinding(@RequestBody TableColumnBinding binding) {
        Long id = tableColumnBindingService.createTableColumnBinding(binding);
        return Result.success(id);
    }

    @PostMapping("/batch/{tableConfigId}")
    @Operation(summary = "批量保存表格列绑定")
    public Result<Void> batchSaveBindings(@PathVariable Long tableConfigId, @RequestBody List<TableColumnBinding> bindings) {
        tableColumnBindingService.batchSaveBindings(tableConfigId, bindings);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新表格列绑定")
    public Result<Void> updateTableColumnBinding(@PathVariable Long id, @RequestBody TableColumnBinding binding) {
        tableColumnBindingService.updateTableColumnBinding(id, binding);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除表格列绑定")
    public Result<Void> deleteTableColumnBinding(@PathVariable Long id) {
        tableColumnBindingService.deleteTableColumnBinding(id);
        return Result.success();
    }

    @DeleteMapping("/table/{tableConfigId}")
    @Operation(summary = "删除表格配置的所有列绑定")
    public Result<Void> deleteBindingsByTableConfigId(@PathVariable Long tableConfigId) {
        tableColumnBindingService.deleteBindingsByTableConfigId(tableConfigId);
        return Result.success();
    }
}
