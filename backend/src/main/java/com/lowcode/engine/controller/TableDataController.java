package com.lowcode.engine.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.common.dto.ApiRequest;
import com.lowcode.engine.dto.ComponentDetailData;
import com.lowcode.common.dto.PageQueryData;
import com.lowcode.engine.service.ITableDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 通用数据查询控制器
 * 根据页面配置查询数据，当前阶段返回模拟数据
 */
@Slf4j
@RestController
@RequestMapping("/api/table-data")
@RequiredArgsConstructor
@Tag(name = "表格数据", description = "通用表格数据查询接口")
public class TableDataController {

    private final ITableDataService tableDataService;

    @PostMapping("/query")
    @Operation(summary = "查询列表数据", description = "根据页面配置查询表格列表数据，支持搜索和分页")
    public Result<PageResult<Map<String, Object>>> queryTableData(
            @RequestBody ApiRequest<PageQueryData> request) {
        log.info("查询表格数据: pageId={}, componentId={}", request.getPageId(), request.getComponentId());
        PageResult<Map<String, Object>> result = tableDataService.queryTableData(request);
        return Result.success(result);
    }

    @PostMapping("/detail")
    @Operation(summary = "查询详情数据", description = "根据组件配置和行ID获取单条详情数据")
    public Result<Map<String, Object>> getDetailData(
            @RequestBody ApiRequest<ComponentDetailData> request) {
        log.info("查询详情数据: pageId={}, componentId={}, id={}",
                request.getPageId(),
                request.getData() != null ? request.getData().getComponentId() : null,
                request.getData() != null ? request.getData().getId() : null);
        Map<String, Object> result = tableDataService.getDetailData(request);
        return Result.success(result);
    }

    @PostMapping("/save")
    @Operation(summary = "保存数据", description = "保存表单数据（新增/编辑）")
    public Result<Long> saveData(@RequestBody ApiRequest<Map<String, Object>> request) {
        log.info("保存数据: pageId={}, componentId={}", request.getPageId(), request.getComponentId());
        Long id = tableDataService.saveData(request);
        return Result.success(id);
    }
}
