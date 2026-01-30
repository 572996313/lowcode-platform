package com.lowcode.controller;

import com.lowcode.common.Result;
import com.lowcode.entity.LowPageConfig;
import com.lowcode.entity.LowPageTemplate;
import com.lowcode.service.ILowPageConfigService;
import com.lowcode.service.IPageTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 页面模板控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/page/template")
@RequiredArgsConstructor
@Tag(name = "页面模板", description = "页面模板相关接口")
public class PageTemplateController {

    private final IPageTemplateService pageTemplateService;
    private final ILowPageConfigService pageConfigService;

    @GetMapping("/list")
    @Operation(summary = "获取页面模板列表")
    public Result<List<LowPageTemplate>> getList() {
        List<LowPageTemplate> result = pageTemplateService.getList();
        return Result.success(result);
    }

    @GetMapping("/code/{templateCode}")
    @Operation(summary = "根据模板编码获取模板")
    public Result<LowPageTemplate> getByCode(@PathVariable String templateCode) {
        LowPageTemplate result = pageTemplateService.getByCode(templateCode);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取页面模板详情")
    public Result<LowPageTemplate> getTemplate(@PathVariable Long id) {
        LowPageTemplate result = pageTemplateService.getById(id);
        return Result.success(result);
    }

    @PostMapping("/from-template")
    @Operation(summary = "从模板创建页面")
    public Result<Long> createPageFromTemplate(@RequestBody Map<String, Object> params) {
        String templateCode = (String) params.get("templateCode");
        String pageName = (String) params.get("pageName");
        String pageCode = (String) params.get("pageCode");

        // 获取模板
        LowPageTemplate template = pageTemplateService.getByCode(templateCode);
        if (template == null) {
            return Result.fail("模板不存在");
        }

        // 获取默认配置模板（如果模板本身没有配置）
        String configTemplate = template.getConfigTemplate();
        if (configTemplate == null || configTemplate.isEmpty()) {
            configTemplate = getDefaultConfigTemplate(template.getLayoutType());
        }

        // 创建页面配置
        LowPageConfig pageConfig = LowPageConfig.builder()
                .pageName(pageName)
                .pageCode(pageCode)
                .pageType(template.getTemplateType())
                .layoutType(template.getLayoutType())
                .templateId(template.getId())
                .configJson(configTemplate)
                .configTemplate(configTemplate)
                .status(true)
                .build();

        Long pageId = pageConfigService.createPageConfig(pageConfig);
        log.info("从模板创建页面成功, templateCode: {}, layoutType: {}, pageId: {}",
                 templateCode, template.getLayoutType(), pageId);

        return Result.success(pageId);
    }

    /**
     * 根据布局类型获取默认配置模板
     */
    private String getDefaultConfigTemplate(String layoutType) {
        if (layoutType == null) {
            layoutType = "custom";
        }

        return switch (layoutType) {
            case "top-bottom", "list" -> """
                {
                  "searchArea": {
                    "enabled": true,
                    "title": "查询条件",
                    "fields": []
                  },
                  "contentArea": {
                    "type": "table",
                    "configId": null,
                    "title": "数据列表",
                    "showToolbar": true
                  }
                }
                """;

            case "tree-table" -> """
                {
                  "treeArea": {
                    "title": "分类树",
                    "dataUrl": "",
                    "treeField": "name"
                  },
                  "searchArea": {
                    "enabled": true,
                    "title": "查询条件",
                    "fields": []
                  },
                  "contentArea": {
                    "type": "table",
                    "configId": null,
                    "title": "数据列表",
                    "showToolbar": true
                  }
                }
                """;

            case "tabs" -> """
                {
                  "tabs": [
                    {
                      "id": "tab1",
                      "title": "Tab 1",
                      "type": "table",
                      "configId": null
                    },
                    {
                      "id": "tab2",
                      "title": "Tab 2",
                      "type": "table",
                      "configId": null
                    }
                  ]
                }
                """;

            case "dashboard" -> """
                {
                  "widgets": [
                    {
                      "id": "widget1",
                      "type": "stat-card",
                      "title": "统计卡片",
                      "position": {"x": 0, "y": 0, "w": 3, "h": 2}
                    }
                  ]
                }
                """;

            case "form" -> """
                {
                  "searchArea": {
                    "enabled": false,
                    "fields": []
                  },
                  "contentArea": {
                    "type": "form",
                    "configId": null,
                    "title": "表单",
                    "showToolbar": true
                  }
                }
                """;

            case "detail" -> """
                {
                  "searchArea": {
                    "enabled": false,
                    "fields": []
                  },
                  "contentArea": {
                    "type": "form",
                    "configId": null,
                    "title": "详情信息",
                    "showToolbar": false
                  }
                }
                """;

            case "custom" -> """
                {
                  "searchArea": {
                    "enabled": false,
                    "fields": []
                  },
                  "contentArea": {
                    "type": "custom",
                    "configId": null,
                    "title": "自定义内容",
                    "showToolbar": false
                  }
                }
                """;

            default -> """
                {
                  "searchArea": {
                    "enabled": false,
                    "fields": []
                  },
                  "contentArea": {
                    "type": "custom",
                    "configId": null,
                    "title": "自定义内容",
                    "showToolbar": false
                  }
                }
                """;
        };
    }
}
