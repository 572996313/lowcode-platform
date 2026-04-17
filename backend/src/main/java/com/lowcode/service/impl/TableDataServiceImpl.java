package com.lowcode.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lowcode.common.PageResult;
import com.lowcode.dto.ApiRequest;
import com.lowcode.dto.ComponentDetailData;
import com.lowcode.dto.PageQueryData;
import com.lowcode.entity.LowPageConfig;
import com.lowcode.mapper.LowPageConfigMapper;
import com.lowcode.service.ITableDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 通用表格数据查询服务实现
 * 当前阶段：根据配置生成模拟数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TableDataServiceImpl implements ITableDataService {

    private final LowPageConfigMapper pageConfigMapper;

    @Override
    public PageResult<Map<String, Object>> queryTableData(ApiRequest<PageQueryData> request) {
        PageQueryData data = request.getData();
        int current = data != null && data.getCurrent() != null ? data.getCurrent() : 1;
        int size = data != null && data.getSize() != null ? data.getSize() : 10;
        Map<String, Object> searchParams = data != null && data.getFilters() != null ? data.getFilters() : Collections.emptyMap();

        // 尝试从 pageId 加载配置，获取列定义
        List<Map<String, Object>> columns = null;
        if (request.getPageId() != null) {
            columns = loadColumnsFromPageConfig(request.getPageId(), request.getComponentId());
        }

        // 生成模拟数据
        List<Map<String, Object>> allData = generateMockData(columns, 56);

        // 搜索过滤
        List<Map<String, Object>> filtered = filterData(allData, searchParams);

        // 分页
        int total = filtered.size();
        int fromIndex = (current - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<Map<String, Object>> records = fromIndex < total
                ? filtered.subList(fromIndex, toIndex)
                : Collections.emptyList();

        return PageResult.of(records, (long) total, (long) current, (long) size);
    }

    /**
     * 从页面配置中加载表格列定义
     */
    private List<Map<String, Object>> loadColumnsFromPageConfig(Long pageId, String componentId) {
        try {
            LowPageConfig pageConfig = pageConfigMapper.selectById(pageId);
            if (pageConfig == null || pageConfig.getConfigJson() == null) return null;

            JSONObject config = JSON.parseObject(pageConfig.getConfigJson());
            JSONArray components = config.getJSONArray("components");
            if (components == null) return null;

            // 找到目标组件
            JSONObject targetComp = null;
            for (int i = 0; i < components.size(); i++) {
                JSONObject comp = components.getJSONObject(i);
                if (componentId != null && componentId.equals(comp.getString("id"))) {
                    targetComp = comp;
                    break;
                }
                // 没有 componentId 时取第一个 table-standard
                if (targetComp == null && "table-standard".equals(comp.getString("type"))) {
                    targetComp = comp;
                }
            }

            if (targetComp == null) return null;
            JSONObject compConfig = targetComp.getJSONObject("config");
            if (compConfig == null) return null;

            JSONArray tableColumns = compConfig.getJSONArray("tableColumns");
            if (tableColumns == null) return null;

            List<Map<String, Object>> result = new ArrayList<>();
            for (int i = 0; i < tableColumns.size(); i++) {
                result.add(tableColumns.getJSONObject(i));
            }
            return result;
        } catch (Exception e) {
            log.warn("加载页面配置失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成模拟数据
     */
    private List<Map<String, Object>> generateMockData(List<Map<String, Object>> columns, int count) {
        List<Map<String, Object>> data = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", i);

            if (columns != null && !columns.isEmpty()) {
                for (Map<String, Object> col : columns) {
                    String prop = (String) col.get("prop");
                    String type = (String) col.get("type");
                    if (prop == null || "index".equals(type) || "selection".equals(type) || "action".equals(type)) continue;

                    row.put(prop, generateFieldValue(prop, i, NAMES, STATUSES, DEPARTMENTS));
                }
            } else {
                row.put("name", NAMES[i % NAMES.length]);
                row.put("status", STATUSES[i % STATUSES.length]);
                row.put("createTime", "2024-" + String.format("%02d", (i % 12) + 1) + "-" + String.format("%02d", (i % 28) + 1));
                row.put("department", DEPARTMENTS[i % DEPARTMENTS.length]);
            }

            data.add(row);
        }
        return data;
    }

    private Object generateFieldValue(String prop, int index, String[] names, String[] statuses, String[] departments) {
        String lower = prop.toLowerCase();
        if (lower.contains("name") || lower.contains("ming")) return names[index % names.length];
        if (lower.contains("status") || lower.contains("ztai")) return statuses[index % statuses.length];
        if (lower.contains("time") || lower.contains("date") || lower.contains("rq"))
            return "2024-" + String.format("%02d", (index % 12) + 1) + "-" + String.format("%02d", (index % 28) + 1);
        if (lower.contains("dept") || lower.contains("bumen") || lower.contains("depart"))
            return departments[index % departments.length];
        if (lower.contains("age") || lower.contains("nianling")) return 20 + (index % 40);
        if (lower.contains("phone") || lower.contains("tel")) return "138" + String.format("%08d", index);
        if (lower.contains("email") || lower.contains("youxiang")) return "user" + index + "@example.com";
        if (lower.contains("amount") || lower.contains("price") || lower.contains("money"))
            return Math.round(Math.random() * 10000 * 100) / 100.0;
        if (lower.contains("code") || lower.contains("bianma")) return "CODE-" + String.format("%04d", index);
        return "数据" + index;
    }

    /**
     * 搜索过滤
     */
    private List<Map<String, Object>> filterData(List<Map<String, Object>> data, Map<String, Object> searchParams) {
        if (searchParams.isEmpty()) return data;

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : data) {
            boolean match = true;
            for (Map.Entry<String, Object> entry : searchParams.entrySet()) {
                if (entry.getValue() == null || entry.getValue().toString().isEmpty()) continue;
                Object val = row.get(entry.getKey());
                if (val == null) { match = false; break; }
                if (!val.toString().contains(entry.getValue().toString())) {
                    match = false;
                    break;
                }
            }
            if (match) result.add(row);
        }
        return result;
    }

    @Override
    public Map<String, Object> getDetailData(ApiRequest<ComponentDetailData> request) {
        ComponentDetailData data = request.getData();
        String componentId = data != null ? data.getComponentId() : null;
        Object rowId = data != null ? data.getId() : null;

        // 尝试从页面配置加载字段列表
        List<Map<String, Object>> columns = null;
        if (request.getPageId() != null && componentId != null) {
            columns = loadColumnsFromPageConfig(request.getPageId(), componentId);
        }

        // 如果是表单组件，尝试从 groups 加载字段
        if (columns == null && request.getPageId() != null && componentId != null) {
            columns = loadFormFieldsFromPageConfig(request.getPageId(), componentId);
        }

        // 生成 mock 详情数据
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", rowId != null ? rowId : 1);

        if (columns != null && !columns.isEmpty()) {
            for (Map<String, Object> col : columns) {
                String prop = (String) col.get("prop");
                String field = (String) col.get("field");
                String key = prop != null ? prop : field;
                if (key == null) continue;
                detail.put(key, generateFieldValue(key, rowId != null ? Integer.parseInt(rowId.toString()) : 1,
                        NAMES, STATUSES, DEPARTMENTS));
            }
        }

        return detail;
    }

    @Override
    public Long saveData(ApiRequest<Map<String, Object>> request) {
        Map<String, Object> data = request.getData();
        log.info("保存数据: pageId={}, componentId={}, data={}", request.getPageId(), request.getComponentId(), data);

        // 当前阶段 mock：有 id 返回 id，没 id 返回新 ID
        if (data != null && data.get("id") != null) {
            return Long.valueOf(data.get("id").toString());
        }
        return System.currentTimeMillis();
    }

    private static final String[] NAMES = {"张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
            "郑十一", "冯十二", "陈小明", "林小红", "黄大伟", "刘芳", "杨洋", "许静"};
    private static final String[] STATUSES = {"active", "inactive", "pending"};
    private static final String[] DEPARTMENTS = {"技术部", "产品部", "市场部", "财务部", "人力资源部"};

    /**
     * 从页面配置中加载表单字段定义
     */
    private List<Map<String, Object>> loadFormFieldsFromPageConfig(Long pageId, String componentId) {
        try {
            LowPageConfig pageConfig = pageConfigMapper.selectById(pageId);
            if (pageConfig == null || pageConfig.getConfigJson() == null) return null;

            JSONObject config = JSON.parseObject(pageConfig.getConfigJson());
            JSONArray components = config.getJSONArray("components");
            if (components == null) return null;

            JSONObject targetComp = null;
            for (int i = 0; i < components.size(); i++) {
                JSONObject comp = components.getJSONObject(i);
                if (componentId.equals(comp.getString("id"))) {
                    targetComp = comp;
                    break;
                }
            }
            if (targetComp == null) return null;

            JSONObject compConfig = targetComp.getJSONObject("config");
            if (compConfig == null) return null;

            JSONArray groups = compConfig.getJSONArray("groups");
            if (groups == null) return null;

            List<Map<String, Object>> result = new ArrayList<>();
            for (int i = 0; i < groups.size(); i++) {
                JSONArray fields = groups.getJSONObject(i).getJSONArray("fields");
                if (fields == null) continue;
                for (int j = 0; j < fields.size(); j++) {
                    result.add(fields.getJSONObject(j));
                }
            }
            return result;
        } catch (Exception e) {
            log.warn("加载表单字段失败: {}", e.getMessage());
            return null;
        }
    }
}
