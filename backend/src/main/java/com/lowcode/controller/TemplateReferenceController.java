package com.lowcode.controller;

import com.lowcode.common.Result;
import com.lowcode.dto.SyncRequest;
import com.lowcode.dto.SyncResult;
import com.lowcode.dto.TemplateReferenceResult;
import com.lowcode.service.ITemplateReferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模板引用控制器
 * 负责处理模板引用查询和同步功能
 */
@Slf4j
@RestController
@RequestMapping("/api/template")
@RequiredArgsConstructor
@Tag(name = "模板引用管理", description = "模板引用查询和同步相关接口")
public class TemplateReferenceController {

    private final ITemplateReferenceService templateReferenceService;

    /**
     * 查询引用了指定模板的页面列表
     *
     * @param templateType 模板类型 (button/column/field)
     * @param templateId 模板ID
     * @return 模板引用查询结果
     */
    @GetMapping("/{templateType}/{templateId}/references")
    @Operation(summary = "查询模板引用", description = "查询引用了指定模板的页面列表")
    public Result<TemplateReferenceResult> getTemplateReferences(
            @Parameter(description = "模板类型(button/column/field)", required = true)
            @PathVariable String templateType,
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long templateId
    ) {
        log.info("查询模板引用: templateType={}, templateId={}", templateType, templateId);
        TemplateReferenceResult result = templateReferenceService.findReferences(templateType, templateId);
        return Result.success(result);
    }

    /**
     * 同步模板到页面
     *
     * @param templateType 模板类型
     * @param templateId 模板ID
     * @param request 同步请求参数
     * @return 同步结果
     */
    @PostMapping("/{templateType}/{templateId}/sync")
    @Operation(summary = "同步模板到页面", description = "将模板配置同步到引用它的页面")
    public Result<SyncResult> syncTemplateToPages(
            @Parameter(description = "模板类型", required = true)
            @PathVariable String templateType,
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long templateId,
            @RequestBody SyncRequest request
    ) {
        log.info("同步模板到页面: templateType={}, templateId={}, request={}", templateType, templateId, request);
        SyncResult result = templateReferenceService.syncToPages(
                templateType,
                templateId,
                request.getPageIds(),
                request.getStrategy()
        );
        return Result.success(result);
    }

    /**
     * 批量查询模板引用关系
     *
     * @param requests 批量查询请求列表
     * @return 模板引用查询结果列表
     */
    @PostMapping("/references/batch")
    @Operation(summary = "批量查询模板引用", description = "批量查询多个模板的引用关系")
    public Result<List<TemplateReferenceResult>> batchGetTemplateReferences(
            @RequestBody List<SyncRequest> requests
    ) {
        log.info("批量查询模板引用: requests={}", requests);
        List<TemplateReferenceResult> results = templateReferenceService.batchFindReferences(requests);
        return Result.success(results);
    }

    /**
     * 检查模板是否被引用
     *
     * @param templateType 模板类型
     * @param templateId 模板ID
     * @return 是否被引用
     */
    @GetMapping("/{templateType}/{templateId}/is-referenced")
    @Operation(summary = "检查模板是否被引用", description = "检查指定模板是否被页面引用")
    public Result<Boolean> isReferenced(
            @Parameter(description = "模板类型", required = true)
            @PathVariable String templateType,
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long templateId
    ) {
        log.info("检查模板是否被引用: templateType={}, templateId={}", templateType, templateId);
        boolean isReferenced = templateReferenceService.isReferenced(templateType, templateId);
        return Result.success(isReferenced);
    }

    /**
     * 获取模板引用数量
     *
     * @param templateType 模板类型
     * @param templateId 模板ID
     * @return 引用数量
     */
    @GetMapping("/{templateType}/{templateId}/reference-count")
    @Operation(summary = "获取模板引用数量", description = "获取引用指定模板的页面数量")
    public Result<Integer> getReferenceCount(
            @Parameter(description = "模板类型", required = true)
            @PathVariable String templateType,
            @Parameter(description = "模板ID", required = true)
            @PathVariable Long templateId
    ) {
        log.info("获取模板引用数量: templateType={}, templateId={}", templateType, templateId);
        int count = templateReferenceService.getReferenceCount(templateType, templateId);
        return Result.success(count);
    }
}
