package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.LowFormConfig;
import com.lowcode.entity.LowFormField;

import java.util.List;
import java.util.Map;

/**
 * 表单配置服务接口
 */
public interface ILowFormConfigService extends IService<LowFormConfig> {

    /**
     * 分页查询表单配置
     */
    PageResult<LowFormConfig> getFormList(Map<String, Object> params);

    /**
     * 获取表单完整配置（包含字段列表）
     */
    LowFormConfig getFormConfig(Long id);

    /**
     * 创建表单配置
     */
    Long createFormConfig(LowFormConfig formConfig);

    /**
     * 更新表单配置
     */
    void updateFormConfig(Long id, LowFormConfig formConfig);

    /**
     * 删除表单配置
     */
    void deleteFormConfig(Long id);

    /**
     * 保存表单字段
     */
    void saveFormFields(Long formId, List<LowFormField> fields);

    /**
     * 根据组件分类获取表单列表
     */
    List<LowFormConfig> getByCategory(String category);

    /**
     * 统计指定分类的表单数量
     */
    Long countByCategory(String category);
}
