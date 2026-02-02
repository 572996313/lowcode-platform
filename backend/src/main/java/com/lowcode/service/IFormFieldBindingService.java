package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.DbTableField;
import com.lowcode.entity.FormFieldBinding;

import java.util.List;
import java.util.Map;

/**
 * 表单字段绑定服务接口
 */
public interface IFormFieldBindingService extends IService<FormFieldBinding> {

    /**
     * 分页查询表单字段绑定
     */
    PageResult<FormFieldBinding> getFormFieldBindingList(Map<String, Object> params);

    /**
     * 根据表单ID获取字段绑定列表（包含字段信息）
     */
    List<FormFieldBinding> getBindingsByFormId(Long formId);

    /**
     * 根据表单ID获取字段绑定列表（包含完整的字段信息）
     */
    List<FormFieldBinding> getBindingsWithFieldsByFormId(Long formId);

    /**
     * 创建表单字段绑定
     */
    Long createFormFieldBinding(FormFieldBinding binding);

    /**
     * 批量保存表单字段绑定
     */
    void batchSaveBindings(Long formId, List<FormFieldBinding> bindings);

    /**
     * 更新表单字段绑定
     */
    void updateFormFieldBinding(Long id, FormFieldBinding binding);

    /**
     * 删除表单字段绑定
     */
    void deleteFormFieldBinding(Long id);

    /**
     * 根据数据库表ID获取可绑定的字段列表
     */
    List<DbTableField> getAvailableFieldsByTableId(Long tableId);

    /**
     * 删除表单的所有字段绑定
     */
    void deleteBindingsByFormId(Long formId);

    /**
     * 根据字段ID获取绑定配置
     */
    FormFieldBinding getBindingByFormField(Long formId, Long fieldId);
}
