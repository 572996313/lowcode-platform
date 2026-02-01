package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.DbFieldWidgetConfig;

import java.util.List;
import java.util.Map;

/**
 * 字段-控件绑定配置服务接口
 */
public interface IDbFieldWidgetConfigService extends IService<DbFieldWidgetConfig> {

    /**
     * 分页查询绑定配置列表
     */
    PageResult<DbFieldWidgetConfig> getConfigList(Map<String, Object> params);

    /**
     * 获取配置详情
     */
    DbFieldWidgetConfig getConfigDetail(Long id);

    /**
     * 获取表的所有绑定配置
     */
    List<DbFieldWidgetConfig> getConfigsByTableId(Long tableId);

    /**
     * 获取字段的绑定配置
     */
    DbFieldWidgetConfig getConfigByFieldId(Long fieldId);

    /**
     * 获取所有默认映射规则
     */
    List<DbFieldWidgetConfig> getDefaultMappings();

    /**
     * 根据字段类型获取默认映射规则
     */
    DbFieldWidgetConfig getDefaultMappingByFieldType(String fieldType);

    /**
     * 创建绑定配置
     */
    void createConfig(DbFieldWidgetConfig config);

    /**
     * 更新绑定配置
     */
    void updateConfig(Long id, DbFieldWidgetConfig config);

    /**
     * 删除绑定配置
     */
    void deleteConfig(Long id);

    /**
     * 批量保存表的字段绑定配置
     */
    void batchSaveConfigs(Long tableId, List<DbFieldWidgetConfig> configs);

    /**
     * 应用默认映射到表字段
     */
    void applyDefaultsToTable(Long tableId);

    /**
     * 初始化系统默认映射规则
     */
    void initDefaultMappings();
}
