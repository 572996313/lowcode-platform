package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.LowTableConfig;
import com.lowcode.service.ILowTableConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 表格配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
@Tag(name = "表格配置", description = "表格配置相关接口")
public class TableConfigController {

    private final ILowTableConfigService tableConfigService;

    @GetMapping("/list")
    @Operation(summary = "分页查询表格配置")
    public Result<PageResult<LowTableConfig>> getTableList(@RequestParam Map<String, Object> params) {
        PageResult<LowTableConfig> result = tableConfigService.getTableList(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取表格配置详情（包含列配置）")
    public Result<LowTableConfig> getTableConfig(@PathVariable Long id) {
        LowTableConfig tableConfig = tableConfigService.getTableConfig(id);
        return Result.success(tableConfig);
    }

    @PostMapping
    @Operation(summary = "创建表格配置")
    public Result<Long> createTableConfig(@RequestBody LowTableConfig tableConfig) {
        Long id = tableConfigService.createTableConfig(tableConfig);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新表格配置")
    public Result<Void> updateTableConfig(@PathVariable Long id, @RequestBody LowTableConfig tableConfig) {
        tableConfigService.updateTableConfig(id, tableConfig);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除表格配置")
    public Result<Void> deleteTableConfig(@PathVariable Long id) {
        tableConfigService.deleteTableConfig(id);
        return Result.success();
    }
}
