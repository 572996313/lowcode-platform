package com.lowcode.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面模板解析工具类
 * 用于解析 configTemplate JSON 中的区域配置信息
 */
public class PageTemplateUtils {

    /**
     * 区域信息类
     */
    @Data
    public static class AreaInfo {
        private String id;           // 区域ID
        private String type;         // 区域类型: search, content, tree, toolbar, tabs等
        private String name;         // 区域显示名称
        private Boolean enabled;     // 是否启用
        private Boolean required;    // 是否必需（不能删除）
        private JSONObject config;   // 区域配置

        public AreaInfo() {
        }

        public AreaInfo(String id, String type, String name, Boolean enabled, Boolean required) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.enabled = enabled;
            this.required = required;
        }
    }

    /**
     * 解析配置模板获取区域列表
     *
     * @param configTemplate 配置模板JSON字符串
     * @return 区域信息列表
     */
    public static List<AreaInfo> parseAreas(String configTemplate) {
        List<AreaInfo> areas = new ArrayList<>();

        if (configTemplate == null || configTemplate.trim().isEmpty()) {
            return areas;
        }

        try {
            JSONObject config = JSON.parseObject(configTemplate);
            JSONArray areasArray = config.getJSONArray("areas");

            if (areasArray != null && !areasArray.isEmpty()) {
                for (int i = 0; i < areasArray.size(); i++) {
                    JSONObject areaObj = areasArray.getJSONObject(i);
                    AreaInfo area = new AreaInfo();
                    area.setId(areaObj.getString("id"));
                    area.setType(areaObj.getString("type"));
                    area.setName(areaObj.getString("name"));
                    area.setEnabled(areaObj.getBooleanValue("enabled"));
                    area.setRequired(areaObj.getBooleanValue("required"));
                    area.setConfig(areaObj.getJSONObject("config"));
                    areas.add(area);
                }
            }
        } catch (Exception e) {
            // JSON解析失败时返回空列表
            return areas;
        }

        return areas;
    }

    /**
     * 获取可配置区域数量（启用的且非必需的）
     *
     * @param configTemplate 配置模板JSON字符串
     * @return 可配置区域数量
     */
    public static int getConfigurableAreaCount(String configTemplate) {
        List<AreaInfo> areas = parseAreas(configTemplate);
        return (int) areas.stream()
                .filter(area -> Boolean.TRUE.equals(area.getEnabled()))
                .filter(area -> !Boolean.TRUE.equals(area.getRequired()))
                .count();
    }

    /**
     * 检查是否包含特定类型的区域
     *
     * @param configTemplate 配置模板JSON字符串
     * @param areaType 区域类型
     * @return 是否包含
     */
    public static boolean hasAreaType(String configTemplate, String areaType) {
        List<AreaInfo> areas = parseAreas(configTemplate);
        return areas.stream()
                .anyMatch(area -> areaType.equals(area.getType()));
    }

    /**
     * 获取启用的区域列表
     *
     * @param configTemplate 配置模板JSON字符串
     * @return 启用的区域列表
     */
    public static List<AreaInfo> getEnabledAreas(String configTemplate) {
        List<AreaInfo> areas = parseAreas(configTemplate);
        return areas.stream()
                .filter(area -> Boolean.TRUE.equals(area.getEnabled()))
                .toList();
    }

    /**
     * 获取必需的区域列表
     *
     * @param configTemplate 配置模板JSON字符串
     * @return 必需的区域列表
     */
    public static List<AreaInfo> getRequiredAreas(String configTemplate) {
        List<AreaInfo> areas = parseAreas(configTemplate);
        return areas.stream()
                .filter(area -> Boolean.TRUE.equals(area.getRequired()))
                .toList();
    }

    /**
     * 根据ID获取区域信息
     *
     * @param configTemplate 配置模板JSON字符串
     * @param areaId 区域ID
     * @return 区域信息，不存在时返回null
     */
    public static AreaInfo getAreaById(String configTemplate, String areaId) {
        List<AreaInfo> areas = parseAreas(configTemplate);
        return areas.stream()
                .filter(area -> areaId.equals(area.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取模板版本号
     *
     * @param configTemplate 配置模板JSON字符串
     * @return 版本号，不存在时返回1
     */
    public static int getTemplateVersion(String configTemplate) {
        if (configTemplate == null || configTemplate.trim().isEmpty()) {
            return 1;
        }

        try {
            JSONObject config = JSON.parseObject(configTemplate);
            Integer version = config.getInteger("version");
            return version != null ? version : 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * 获取模板代码
     *
     * @param configTemplate 配置模板JSON字符串
     * @return 模板代码
     */
    public static String getTemplateCode(String configTemplate) {
        if (configTemplate == null || configTemplate.trim().isEmpty()) {
            return null;
        }

        try {
            JSONObject config = JSON.parseObject(configTemplate);
            return config.getString("templateCode");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取布局类型
     *
     * @param configTemplate 配置模板JSON字符串
     * @return 布局类型
     */
    public static String getLayoutType(String configTemplate) {
        if (configTemplate == null || configTemplate.trim().isEmpty()) {
            return null;
        }

        try {
            JSONObject config = JSON.parseObject(configTemplate);
            return config.getString("layoutType");
        } catch (Exception e) {
            return null;
        }
    }
}
