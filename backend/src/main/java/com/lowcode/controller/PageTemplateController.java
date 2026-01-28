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

        // 创建页面配置
        LowPageConfig pageConfig = LowPageConfig.builder()
                .pageName(pageName)
                .pageCode(pageCode)
                .pageType(template.getTemplateType())
                .layoutType(template.getLayoutType())
                .templateId(template.getId())
                .configJson(template.getConfigTemplate())
                .configTemplate(template.getConfigTemplate())
                .status(true)
                .build();

        Long pageId = pageConfigService.createPageConfig(pageConfig);
        log.info("从模板创建页面成功, templateCode: {}, pageId: {}", templateCode, pageId);

        return Result.success(pageId);
    }
}
