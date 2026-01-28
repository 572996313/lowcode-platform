package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.LowComponentTemplate;

import java.util.List;
import java.util.Map;

/**
 * 组件模板服务接口
 */
public interface IComponentTemplateService extends IService<LowComponentTemplate> {

    /**
     * 分页查询组件模板
     */
    PageResult<LowComponentTemplate> getPageList(Map<String, Object> params);

    /**
     * 获取组件模板列表（不分页）
     */
    List<LowComponentTemplate> getList(Map<String, Object> params);

    /**
     * 根据分类获取组件模板
     */
    List<LowComponentTemplate> getByCategory(String category);

    /**
     * 根据编码获取组件模板
     */
    LowComponentTemplate getByCode(String templateCode);
}
