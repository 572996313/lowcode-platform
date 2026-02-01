package com.lowcode.controller;

import com.lowcode.common.PageResult;
import com.lowcode.common.Result;
import com.lowcode.entity.LowDropdownConfig;
import com.lowcode.service.IDropdownService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 下拉配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/dropdown")
@RequiredArgsConstructor
@Tag(name = "下拉配置", description = "下拉配置和查询相关接口")
public class DropdownController {

    private final IDropdownService dropdownService;

    // ============ 下拉配置管理接口 ============

    @GetMapping("/config/list")
    @Operation(summary = "分页查询下拉配置")
    public Result<PageResult<LowDropdownConfig>> getDropdownList(@RequestParam Map<String, Object> params) {
        PageResult<LowDropdownConfig> result = dropdownService.getDropdownList(params);
        return Result.success(result);
    }

    @GetMapping("/config/{id}")
    @Operation(summary = "获取下拉配置详情")
    public Result<LowDropdownConfig> getDropdownConfig(@PathVariable Long id) {
        LowDropdownConfig config = dropdownService.getDropdownConfig(id);
        return Result.success(config);
    }

    @GetMapping("/config/code/{code}")
    @Operation(summary = "根据编码获取配置")
    public Result<LowDropdownConfig> getDropdownByCode(@PathVariable String code) {
        LowDropdownConfig config = dropdownService.getDropdownByCode(code);
        return Result.success(config);
    }

    @PostMapping("/config")
    @Operation(summary = "创建下拉配置")
    public Result<Long> createDropdown(@RequestBody LowDropdownConfig config) {
        Long id = dropdownService.createDropdown(config);
        return Result.success(id);
    }

    @PutMapping("/config/{id}")
    @Operation(summary = "更新下拉配置")
    public Result<Void> updateDropdown(@PathVariable Long id, @RequestBody LowDropdownConfig config) {
        dropdownService.updateDropdown(id, config);
        return Result.success();
    }

    @DeleteMapping("/config/{id}")
    @Operation(summary = "删除下拉配置")
    public Result<Void> deleteDropdown(@PathVariable Long id) {
        dropdownService.deleteDropdown(id);
        return Result.success();
    }

    @PostMapping("/config/test")
    @Operation(summary = "测试 SQL（返回预览数据）")
    public Result<List<Map<String, Object>>> testSql(@RequestBody TestSqlRequest request) {
        List<Map<String, Object>> result = dropdownService.testSql(request.getConfig(), request.getParams());
        return Result.success(result);
    }

    // ============ 下拉数据查询接口 ============

    @PostMapping("/data/query")
    @Operation(summary = "查询下拉数据")
    public Result<Map<String, Object>> queryDropdownData(@RequestBody DropdownQueryRequest request) {
        Map<String, Object> result = dropdownService.queryDropdownData(
                request.getConfigId(),
                request.getConfigCode(),
                request.getParams()
        );
        return Result.success(result);
    }

    @GetMapping("/data/{configId}")
    @Operation(summary = "根据配置ID查询下拉数据（无参数）")
    public Result<Map<String, Object>> getDropdownData(@PathVariable Long configId) {
        Map<String, Object> result = dropdownService.queryDropdownData(configId, null, null);
        return Result.success(result);
    }

    // ============ 请求 DTO ============

    /**
     * SQL 测试请求
     */
    public static class TestSqlRequest {
        private LowDropdownConfig config;
        private Map<String, Object> params;

        public LowDropdownConfig getConfig() {
            return config;
        }

        public void setConfig(LowDropdownConfig config) {
            this.config = config;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }

    /**
     * 下拉数据查询请求
     */
    public static class DropdownQueryRequest {
        private Long configId;
        private String configCode;
        private Map<String, Object> params;

        public Long getConfigId() {
            return configId;
        }

        public void setConfigId(Long configId) {
            this.configId = configId;
        }

        public String getConfigCode() {
            return configCode;
        }

        public void setConfigCode(String configCode) {
            this.configCode = configCode;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }
    }
}
