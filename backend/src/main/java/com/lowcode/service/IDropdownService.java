package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.LowDropdownConfig;

import java.util.List;
import java.util.Map;

/**
 * 下拉配置服务接口
 */
public interface IDropdownService extends IService<LowDropdownConfig> {

    /**
     * 分页查询下拉配置列表
     */
    PageResult<LowDropdownConfig> getDropdownList(Map<String, Object> params);

    /**
     * 获取下拉配置详情
     */
    LowDropdownConfig getDropdownConfig(Long id);

    /**
     * 根据配置编码获取配置
     */
    LowDropdownConfig getDropdownByCode(String configCode);

    /**
     * 创建下拉配置
     */
    Long createDropdown(LowDropdownConfig config);

    /**
     * 更新下拉配置
     */
    void updateDropdown(Long id, LowDropdownConfig config);

    /**
     * 删除下拉配置
     */
    void deleteDropdown(Long id);

    /**
     * 测试 SQL 查询，返回预览数据
     */
    List<Map<String, Object>> testSql(LowDropdownConfig config, Map<String, Object> params);

    /**
     * 执行查询并返回格式化的下拉选项
     */
    Map<String, Object> queryDropdownData(Long configId, String configCode, Map<String, Object> params);
}
