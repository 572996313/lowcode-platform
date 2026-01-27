package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.LowTableConfig;
import com.lowcode.entity.LowTableColumn;

import java.util.List;
import java.util.Map;

/**
 * 表格配置服务接口
 */
public interface ILowTableConfigService extends IService<LowTableConfig> {

    /**
     * 分页查询表格配置
     */
    PageResult<LowTableConfig> getTableList(Map<String, Object> params);

    /**
     * 获取表格完整配置（包含列配置）
     */
    LowTableConfig getTableConfig(Long id);

    /**
     * 创建表格配置
     */
    Long createTableConfig(LowTableConfig tableConfig);

    /**
     * 更新表格配置
     */
    void updateTableConfig(Long id, LowTableConfig tableConfig);

    /**
     * 删除表格配置
     */
    void deleteTableConfig(Long id);

    /**
     * 保存表格列配置
     */
    void saveTableColumns(Long tableId, List<LowTableColumn> columns);
}
