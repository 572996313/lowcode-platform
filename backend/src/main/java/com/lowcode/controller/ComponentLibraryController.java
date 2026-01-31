package com.lowcode.controller;

import com.lowcode.common.Result;
import com.lowcode.entity.LowButtonConfig;
import com.lowcode.entity.LowFormConfig;
import com.lowcode.entity.LowTableConfig;
import com.lowcode.service.ILowButtonConfigService;
import com.lowcode.service.ILowFormConfigService;
import com.lowcode.service.ILowTableConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组件库控制器
 */
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
@Tag(name = "组件库", description = "组件库相关接口")
public class ComponentLibraryController {

    private final ILowButtonConfigService buttonConfigService;
    private final ILowFormConfigService formConfigService;
    private final ILowTableConfigService tableConfigService;

    /**
     * 获取组件库中的组件
     */
    @GetMapping("/components")
    @Operation(summary = "获取组件库中的组件", description = "根据组件分类获取按钮、表单、表格组件")
    public Result<Map<String, Object>> getLibraryComponents(
            @Parameter(description = "组件分类(common=通用组件库, business=业务组件库)")
            @RequestParam String libraryType,

            @Parameter(description = "组件类型(form/table/button，不传则返回所有类型)")
            @RequestParam(required = false) String componentType
    ) {
        Map<String, Object> result = new HashMap<>();

        // 根据组件分类获取组件
        if (componentType == null || "form".equals(componentType)) {
            List<LowFormConfig> forms = formConfigService.getByCategory(libraryType);
            result.put("forms", forms);
        }

        if (componentType == null || "table".equals(componentType)) {
            List<LowTableConfig> tables = tableConfigService.getByCategory(libraryType);
            result.put("tables", tables);
        }

        if (componentType == null || "button".equals(componentType)) {
            List<LowButtonConfig> buttons = buttonConfigService.getByCategory(libraryType);
            result.put("buttons", buttons);
        }

        return Result.success(result);
    }

    /**
     * 获取组件库统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取组件库统计信息", description = "统计通用组件库和业务组件库的组件数量")
    public Result<Map<String, Object>> getLibraryStats() {
        Map<String, Object> stats = new HashMap<>();

        // 通用组件库统计
        Map<String, Object> commonStats = new HashMap<>();
        commonStats.put("buttons", buttonConfigService.countByCategory("common"));
        commonStats.put("forms", formConfigService.countByCategory("common"));
        commonStats.put("tables", tableConfigService.countByCategory("common"));

        // 业务组件库统计
        Map<String, Object> businessStats = new HashMap<>();
        businessStats.put("buttons", buttonConfigService.countByCategory("business"));
        businessStats.put("forms", formConfigService.countByCategory("business"));
        businessStats.put("tables", tableConfigService.countByCategory("business"));

        stats.put("common", commonStats);
        stats.put("business", businessStats);

        return Result.success(stats);
    }
}
