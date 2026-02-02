package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.FormStyleTemplate;

import java.util.List;
import java.util.Map;

/**
 * 表单样式模板服务接口
 */
public interface IFormStyleTemplateService extends IService<FormStyleTemplate> {

    /**
     * 分页查询模板列表
     */
    PageResult<FormStyleTemplate> getPage(Map<String, Object> params);

    /**
     * 获取所有启用的模板
     */
    List<FormStyleTemplate> getAllEnabled();

    /**
     * 根据模板编码获取模板
     */
    FormStyleTemplate getByCode(String code);
}
