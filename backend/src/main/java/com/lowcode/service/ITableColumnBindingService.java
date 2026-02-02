package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.DbTableField;
import com.lowcode.entity.TableColumnBinding;

import java.util.List;
import java.util.Map;

/**
 * 表格列绑定服务接口
 */
public interface ITableColumnBindingService extends IService<TableColumnBinding> {

    /**
     * 分页查询表格列绑定
     */
    PageResult<TableColumnBinding> getTableColumnBindingList(Map<String, Object> params);

    /**
     * 根据表格配置ID获取列绑定列表
     */
    List<TableColumnBinding> getBindingsByTableConfigId(Long tableConfigId);

    /**
     * 根据表格配置ID获取列绑定列表（包含完整的字段信息）
     */
    List<TableColumnBinding> getBindingsWithFieldsByTableConfigId(Long tableConfigId);

    /**
     * 创建表格列绑定
     */
    Long createTableColumnBinding(TableColumnBinding binding);

    /**
     * 批量保存表格列绑定
     */
    void batchSaveBindings(Long tableConfigId, List<TableColumnBinding> bindings);

    /**
     * 更新表格列绑定
     */
    void updateTableColumnBinding(Long id, TableColumnBinding binding);

    /**
     * 删除表格列绑定
     */
    void deleteTableColumnBinding(Long id);

    /**
     * 根据数据库表ID获取可绑定的字段列表
     */
    List<DbTableField> getAvailableFieldsByTableId(Long tableId);

    /**
     * 删除表格配置的所有列绑定
     */
    void deleteBindingsByTableConfigId(Long tableConfigId);

    /**
     * 根据字段ID获取绑定配置
     */
    TableColumnBinding getBindingByTableField(Long tableConfigId, Long fieldId);
}
