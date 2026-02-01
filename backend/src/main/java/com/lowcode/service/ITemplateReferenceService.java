package com.lowcode.service;

import com.lowcode.dto.SyncRequest;
import com.lowcode.dto.SyncResult;
import com.lowcode.dto.TemplateReferenceResult;

import java.util.List;

/**
 * 模板引用服务接口
 * 负责管理模板引用关系和同步功能
 */
public interface ITemplateReferenceService {

    /**
     * 查询引用了指定模板的页面列表
     *
     * @param templateType 模板类型 (button/column/field)
     * @param templateId 模板ID
     * @return 模板引用查询结果
     */
    TemplateReferenceResult findReferences(String templateType, Long templateId);

    /**
     * 同步模板到页面
     *
     * @param templateType 模板类型
     * @param templateId 模板ID
     * @param pageIds 要同步的页面ID列表
     * @param strategy 同步策略 (merge=合并保留overwrite, replace=替换清空overwrite)
     * @return 同步结果
     */
    SyncResult syncToPages(String templateType, Long templateId, List<Long> pageIds, String strategy);

    /**
     * 批量查询模板引用关系
     *
     * @param requests 批量查询请求列表
     * @return 模板引用查询结果列表
     */
    List<TemplateReferenceResult> batchFindReferences(List<SyncRequest> requests);

    /**
     * 检查模板是否被引用
     *
     * @param templateType 模板类型
     * @param templateId 模板ID
     * @return 是否被引用
     */
    boolean isReferenced(String templateType, Long templateId);

    /**
     * 获取模板引用数量
     *
     * @param templateType 模板类型
     * @param templateId 模板ID
     * @return 引用数量
     */
    int getReferenceCount(String templateType, Long templateId);
}
