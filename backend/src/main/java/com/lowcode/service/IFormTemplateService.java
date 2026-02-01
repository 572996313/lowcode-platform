package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.LowFormTemplate;

import java.util.List;
import java.util.Map;

/**
 * 表单模板服务接口
 */
public interface IFormTemplateService extends IService<LowFormTemplate> {

    /**
     * 分页查询模板列表
     */
    PageResult<LowFormTemplate> getPage(Map<String, Object> params);

    /**
     * 获取所有启用的模板
     */
    List<LowFormTemplate> getAll();

    /**
     * 根据模板编码获取模板
     */
    LowFormTemplate getByCode(String templateCode);
}