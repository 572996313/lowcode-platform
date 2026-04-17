package com.lowcode.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lowcode.common.Result;
import com.lowcode.dto.ApiRequest;
import com.lowcode.dto.ComponentDetailData;
import com.lowcode.entity.LowPageConfig;
import com.lowcode.mapper.LowPageConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面组件控制器
 * 按需获取副组件配置
 */
@Slf4j
@RestController
@RequestMapping("/api/page-component")
@RequiredArgsConstructor
@Tag(name = "页面组件", description = "按需获取页面中的组件配置")
public class PageComponentController {

    private final LowPageConfigMapper pageConfigMapper;

    @PostMapping("/detail")
    @Operation(summary = "获取组件配置", description = "根据 pageId 和 componentId 获取单个组件的配置信息")
    public Result<Map<String, Object>> getComponentDetail(
            @RequestBody ApiRequest<ComponentDetailData> request) {
        Long pageId = request.getPageId();
        String componentId = request.getData() != null ? request.getData().getComponentId() : null;

        if (pageId == null || componentId == null) {
            return Result.fail("pageId 和 componentId 不能为空");
        }

        LowPageConfig pageConfig = pageConfigMapper.selectById(pageId);
        if (pageConfig == null || pageConfig.getConfigJson() == null) {
            return Result.fail("页面配置不存在");
        }

        JSONObject config = JSON.parseObject(pageConfig.getConfigJson());
        JSONArray components = config.getJSONArray("components");
        if (components == null) {
            return Result.fail("页面无组件配置");
        }

        for (int i = 0; i < components.size(); i++) {
            JSONObject comp = components.getJSONObject(i);
            if (componentId.equals(comp.getString("id"))) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", comp.getString("id"));
                result.put("name", comp.getString("name"));
                result.put("type", comp.getString("type"));
                result.put("role", comp.getString("role"));
                result.put("config", comp.get("config"));
                result.put("style", comp.get("style"));
                result.put("position", comp.get("position"));
                result.put("enabled", comp.get("enabled"));
                return Result.success(result);
            }
        }

        return Result.fail("未找到组件: " + componentId);
    }
}
