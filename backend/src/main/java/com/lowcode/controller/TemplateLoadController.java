package com.lowcode.controller;

import com.lowcode.common.Result;
import com.lowcode.entity.LowButtonConfig;
import com.lowcode.service.ILowButtonConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板加载控制器
 * 负责加载按钮、表格列、表单字段等模板配置
 */
@Slf4j
@RestController
@RequestMapping("/api/template")
@RequiredArgsConstructor
@Tag(name = "模板加载", description = "加载模板配置相关接口")
public class TemplateLoadController {

    private final ILowButtonConfigService buttonConfigService;

    /**
     * 获取按钮模板配置
     *
     * @param templateId 模板ID
     * @return 包含完整配置和默认配置的结果
     */
    @GetMapping("/button/{templateId}")
    @Operation(summary = "获取按钮模板配置", description = "获取按钮模板的完整配置和默认配置")
    public Result<Map<String, Object>> getButtonTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long templateId
    ) {
        log.info("获取按钮模板配置: templateId={}", templateId);

        LowButtonConfig button = buttonConfigService.getById(templateId);
        if (button == null) {
            return Result.fail("按钮模板不存在: " + templateId);
        }

        // 构建完整模板配置
        Map<String, Object> templateConfig = buildButtonTemplateConfig(button);

        // 构建默认配置（用于页面配置）
        Map<String, Object> defaultConfig = buildButtonDefaultConfig(button);

        Map<String, Object> result = new HashMap<>();
        result.put("templateConfig", templateConfig);
        result.put("defaultConfig", defaultConfig);

        return Result.success(result);
    }

    /**
     * 批量获取按钮模板配置
     *
     * @param templateIds 模板ID列表
     * @return 包含完整配置和默认配置的结果列表
     */
    @PostMapping("/button/batch")
    @Operation(summary = "批量获取按钮模板配置", description = "批量获取按钮模板的完整配置和默认配置")
    public Result<List<Map<String, Object>>> getButtonTemplates(
            @RequestBody List<Long> templateIds
    ) {
        log.info("批量获取按钮模板配置: templateIds={}", templateIds);

        List<LowButtonConfig> buttons = buttonConfigService.getButtonsByIds(templateIds);
        List<Map<String, Object>> results = new java.util.ArrayList<>();

        for (LowButtonConfig button : buttons) {
            Map<String, Object> templateConfig = buildButtonTemplateConfig(button);
            Map<String, Object> defaultConfig = buildButtonDefaultConfig(button);

            Map<String, Object> result = new HashMap<>();
            result.put("templateConfig", templateConfig);
            result.put("defaultConfig", defaultConfig);
            results.add(result);
        }

        return Result.success(results);
    }

    /**
     * 获取表格列模板配置
     *
     * @param templateId 模板ID
     * @return 包含完整配置和默认配置的结果
     */
    @GetMapping("/column/{templateId}")
    @Operation(summary = "获取表格列模板配置", description = "获取表格列模板的完整配置和默认配置")
    public Result<Map<String, Object>> getColumnTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long templateId
    ) {
        log.info("获取表格列模板配置: templateId={}", templateId);

        // TODO: 实现表格列模板加载
        // 目前返回示例数据
        Map<String, Object> templateConfig = new HashMap<>();
        templateConfig.put("id", templateId);
        templateConfig.put("columnName", "示例列");
        templateConfig.put("columnCode", "example_column");
        templateConfig.put("columnType", "text");

        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("id", "col_" + templateId + "_" + System.currentTimeMillis());
        defaultConfig.put("prop", "example_column");
        defaultConfig.put("label", "示例列");
        defaultConfig.put("type", "text");
        defaultConfig.put("width", 150);
        defaultConfig.put("align", "left");

        Map<String, Object> result = new HashMap<>();
        result.put("templateConfig", templateConfig);
        result.put("defaultConfig", defaultConfig);

        return Result.success(result);
    }

    /**
     * 获取表单字段模板配置
     *
     * @param templateId 模板ID
     * @return 包含完整配置和默认配置的结果
     */
    @GetMapping("/field/{templateId}")
    @Operation(summary = "获取表单字段模板配置", description = "获取表单字段模板的完整配置和默认配置")
    public Result<Map<String, Object>> getFieldTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long templateId
    ) {
        log.info("获取表单字段模板配置: templateId={}", templateId);

        // TODO: 实现表单字段模板加载
        // 目前返回示例数据
        Map<String, Object> templateConfig = new HashMap<>();
        templateConfig.put("id", templateId);
        templateConfig.put("fieldName", "示例字段");
        templateConfig.put("fieldCode", "example_field");
        templateConfig.put("fieldType", "input");

        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("id", "field_" + templateId + "_" + System.currentTimeMillis());
        defaultConfig.put("fieldCode", "example_field");
        defaultConfig.put("label", "示例字段");
        defaultConfig.put("fieldType", "input");
        defaultConfig.put("placeholder", "请输入示例字段");
        defaultConfig.put("required", false);
        defaultConfig.put("span", 6);

        Map<String, Object> result = new HashMap<>();
        result.put("templateConfig", templateConfig);
        result.put("defaultConfig", defaultConfig);

        return Result.success(result);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 构建按钮模板完整配置
     */
    private Map<String, Object> buildButtonTemplateConfig(LowButtonConfig button) {
        Map<String, Object> config = new HashMap<>();
        config.put("id", button.getId());
        config.put("buttonName", button.getButtonName());
        config.put("buttonCode", button.getButtonCode());
        config.put("componentCategory", button.getComponentCategory());
        config.put("position", button.getPosition());
        config.put("buttonType", button.getButtonType());
        config.put("buttonSize", button.getButtonSize());
        config.put("icon", button.getIcon());
        config.put("actionType", button.getActionType());
        config.put("actionConfig", button.getActionConfig());
        config.put("confirmConfig", button.getConfirmConfig());
        config.put("perms", button.getPerms());
        config.put("sortOrder", button.getSortOrder());
        return config;
    }

    /**
     * 构建按钮默认配置（用于页面配置）
     */
    private Map<String, Object> buildButtonDefaultConfig(LowButtonConfig button) {
        Map<String, Object> config = new HashMap<>();

        // 生成唯一ID
        config.put("id", "btn_" + button.getId() + "_" + System.currentTimeMillis());

        // 按钮名称
        config.put("name", button.getButtonName());

        // 按钮类型
        config.put("type", button.getButtonType() != null ? button.getButtonType() : "default");

        // 图标
        if (button.getIcon() != null && !button.getIcon().isEmpty()) {
            config.put("icon", button.getIcon());
        }

        // 按钮动作
        Map<String, Object> action = new HashMap<>();
        action.put("type", button.getActionType() != null ? button.getActionType() : "custom");

        // 解析 actionConfig JSON
        if (button.getActionConfig() != null && !button.getActionConfig().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                Map<String, Object> actionConfigMap = mapper.readValue(button.getActionConfig(), Map.class);
                action.putAll(actionConfigMap);
            } catch (Exception e) {
                log.error("解析 actionConfig 失败", e);
            }
        }

        // 确保必要字段存在
        if (!action.containsKey("apiEndpoint")) {
            action.put("apiEndpoint", "");
        }
        if (!action.containsKey("confirmMessage")) {
            action.put("confirmMessage", "");
        }
        if (!action.containsKey("successMessage")) {
            action.put("successMessage", "");
        }

        config.put("action", action);

        // 权限标识
        if (button.getPerms() != null && !button.getPerms().isEmpty()) {
            config.put("perms", button.getPerms());
        }

        return config;
    }
}
