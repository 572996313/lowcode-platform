package com.lowcode.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.entity.LowDropdownConfig;
import com.lowcode.mapper.LowDropdownConfigMapper;
import com.lowcode.service.IDropdownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 下拉配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DropdownServiceImpl extends ServiceImpl<LowDropdownConfigMapper, LowDropdownConfig> implements IDropdownService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final Pattern PARAM_PATTERN = Pattern.compile("#\\{(\\w+)\\}");

    @Override
    public PageResult<LowDropdownConfig> getDropdownList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        String configName = params.get("configName") != null ? params.get("configName").toString() : null;
        String configCode = params.get("configCode") != null ? params.get("configCode").toString() : null;
        String dropdownType = params.get("dropdownType") != null ? params.get("dropdownType").toString() : null;
        Boolean enabled = params.get("enabled") != null ? Boolean.parseBoolean(params.get("enabled").toString()) : null;

        Page<LowDropdownConfig> page = new Page<>(current, size);

        LambdaQueryWrapper<LowDropdownConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StrUtil.isNotBlank(configName), LowDropdownConfig::getConfigName, configName)
               .like(StrUtil.isNotBlank(configCode), LowDropdownConfig::getConfigCode, configCode)
               .eq(StrUtil.isNotBlank(dropdownType), LowDropdownConfig::getDropdownType, dropdownType)
               .eq(enabled != null, LowDropdownConfig::getEnabled, enabled)
               .orderByAsc(LowDropdownConfig::getSortOrder)
               .orderByDesc(LowDropdownConfig::getUpdateTime);

        Page<LowDropdownConfig> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public LowDropdownConfig getDropdownConfig(Long id) {
        LowDropdownConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("下拉配置不存在");
        }
        return config;
    }

    @Override
    public LowDropdownConfig getDropdownByCode(String configCode) {
        if (StrUtil.isBlank(configCode)) {
            throw new BusinessException("配置编码不能为空");
        }
        LowDropdownConfig config = baseMapper.selectByConfigCode(configCode);
        if (config == null) {
            throw new BusinessException("下拉配置不存在: " + configCode);
        }
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDropdown(LowDropdownConfig config) {
        // 检查编码是否重复
        if (StrUtil.isNotBlank(config.getConfigCode())) {
            long count = lambdaQuery()
                    .eq(LowDropdownConfig::getConfigCode, config.getConfigCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("配置编码已存在");
            }
        }

        // 设置默认值
        if (config.getEnabled() == null) {
            config.setEnabled(true);
        }
        if (config.getSortOrder() == null) {
            config.setSortOrder(0);
        }

        save(config);

        log.info("创建下拉配置成功, id: {}, code: {}", config.getId(), config.getConfigCode());
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDropdown(Long id, LowDropdownConfig config) {
        LowDropdownConfig existConfig = getById(id);
        if (existConfig == null) {
            throw new BusinessException("下拉配置不存在");
        }

        // 检查编码是否重复
        if (StrUtil.isNotBlank(config.getConfigCode()) && !config.getConfigCode().equals(existConfig.getConfigCode())) {
            long count = lambdaQuery()
                    .eq(LowDropdownConfig::getConfigCode, config.getConfigCode())
                    .ne(LowDropdownConfig::getId, id)
                    .count();
            if (count > 0) {
                throw new BusinessException("配置编码已存在");
            }
        }

        config.setId(id);
        updateById(config);

        log.info("更新下拉配置成功, id: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDropdown(Long id) {
        removeById(id);
        log.info("删除下拉配置成功, id: {}", id);
    }

    @Override
    public List<Map<String, Object>> testSql(LowDropdownConfig config, Map<String, Object> params) {
        if (StrUtil.isBlank(config.getSqlTemplate())) {
            throw new BusinessException("SQL模板不能为空");
        }

        try {
            String executableSql = prepareExecutableSql(config.getSqlTemplate(), params);
            return baseMapper.executeQuery(executableSql, params);
        } catch (Exception e) {
            log.error("SQL执行失败: {}", e.getMessage(), e);
            throw new BusinessException("SQL执行失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> queryDropdownData(Long configId, String configCode, Map<String, Object> params) {
        // 获取配置
        LowDropdownConfig config;
        if (configId != null) {
            config = getDropdownConfig(configId);
        } else if (StrUtil.isNotBlank(configCode)) {
            config = getDropdownByCode(configCode);
        } else {
            throw new BusinessException("必须提供 configId 或 configCode");
        }

        if (!config.getEnabled()) {
            throw new BusinessException("该下拉配置已禁用");
        }

        // 执行 SQL 查询
        List<Map<String, Object>> rawData = testSql(config, params);

        // 解析额外字段
        List<String> extraFields = new ArrayList<>();
        if (StrUtil.isNotBlank(config.getExtraFields())) {
            extraFields = Arrays.stream(config.getExtraFields().split(","))
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .toList();
        }

        // 根据下拉类型处理数据
        String dropdownType = config.getDropdownType();
        if ("tree".equals(dropdownType)) {
            return buildTreeResponse(config, rawData, extraFields);
        } else if ("cascader".equals(dropdownType)) {
            return buildCascaderResponse(config, rawData, extraFields);
        } else {
            // 默认为普通下拉
            return buildFlatResponse(config, rawData, extraFields);
        }
    }

    /**
     * 准备可执行的 SQL（将 #{paramName} 替换为 :paramName）
     */
    private String prepareExecutableSql(String template, Map<String, Object> params) {
        Matcher matcher = PARAM_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group(1);
            // 替换为 NamedParameterJdbcTemplate 的参数格式
            matcher.appendReplacement(sb, ":" + paramName);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 构建普通下拉响应
     */
    private Map<String, Object> buildFlatResponse(LowDropdownConfig config, List<Map<String, Object>> rawData, List<String> extraFields) {
        List<Map<String, Object>> options = new ArrayList<>();

        for (Map<String, Object> row : rawData) {
            Map<String, Object> option = new HashMap<>();
            String label = row.getOrDefault(config.getLabelField(), "").toString();
            Object value = row.get(config.getValueField());

            option.put("label", label);
            option.put("value", value);

            // 添加额外字段
            for (String field : extraFields) {
                option.put(field, row.get(field));
            }

            options.add(option);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labelField", config.getLabelField());
        result.put("valueField", config.getValueField());
        result.put("dropdownType", "flat");
        result.put("options", options);

        return result;
    }

    /**
     * 构建树形下拉响应
     */
    private Map<String, Object> buildTreeResponse(LowDropdownConfig config, List<Map<String, Object>> rawData, List<String> extraFields) {
        String parentField = StrUtil.isNotBlank(config.getParentField()) ? config.getParentField() : "parent_id";

        // 构建节点映射
        Map<Object, Map<String, Object>> nodeMap = new HashMap<>();
        for (Map<String, Object> row : rawData) {
            Object value = row.get(config.getValueField());
            if (value == null) continue;

            Map<String, Object> node = new HashMap<>();
            String label = row.getOrDefault(config.getLabelField(), "").toString();
            node.put("label", label);
            node.put("value", value);
            node.put("children", new ArrayList<>());

            // 添加额外字段
            for (String field : extraFields) {
                node.put(field, row.get(field));
            }

            // 保存原始父级值
            node.put("_parentId", row.get(parentField));
            nodeMap.put(value, node);
        }

        // 构建树形结构
        List<Map<String, Object>> rootNodes = new ArrayList<>();
        Set<Object> allNodeIds = new HashSet<>(nodeMap.keySet());

        for (Map.Entry<Object, Map<String, Object>> entry : nodeMap.entrySet()) {
            Object parentId = entry.getValue().get("_parentId");
            if (parentId == null || parentId.toString().isEmpty() || "0".equals(parentId.toString())) {
                rootNodes.add(entry.getValue());
            } else if (nodeMap.containsKey(parentId)) {
                Map<String, Object> parent = nodeMap.get(parentId);
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                children.add(entry.getValue());
                allNodeIds.remove(entry.getKey());
            }
        }

        // 添加没有父级引用的节点到根节点
        for (Object orphanId : allNodeIds) {
            if (nodeMap.get(orphanId).get("_parentId") != null && !nodeMap.get(orphanId).get("_parentId").toString().isEmpty()) {
                rootNodes.add(nodeMap.get(orphanId));
            }
        }

        // 移除临时字段
        for (Map<String, Object> node : nodeMap.values()) {
            node.remove("_parentId");
            if (CollUtil.isEmpty((Collection<?>) node.get("children"))) {
                node.remove("children");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labelField", config.getLabelField());
        result.put("valueField", config.getValueField());
        result.put("parentField", parentField);
        result.put("dropdownType", "tree");
        result.put("options", rootNodes);

        return result;
    }

    /**
     * 构建级联下拉响应
     */
    private Map<String, Object> buildCascaderResponse(LowDropdownConfig config, List<Map<String, Object>> rawData, List<String> extraFields) {
        // 级联通常按 level 字段分组，如果没有则按 parent 字段构建树
        String parentField = StrUtil.isNotBlank(config.getParentField()) ? config.getParentField() : "parent_id";

        // 复用树形结构构建逻辑
        Map<String, Object> treeResult = buildTreeResponse(config, rawData, extraFields);

        treeResult.put("dropdownType", "cascader");
        return treeResult;
    }
}
