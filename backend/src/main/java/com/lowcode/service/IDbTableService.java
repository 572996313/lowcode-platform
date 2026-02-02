package com.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.common.PageResult;
import com.lowcode.entity.DbTable;
import com.lowcode.entity.DbTableField;

import java.util.List;
import java.util.Map;

/**
 * 库表管理服务接口
 */
public interface IDbTableService extends IService<DbTable> {

    /**
     * 分页查询库表列表
     */
    PageResult<DbTable> getTableList(Map<String, Object> params);

    /**
     * 获取库表详情（包含字段列表）
     */
    DbTable getTableDetail(Long id);

    /**
     * 获取表的字段列表
     */
    List<DbTableField> getTableFields(Long tableId);

    /**
     * 扫描并更新所有数据库对象（表和视图）
     */
    void scanAllTables();

    /**
     * 同步指定表的字段信息
     */
    void syncTableFields(Long tableId);

    /**
     * 更新库表信息
     */
    void updateTable(Long id, DbTable dbTable);

    /**
     * 更新字段显示名称
     */
    void updateFieldLabel(Long fieldId, String fieldLabel);

    /**
     * 批量更新字段标签
     */
    void batchUpdateFieldLabels(List<Map<String, Object>> fieldUpdates);

    /**
     * 删除库表（级联删除字段）
     */
    void deleteTable(Long id);

    /**
     * 设置表的分组
     */
    void setTableGroup(Long tableId, Long groupId);

    /**
     * 批量设置表的分组
     */
    void batchSetTableGroup(List<Long> tableIds, Long groupId);
}
