package com.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lowcode.dto.PageReference;
import com.lowcode.dto.SyncError;
import com.lowcode.dto.SyncRequest;
import com.lowcode.dto.SyncResult;
import com.lowcode.dto.TemplateReferenceResult;
import com.lowcode.entity.LowButtonConfig;
import com.lowcode.entity.LowPageConfig;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.service.ILowButtonConfigService;
import com.lowcode.service.ILowPageConfigService;
import com.lowcode.service.ITemplateReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 模板引用服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateReferenceServiceImpl implements ITemplateReferenceService {

    private final ILowPageConfigService pageConfigService;
    private final ILowButtonConfigService buttonConfigService;
    private final ObjectMapper objectMapper;

    /**
     * 查询引用了指定模板的页面列表
     */
    @Override
    public TemplateReferenceResult findReferences(String templateType, Long templateId) {
        log.info("查询模板引用关系: templateType={}, templateId={}", templateType, templateId);

        // 验证模板类型
        if (!isValidTemplateType(templateType)) {
            throw new BusinessException("无效的模板类型: " + templateType);
        }

        // 验证模板是否存在
        validateTemplateExists(templateType, templateId);

        // 获取所有页面
        List<LowPageConfig> allPages = pageConfigService.list();

        List<PageReference> references = new ArrayList<>();
        String templateName = getTemplateName(templateType, templateId);

        // 遍历所有页面，查找引用
        for (LowPageConfig page : allPages) {
            if (page.getConfigTemplate() == null || page.getConfigTemplate().isEmpty()) {
                continue;
            }

            try {
                JsonNode configNode = objectMapper.readTree(page.getConfigTemplate());
                List<PageReference> pageRefs = findReferencesInConfig(
                    configNode,
                    templateType,
                    templateId,
                    page.getId(),
                    page.getPageName(),
                    page.getPageCode(),
                    page.getUpdateTime()
                );
                references.addAll(pageRefs);
            } catch (JsonProcessingException e) {
                log.error("解析页面配置失败: pageId={}", page.getId(), e);
            }
        }

        return TemplateReferenceResult.builder()
                .templateId(templateId)
                .templateType(templateType)
                .templateName(templateName)
                .pages(references)
                .totalCount(references.size())
                .build();
    }

    /**
     * 同步模板到页面
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SyncResult syncToPages(String templateType, Long templateId, List<Long> pageIds, String strategy) {
        log.info("同步模板到页面: templateType={}, templateId={}, pageIds={}, strategy={}",
                templateType, templateId, pageIds, strategy);

        // 验证参数
        if (!isValidTemplateType(templateType)) {
            throw new BusinessException("无效的模板类型: " + templateType);
        }

        if (!"merge".equals(strategy) && !"replace".equals(strategy)) {
            throw new BusinessException("无效的同步策略: " + strategy);
        }

        // 获取模板配置
        Object templateConfig = getTemplateConfig(templateType, templateId);
        String templateConfigJson;
        try {
            templateConfigJson = objectMapper.writeValueAsString(templateConfig);
        } catch (JsonProcessingException e) {
            log.error("序列化模板配置失败", e);
            throw new BusinessException("模板配置序列化失败");
        }

        int totalCount = pageIds.size();
        int successCount = 0;
        int failedCount = 0;
        List<SyncError> errors = new ArrayList<>();

        // 逐个同步
        for (Long pageId : pageIds) {
            try {
                LowPageConfig page = pageConfigService.getById(pageId);
                if (page == null) {
                    errors.add(SyncError.builder()
                            .pageId(pageId)
                            .pageName("未知")
                            .errorMessage("页面不存在")
                            .build());
                    failedCount++;
                    continue;
                }

                // 解析页面配置
                JsonNode configNode = objectMapper.readTree(page.getConfigTemplate());

                // 同步模板到配置
                boolean updated = syncTemplateToConfig(
                        (ObjectNode) configNode,
                        templateType,
                        templateId,
                        templateConfigJson,
                        strategy
                );

                if (updated) {
                    // 更新页面配置
                    page.setConfigTemplate(objectMapper.writeValueAsString(configNode));
                    pageConfigService.updateById(page);
                    successCount++;
                    log.info("同步成功: pageId={}, pageName={}", pageId, page.getPageName());
                } else {
                    log.info("页面未引用该模板，跳过: pageId={}", pageId);
                    successCount++; // 未引用也算成功
                }

            } catch (Exception e) {
                log.error("同步失败: pageId={}", pageId, e);
                LowPageConfig page = pageConfigService.getById(pageId);
                errors.add(SyncError.builder()
                        .pageId(pageId)
                        .pageName(page != null ? page.getPageName() : "未知")
                        .errorMessage(e.getMessage())
                        .build());
                failedCount++;
            }
        }

        return SyncResult.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failedCount(failedCount)
                .errors(errors)
                .build();
    }

    /**
     * 批量查询模板引用关系
     */
    @Override
    public List<TemplateReferenceResult> batchFindReferences(List<SyncRequest> requests) {
        List<TemplateReferenceResult> results = new ArrayList<>();
        for (SyncRequest request : requests) {
            TemplateReferenceResult result = findReferences(
                    request.getTemplateType(),
                    request.getTemplateId()
            );
            results.add(result);
        }
        return results;
    }

    /**
     * 检查模板是否被引用
     */
    @Override
    public boolean isReferenced(String templateType, Long templateId) {
        return getReferenceCount(templateType, templateId) > 0;
    }

    /**
     * 获取模板引用数量
     */
    @Override
    public int getReferenceCount(String templateType, Long templateId) {
        TemplateReferenceResult result = findReferences(templateType, templateId);
        return result.getTotalCount();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证模板类型是否有效
     */
    private boolean isValidTemplateType(String templateType) {
        return "button".equals(templateType) || "column".equals(templateType) || "field".equals(templateType);
    }

    /**
     * 验证模板是否存在
     */
    private void validateTemplateExists(String templateType, Long templateId) {
        switch (templateType) {
            case "button":
                LowButtonConfig button = buttonConfigService.getById(templateId);
                if (button == null) {
                    throw new BusinessException("按钮模板不存在: " + templateId);
                }
                break;
            // TODO: column 和 field 类型验证
            default:
                break;
        }
    }

    /**
     * 获取模板名称
     */
    private String getTemplateName(String templateType, Long templateId) {
        switch (templateType) {
            case "button":
                LowButtonConfig button = buttonConfigService.getById(templateId);
                return button != null ? button.getButtonName() : "未知按钮";
            // TODO: column 和 field 类型
            default:
                return "未知模板";
        }
    }

    /**
     * 获取模板配置（用于同步）
     */
    private Object getTemplateConfig(String templateType, Long templateId) {
        switch (templateType) {
            case "button":
                LowButtonConfig button = buttonConfigService.getById(templateId);
                if (button == null) {
                    throw new BusinessException("按钮模板不存在: " + templateId);
                }
                // 转换为前端 ToolbarButton 格式
                return Map.of(
                        "id", "btn_" + templateId,
                        "name", button.getButtonName(),
                        "type", button.getButtonType() != null ? button.getButtonType() : "default",
                        "icon", button.getIcon() != null ? button.getIcon() : "",
                        "action", Map.of(
                                "type", button.getActionType() != null ? button.getActionType() : "custom",
                                "apiEndpoint", "",
                                "confirmMessage", ""
                        )
                );
            // TODO: column 和 field 类型
            default:
                throw new BusinessException("不支持的模板类型: " + templateType);
        }
    }

    /**
     * 在配置中查找引用
     */
    private List<PageReference> findReferencesInConfig(
            JsonNode configNode,
            String templateType,
            Long templateId,
            Long pageId,
            String pageName,
            String pageCode,
            Object updateTime
    ) {
        List<PageReference> references = new ArrayList<>();
        String updateTimeStr = updateTime != null
                ? ((java.time.LocalDateTime) updateTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "";

        // 递归遍历 JSON
        findReferencesRecursive(configNode, templateType, templateId, "", references);

        // 构建 PageReference 对象
        for (PageReference ref : references) {
            ref.setPageId(pageId);
            ref.setPageName(pageName);
            ref.setPageCode(pageCode);
            ref.setUpdatedAt(updateTimeStr);
        }

        return references;
    }

    /**
     * 递归查找引用
     */
    private void findReferencesRecursive(
            JsonNode node,
            String templateType,
            Long templateId,
            String currentPath,
            List<PageReference> references
    ) {
        if (node == null || node.isNull()) {
            return;
        }

        // 检查当前节点是否是对象
        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;

            // 检查是否是模板引用
            if (objNode.has("templateId") && objNode.has("templateType")) {
                JsonNode templateIdNode = objNode.get("templateId");
                JsonNode templateTypeNode = objNode.get("templateType");

                if (templateIdNode.isNumber() && templateIdNode.asLong() == templateId
                        && templateTypeNode.isTextual() && templateTypeNode.asText().equals(templateType)) {

                    boolean isLinked = objNode.has("isLinked") && objNode.get("isLinked").asBoolean();
                    references.add(PageReference.builder()
                            .referencePath(currentPath.isEmpty() ? "root" : currentPath)
                            .isLinked(isLinked)
                            .build());
                }
            }

            // 递归遍历所有字段
            Iterator<Map.Entry<String, JsonNode>> fields = objNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String newPath = currentPath.isEmpty() ? entry.getKey() : currentPath + "." + entry.getKey();
                findReferencesRecursive(entry.getValue(), templateType, templateId, newPath, references);
            }
        } else if (node.isArray()) {
            // 遍历数组
            for (int i = 0; i < node.size(); i++) {
                String newPath = currentPath + "[" + i + "]";
                findReferencesRecursive(node.get(i), templateType, templateId, newPath, references);
            }
        }
    }

    /**
     * 同步模板到配置
     * @return 是否有更新
     */
    private boolean syncTemplateToConfig(
            ObjectNode configNode,
            String templateType,
            Long templateId,
            String templateConfigJson,
            String strategy
    ) throws JsonProcessingException {
        boolean updated = false;

        // 解析模板配置
        JsonNode templateConfigNode = objectMapper.readTree(templateConfigJson);

        // 递归查找并更新引用
        updated = syncTemplateRecursive(configNode, templateType, templateId, templateConfigNode, strategy, "");

        return updated;
    }

    /**
     * 递归同步模板
     */
    private boolean syncTemplateRecursive(
            JsonNode node,
            String templateType,
            Long templateId,
            JsonNode templateConfigNode,
            String strategy,
            String currentPath
    ) {
        boolean updated = false;

        if (node == null || node.isNull()) {
            return false;
        }

        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;

            // 检查是否是目标模板引用
            if (objNode.has("templateId") && objNode.has("templateType")) {
                JsonNode templateIdNode = objNode.get("templateId");
                JsonNode templateTypeNode = objNode.get("templateType");

                if (templateIdNode.isNumber() && templateIdNode.asLong() == templateId
                        && templateTypeNode.isTextual() && templateTypeNode.asText().equals(templateType)) {

                    // 根据同步策略更新
                    if ("replace".equals(strategy)) {
                        // 替换模式：清空 overwrite，完全使用模板
                        objNode.remove("overwrite");
                        // 更新模板基础配置（保留 templateId, templateType, isLinked）
                        updateTemplateFields(objNode, templateConfigNode);
                        updated = true;
                    } else {
                        // 合并模式：保留 overwrite，更新模板基础配置
                        updateTemplateFields(objNode, templateConfigNode);
                        updated = true;
                    }

                    log.debug("更新模板引用: path={}, strategy={}", currentPath, strategy);
                    return true;
                }
            }

            // 递归遍历所有字段
            Iterator<Map.Entry<String, JsonNode>> fields = objNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String newPath = currentPath.isEmpty() ? entry.getKey() : currentPath + "." + entry.getKey();
                if (syncTemplateRecursive(entry.getValue(), templateType, templateId, templateConfigNode, strategy, newPath)) {
                    updated = true;
                }
            }
        } else if (node.isArray()) {
            // 遍历数组
            for (int i = 0; i < node.size(); i++) {
                String newPath = currentPath + "[" + i + "]";
                if (syncTemplateRecursive(node.get(i), templateType, templateId, templateConfigNode, strategy, newPath)) {
                    updated = true;
                }
            }
        }

        return updated;
    }

    /**
     * 更新模板字段（保留引用字段）
     */
    private void updateTemplateFields(ObjectNode target, JsonNode templateConfig) {
        // 复制模板配置的所有字段，除了 templateId, templateType, isLinked, overwrite
        Iterator<Map.Entry<String, JsonNode>> fields = templateConfig.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            // 跳过引用字段
            if (!"templateId".equals(key) && !"templateType".equals(key)
                    && !"isLinked".equals(key) && !"overwrite".equals(key)
                    && !"id".equals(key)) { // id 也跳过，保持原 id
                target.set(key, entry.getValue());
            }
        }
    }
}
