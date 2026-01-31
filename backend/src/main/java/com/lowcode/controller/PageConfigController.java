package com.lowcode.controller;

import com.lowcode.common.PageResult;
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

import java.util.Map;

/**
 * 页面配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/page")
@RequiredArgsConstructor
@Tag(name = "页面配置", description = "页面配置相关接口")
public class PageConfigController {

    private final ILowPageConfigService pageConfigService;
    private final IPageTemplateService pageTemplateService;

    @GetMapping("/list")
    @Operation(summary = "分页查询页面配置")
    public Result<PageResult<LowPageConfig>> getPageList(@RequestParam Map<String, Object> params) {
        PageResult<LowPageConfig> result = pageConfigService.getPageList(params);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取页面配置详情")
    public Result<LowPageConfig> getPageConfig(@PathVariable Long id) {
        LowPageConfig pageConfig = pageConfigService.getPageConfig(id);
        return Result.success(pageConfig);
    }

    @PostMapping
    @Operation(summary = "创建页面配置")
    public Result<Long> createPageConfig(@RequestBody LowPageConfig pageConfig) {
        Long id = pageConfigService.createPageConfig(pageConfig);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新页面配置")
    public Result<Void> updatePageConfig(@PathVariable Long id, @RequestBody LowPageConfig pageConfig) {
        pageConfigService.updatePageConfig(id, pageConfig);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除页面配置")
    public Result<Void> deletePageConfig(@PathVariable Long id) {
        pageConfigService.deletePageConfig(id);
        return Result.success();
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布页面")
    public Result<Long> publishPage(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String routePath = request.get("routePath");
        Long pageId = pageConfigService.publishPage(id, routePath);
        return Result.success(pageId);
    }

    @PostMapping("/{id}/unpublish")
    @Operation(summary = "取消发布页面")
    public Result<Void> unpublishPage(@PathVariable Long id) {
        pageConfigService.unpublishPage(id);
        return Result.success();
    }

    @GetMapping("/published")
    @Operation(summary = "获取已发布的页面列表")
    public Result<java.util.List<LowPageConfig>> getPublishedPages() {
        java.util.List<LowPageConfig> pages = pageConfigService.getPublishedPages();
        return Result.success(pages);
    }

    @PostMapping("/{id}/switch-template")
    @Operation(summary = "切换页面模板")
    public Result<Void> switchTemplate(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Long templateId = ((Number) params.get("templateId")).longValue();
        Boolean keepConfig = params.get("keepConfig") != null ? (Boolean) params.get("keepConfig") : false;

        // 获取新模板
        LowPageTemplate template = pageTemplateService.getById(templateId);
        if (template == null) {
            return Result.fail("模板不存在");
        }

        // 获取当前页面配置
        LowPageConfig pageConfig = pageConfigService.getPageConfig(id);
        if (pageConfig == null) {
            return Result.fail("页面不存在");
        }

        // 更新页面配置
        pageConfig.setTemplateId(templateId);
        pageConfig.setLayoutType(template.getLayoutType());
        pageConfig.setPageType(template.getTemplateType());

        // 如果不保留配置，则使用新模板的配置
        if (!keepConfig) {
            String configTemplate = template.getConfigTemplate();
            if (configTemplate != null && !configTemplate.isEmpty()) {
                pageConfig.setConfigTemplate(configTemplate);
                pageConfig.setConfigJson(configTemplate);
            }
        }

        // 如果页面已发布，取消发布状态
        if (pageConfig.getPublished() != null && pageConfig.getPublished()) {
            pageConfig.setPublished(false);
            pageConfig.setRoutePath(null);
            pageConfig.setPublishTime(null);
        }

        pageConfigService.updatePageConfig(id, pageConfig);

        log.info("切换页面模板成功, pageId: {}, oldTemplateId: {}, newTemplateId: {}, keepConfig: {}",
                 id, pageConfig.getTemplateId(), templateId, keepConfig);

        return Result.success();
    }
}
