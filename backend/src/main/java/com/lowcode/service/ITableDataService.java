package com.lowcode.service;

import com.lowcode.common.PageResult;
import com.lowcode.dto.ApiRequest;
import com.lowcode.dto.ComponentDetailData;
import com.lowcode.dto.PageQueryData;

import java.util.Map;

/**
 * 通用数据查询服务
 * 根据页面配置生成模拟数据，后续可扩展为查询真实数据
 */
public interface ITableDataService {

    PageResult<Map<String, Object>> queryTableData(ApiRequest<PageQueryData> request);

    /**
     * 获取单条详情数据（编辑/查看表单时调用）
     */
    Map<String, Object> getDetailData(ApiRequest<ComponentDetailData> request);

    /**
     * 保存数据（新增/编辑表单提交）
     */
    Long saveData(ApiRequest<Map<String, Object>> request);
}
