package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.DbTable;
import com.lowcode.entity.DbTableField;
import com.lowcode.service.IDbTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 库表管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/db-table")
@RequiredArgsConstructor
@Tag(name = "库表管理", description = "库表管理相关接口")
public class DbTableController {

    private final IDbTableService dbTableService;

    @GetMapping("/list")
    @Operation(summary = "分页查询库表列表")
    public Result<PageResult<DbTable>> getTableList(@RequestParam Map<String, Object> params) {
        PageResult<DbTable> result = dbTableService.getTableList(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取库表详情（包含字段）")
    public Result<DbTable> getTableDetail(@PathVariable Long id) {
        DbTable dbTable = dbTableService.getTableDetail(id);
        return Result.success(dbTable);
    }

    @GetMapping("/{id}/fields")
    @Operation(summary = "获取表的字段列表")
    public Result<List<DbTableField>> getTableFields(@PathVariable Long id) {
        List<DbTableField> fields = dbTableService.getTableFields(id);
        return Result.success(fields);
    }

    @PostMapping("/scan-all")
    @Operation(summary = "扫描并更新所有数据库对象")
    public Result<Void> scanAllTables() {
        dbTableService.scanAllTables();
        return Result.success();
    }

    @PostMapping("/{id}/sync")
    @Operation(summary = "同步指定表的字段信息")
    public Result<Void> syncTableFields(@PathVariable Long id) {
        dbTableService.syncTableFields(id);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新库表信息")
    public Result<Void> updateTable(@PathVariable Long id, @RequestBody DbTable dbTable) {
        dbTableService.updateTable(id, dbTable);
        return Result.success();
    }

    @PutMapping("/field/{fieldId}")
    @Operation(summary = "更新字段显示名称")
    public Result<Void> updateFieldLabel(@PathVariable Long fieldId, @RequestBody Map<String, String> params) {
        String fieldLabel = params.get("fieldLabel");
        dbTableService.updateFieldLabel(fieldId, fieldLabel);
        return Result.success();
    }

    @PutMapping("/field/batch")
    @Operation(summary = "批量更新字段标签")
    public Result<Void> batchUpdateFieldLabels(@RequestBody List<Map<String, Object>> fieldUpdates) {
        dbTableService.batchUpdateFieldLabels(fieldUpdates);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除库表（级联删除字段）")
    public Result<Void> deleteTable(@PathVariable Long id) {
        dbTableService.deleteTable(id);
        return Result.success();
    }

    @PutMapping("/{id}/group")
    @Operation(summary = "设置表的分组")
    public Result<Void> setTableGroup(@PathVariable Long id, @RequestBody Map<String, Long> params) {
        Long groupId = params.get("groupId");
        dbTableService.setTableGroup(id, groupId);
        return Result.success();
    }

    @PutMapping("/batch-group")
    @Operation(summary = "批量设置表的分组")
    public Result<Void> batchSetTableGroup(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Number> tableIdNumbers = (List<Number>) params.get("tableIds");
        List<Long> tableIds = tableIdNumbers.stream()
                .map(Number::longValue)
                .collect(java.util.stream.Collectors.toList());
        Long groupId = params.get("groupId") != null ? Long.parseLong(params.get("groupId").toString()) : null;
        dbTableService.batchSetTableGroup(tableIds, groupId);
        return Result.success();
    }
}
