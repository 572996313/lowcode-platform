package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.PageLayout;

import java.util.Map;

/**
 * 页面布局配置服务接口
 */
public interface IPageLayoutService extends IService<PageLayout> {

    /**
     * 分页查询页面布局
     */
    PageResult<PageLayout> getPageLayoutList(Map<String, Object> params);

    /**
     * 获取页面布局详情
     */
    PageLayout getPageLayout(Long id);

    /**
     * 创建页面布局
     */
    Long createPageLayout(PageLayout pageLayout);

    /**
     * 更新页面布局
     */
    void updatePageLayout(Long id, PageLayout pageLayout);

    /**
     * 删除页面布局
     */
    void deletePageLayout(Long id);

    /**
     * 发布页面布局
     */
    void publishPageLayout(Long id);

    /**
     * 取消发布页面布局
     */
    void unpublishPageLayout(Long id);

    /**
     * 根据编码获取页面布局
     */
    PageLayout getByLayoutCode(String layoutCode);
}
