package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.TableStyleTemplate;

import java.util.List;
import java.util.Map;

/**
 * 表格样式模板服务接口
 */
public interface ITableStyleTemplateService extends IService<TableStyleTemplate> {

    /**
     * 分页查询模板列表
     */
    PageResult<TableStyleTemplate> getPage(Map<String, Object> params);

    /**
     * 获取所有启用的模板
     */
    List<TableStyleTemplate> getAllEnabled();

    /**
     * 根据模板编码获取模板
     */
    TableStyleTemplate getByCode(String code);
}
