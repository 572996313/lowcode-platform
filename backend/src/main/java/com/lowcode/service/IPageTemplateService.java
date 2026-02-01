package com.lowcode.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.entity.LowPageTemplate;

import java.util.List;

/**
 * 页面模板服务接口
 */
public interface IPageTemplateService extends IService<LowPageTemplate> {

    /**
     * 获取页面模板列表
     */
    List<LowPageTemplate> getList();

    /**
     * 分页查询页面模板
     */
    Page<LowPageTemplate> getPage(Long current, Long size, String keyword, String templateType, String layoutType);

    /**
     * 获取所有页面模板（不分页）
     */
    List<LowPageTemplate> getAll();

    /**
     * 根据模板编码获取模板
     */
    LowPageTemplate getByCode(String templateCode);
}
