package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.entity.DbFieldWidgetTemplate;

import java.util.List;

/**
 * 字段控件配置模板 Service 接口
 *
 * @author lowcode-platform
 * @since 2025-02-01
 */
public interface IDbFieldWidgetTemplateService extends IService<DbFieldWidgetTemplate> {

    /**
     * 根据字段ID获取所有启用的模板
     *
     * @param fieldId 字段ID
     * @return 模板列表
     */
    List<DbFieldWidgetTemplate> getTemplatesByFieldId(Long fieldId);

    /**
     * 根据表ID获取所有模板
     *
     * @param tableId 表ID
     * @return 模板列表
     */
    List<DbFieldWidgetTemplate> getTemplatesByTableId(Long tableId);

    /**
     * 根据字段ID和模板编码获取模板
     *
     * @param fieldId      字段ID
     * @param templateCode 模板编码
     * @return 模板对象
     */
    DbFieldWidgetTemplate getTemplateByCode(Long fieldId, String templateCode);

    /**
     * 创建模板
     *
     * @param template 模板对象
     * @return 创建的模板
     */
    DbFieldWidgetTemplate createTemplate(DbFieldWidgetTemplate template);

    /**
     * 更新模板
     *
     * @param id       模板ID
     * @param template 模板对象
     * @return 更新的模板
     */
    DbFieldWidgetTemplate updateTemplate(Long id, DbFieldWidgetTemplate template);

    /**
     * 删除模板（逻辑删除）
     *
     * @param id 模板ID
     * @return 是否成功
     */
    boolean deleteTemplate(Long id);

    /**
     * 应用模板为主配置
     * 将模板的控件类型和配置复制到字段的主配置中
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    boolean applyTemplateAsPrimary(Long templateId);

    /**
     * 切换模板启用状态
     *
     * @param id      模板ID
     * @param enabled 启用状态
     * @return 是否成功
     */
    boolean toggleEnabled(Long id, Integer enabled);
}
