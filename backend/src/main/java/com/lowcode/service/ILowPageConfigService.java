package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.LowPageConfig;

import java.util.Map;

/**
 * 页面配置服务接口
 */
public interface ILowPageConfigService extends IService<LowPageConfig> {

    /**
     * 分页查询页面配置
     */
    PageResult<LowPageConfig> getPageList(Map<String, Object> params);

    /**
     * 获取页面完整配置（包含表单、表格、按钮等）
     */
    LowPageConfig getPageConfig(Long id);

    /**
     * 创建页面配置
     */
    Long createPageConfig(LowPageConfig pageConfig);

    /**
     * 更新页面配置
     */
    void updatePageConfig(Long id, LowPageConfig pageConfig);

    /**
     * 删除页面配置
     */
    void deletePageConfig(Long id);
}
