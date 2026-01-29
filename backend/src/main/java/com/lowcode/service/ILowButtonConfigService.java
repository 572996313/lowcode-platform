package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.LowButtonConfig;

import java.util.List;
import java.util.Map;

/**
 * 按钮配置服务接口
 */
public interface ILowButtonConfigService extends IService<LowButtonConfig> {

    /**
     * 分页查询按钮配置
     */
    PageResult<LowButtonConfig> getButtonList(Map<String, Object> params);

    /**
     * 根据页面ID查询所有按钮
     */
    List<LowButtonConfig> getButtonsByPageId(Long pageId);

    /**
     * 根据表单ID查询按钮
     */
    List<LowButtonConfig> getButtonsByFormId(Long formId);

    /**
     * 根据表格ID查询按钮（工具栏和操作列）
     */
    List<LowButtonConfig> getButtonsByTableId(Long tableId);

    /**
     * 创建按钮配置
     */
    Long createButtonConfig(LowButtonConfig buttonConfig);

    /**
     * 更新按钮配置
     */
    void updateButtonConfig(Long id, LowButtonConfig buttonConfig);

    /**
     * 删除按钮配置
     */
    void deleteButtonConfig(Long id);

    /**
     * 批量保存按钮配置
     */
    void batchSaveButtons(Long pageId, List<LowButtonConfig> buttons);

    /**
     * 按表单ID批量保存按钮配置
     */
    void batchSaveButtonsByFormId(Long formId, List<LowButtonConfig> buttons);

    /**
     * 按表格ID批量保存按钮配置
     */
    void batchSaveButtonsByTableId(Long tableId, List<LowButtonConfig> buttons);
}
