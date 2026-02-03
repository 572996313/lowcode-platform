package com.lowcode.service;

import com.lowcode.dto.DictReferenceDetail;
import com.lowcode.dto.DictReferenceResult;

import java.util.List;

/**
 * 字典引用关系服务接口
 *
 * @author lowcode
 */
public interface ISysDictReferenceService {

    /**
     * 资源类型常量
     */
    String RESOURCE_TYPE_FORM_FIELD = "form_field";
    String RESOURCE_TYPE_TABLE_COLUMN = "table_column";
    String RESOURCE_TYPE_FORM_TEMPLATE = "form_template";
    String RESOURCE_TYPE_TABLE_TEMPLATE = "table_template";

    /**
     * 查找字典的所有引用
     *
     * @param dictCode 字典编码
     * @return 引用结果
     */
    DictReferenceResult findReferences(String dictCode);

    /**
     * 添加引用记录
     *
     * @param dictCategoryCode 字典分类编码
     * @param resourceType     资源类型
     * @param resourceId       资源ID
     * @param resourceName     资源名称
     * @param pageId           所属页面ID
     * @param pageName         所属页面名称
     * @param fieldCode        字段编码
     * @param fieldLabel       字段标签
     * @param referencePath    引用路径
     */
    void addReference(String dictCategoryCode, String resourceType, Long resourceId,
                      String resourceName, Long pageId, String pageName,
                      String fieldCode, String fieldLabel, String referencePath);

    /**
     * 移除引用记录
     *
     * @param dictCategoryCode 字典分类编码
     * @param resourceType     资源类型
     * @param resourceId       资源ID
     */
    void removeReference(String dictCategoryCode, String resourceType, Long resourceId);

    /**
     * 移除资源下的所有引用
     *
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     */
    void removeByResource(String resourceType, Long resourceId);

    /**
     * 更新字段的引用关系
     *
     * @param dictCategoryCode 字典分类编码
     * @param resourceType     资源类型
     * @param resourceId       资源ID
     * @param resourceName     资源名称
     * @param pageId           所属页面ID
     * @param pageName         所属页面名称
     * @param fieldCode        字段编码
     * @param fieldLabel       字段标签
     * @param referencePath    引用路径
     */
    void updateReference(String dictCategoryCode, String resourceType, Long resourceId,
                         String resourceName, Long pageId, String pageName,
                         String fieldCode, String fieldLabel, String referencePath);

    /**
     * 检查字典是否被引用
     *
     * @param dictCode 字典编码
     * @return 是否被引用
     */
    boolean isReferenced(String dictCode);

    /**
     * 获取字典的引用数量
     *
     * @param dictCode 字典编码
     * @return 引用数量
     */
    int getReferenceCount(String dictCode);

    /**
     * 重建所有引用索引（扫描所有资源并重建引用关系）
     *
     * @return 重建的引用数量
     */
    int rebuildAllReferences();

    /**
     * 清理无效引用（资源已删除的引用记录）
     *
     * @return 清理的引用数量
     */
    int cleanUnusedReferences();

    /**
     * 获取所有未被使用的字典编码列表
     *
     * @return 未使用的字典编码列表
     */
    List<String> getUnusedDictCodes();

    /**
     * 获取所有字典的引用计数
     *
     * @return Map<字典编码, 引用数量>
     */
    java.util.Map<String, Integer> getAllDictReferenceCounts();
}
