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
import com.lowcode.entity.DbFieldWidgetConfig;
import com.lowcode.entity.DbTableField;
import com.lowcode.mapper.DbFieldWidgetConfigMapper;
import com.lowcode.mapper.DbTableFieldMapper;
import com.lowcode.service.IDbFieldWidgetConfigService;
import com.lowcode.service.IDbTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 字段-控件绑定配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DbFieldWidgetConfigServiceImpl extends ServiceImpl<DbFieldWidgetConfigMapper, DbFieldWidgetConfig>
        implements IDbFieldWidgetConfigService {

    private final DbTableFieldMapper dbTableFieldMapper;
    private final IDbTableService dbTableService;

    @Override
    public PageResult<DbFieldWidgetConfig> getConfigList(Map<String, Object> params) {
        Integer current = params.get("current") != null ? Integer.parseInt(params.get("current").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;
        Long tableId = params.get("tableId") != null ? Long.parseLong(params.get("tableId").toString()) : null;
        String widgetType = params.get("widgetType") != null ? params.get("widgetType").toString() : null;
        Integer enabled = params.get("enabled") != null ? Integer.parseInt(params.get("enabled").toString()) : null;

        Page<DbFieldWidgetConfig> page = new Page<>(current, size);

        LambdaQueryWrapper<DbFieldWidgetConfig> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(tableId != null, DbFieldWidgetConfig::getTableId, tableId)
               .like(StrUtil.isNotBlank(widgetType), DbFieldWidgetConfig::getWidgetType, widgetType)
               .eq(enabled != null, DbFieldWidgetConfig::getEnabled, enabled)
               .orderByAsc(DbFieldWidgetConfig::getIsDefault)
               .orderByDesc(DbFieldWidgetConfig::getUpdateTime);

        Page<DbFieldWidgetConfig> result = page(page, wrapper);

        return PageResult.of(result);
    }

    @Override
    public DbFieldWidgetConfig getConfigDetail(Long id) {
        DbFieldWidgetConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        return config;
    }

    @Override
    public List<DbFieldWidgetConfig> getConfigsByTableId(Long tableId) {
        return list(Wrappers.lambdaQuery(DbFieldWidgetConfig.class)
                .eq(DbFieldWidgetConfig::getTableId, tableId)
                .eq(DbFieldWidgetConfig::getIsDefault, 0)
                .orderByAsc(DbFieldWidgetConfig::getSortOrder));
    }

    @Override
    public DbFieldWidgetConfig getConfigByFieldId(Long fieldId) {
        return getOne(Wrappers.lambdaQuery(DbFieldWidgetConfig.class)
                .eq(DbFieldWidgetConfig::getFieldId, fieldId));
    }

    @Override
    public List<DbFieldWidgetConfig> getDefaultMappings() {
        return list(Wrappers.lambdaQuery(DbFieldWidgetConfig.class)
                .eq(DbFieldWidgetConfig::getIsDefault, 1)
                .eq(DbFieldWidgetConfig::getEnabled, 1)
                .orderByAsc(DbFieldWidgetConfig::getSortOrder));
    }

    @Override
    public DbFieldWidgetConfig getDefaultMappingByFieldType(String fieldType) {
        // 标准化字段类型（去除括号及内容）
        String normalizedType = normalizeFieldType(fieldType);

        // 精确匹配
        DbFieldWidgetConfig config = getOne(Wrappers.lambdaQuery(DbFieldWidgetConfig.class)
                .eq(DbFieldWidgetConfig::getIsDefault, 1)
                .eq(DbFieldWidgetConfig::getEnabled, 1)
                .eq(DbFieldWidgetConfig::getDefaultForFieldType, normalizedType)
                .last("LIMIT 1"));

        if (config != null) {
            return config;
        }

        // 模糊匹配（类型前缀）
        return getOne(Wrappers.lambdaQuery(DbFieldWidgetConfig.class)
                .eq(DbFieldWidgetConfig::getIsDefault, 1)
                .eq(DbFieldWidgetConfig::getEnabled, 1)
                .likeRight(DbFieldWidgetConfig::getDefaultForFieldType, normalizedType)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createConfig(DbFieldWidgetConfig config) {
        validateConfig(config);
        save(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(Long id, DbFieldWidgetConfig config) {
        DbFieldWidgetConfig existing = getById(id);
        if (existing == null) {
            throw new BusinessException("配置不存在");
        }

        // 默认映射规则不允许修改关键字段
        if (existing.getIsDefault() == 1) {
            config.setIsDefault(1);
            config.setDefaultForFieldType(existing.getDefaultForFieldType());
        }

        validateConfig(config);
        config.setId(id);
        updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        DbFieldWidgetConfig config = getById(id);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }

        // 默认映射规则不允许删除
        if (config.getIsDefault() == 1) {
            throw new BusinessException("默认映射规则不允许删除");
        }

        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveConfigs(Long tableId, List<DbFieldWidgetConfig> configs) {
        if (CollUtil.isEmpty(configs)) {
            return;
        }

        // 验证表存在
        dbTableService.getById(tableId);

        // 删除表的所有现有配置
        remove(Wrappers.lambdaQuery(DbFieldWidgetConfig.class)
                .eq(DbFieldWidgetConfig::getTableId, tableId)
                .eq(DbFieldWidgetConfig::getIsDefault, 0));

        // 批量保存新配置
        configs.forEach(config -> {
            config.setTableId(tableId);
            config.setIsDefault(0);
            config.setEnabled(1);
        });

        saveBatch(configs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyDefaultsToTable(Long tableId) {
        // 验证表存在
        var table = dbTableService.getById(tableId);
        if (table == null) {
            throw new BusinessException("表不存在");
        }

        // 获取表的所有字段
        List<DbTableField> fields = dbTableService.getTableFields(tableId);
        if (CollUtil.isEmpty(fields)) {
            throw new BusinessException("表无字段，请先同步字段");
        }

        // 获取所有默认映射规则
        List<DbFieldWidgetConfig> defaultMappings = getDefaultMappings();
        if (CollUtil.isEmpty(defaultMappings)) {
            throw new BusinessException("默认映射规则不存在，请先初始化");
        }

        // 为每个字段应用默认映射
        List<DbFieldWidgetConfig> configs = new ArrayList<>();
        for (DbTableField field : fields) {
            DbFieldWidgetConfig defaultMapping = getDefaultMappingByFieldType(field.getFieldType());
            if (defaultMapping == null) {
                // 如果没有匹配的映射，使用 input 作为默认
                defaultMapping = getDefaultMappingByFieldType("VARCHAR");
            }

            // 创建字段绑定配置
            DbFieldWidgetConfig config = DbFieldWidgetConfig.builder()
                    .tableId(tableId)
                    .fieldId(field.getId())
                    .widgetType(defaultMapping.getWidgetType())
                    .widgetConfig(generateFieldConfig(defaultMapping, field))
                    .isDefault(0)
                    .enabled(1)
                    .sortOrder(field.getOrdinalPosition())
                    .build();

            configs.add(config);
        }

        // 删除表的现有配置
        remove(Wrappers.lambdaQuery(DbFieldWidgetConfig.class)
                .eq(DbFieldWidgetConfig::getTableId, tableId)
                .eq(DbFieldWidgetConfig::getIsDefault, 0));

        // 保存新配置
        saveBatch(configs);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initDefaultMappings() {
        log.info("开始初始化默认映射规则...");

        // 先删除所有默认映射规则
        remove(Wrappers.lambdaQuery(DbFieldWidgetConfig.class)
                .eq(DbFieldWidgetConfig::getIsDefault, 1));

        List<DbFieldWidgetConfig> mappings = new ArrayList<>();

        // 字符串类型
        mappings.add(createDefaultMapping(1, "VARCHAR", "input",
                "{\"placeholder\":\"请输入...\",\"required\":false,\"span\":12}"));
        mappings.add(createDefaultMapping(2, "TEXT", "textarea",
                "{\"placeholder\":\"请输入...\",\"rows\":3,\"required\":false,\"span\":24}"));
        mappings.add(createDefaultMapping(3, "CHAR", "input",
                "{\"placeholder\":\"请输入...\",\"required\":false,\"span\":12}"));
        mappings.add(createDefaultMapping(4, "ENUM", "select",
                "{\"placeholder\":\"请选择...\",\"required\":false,\"span\":12,\"options\":[]}"));

        // 数字类型
        mappings.add(createDefaultMapping(5, "INT", "number",
                "{\"placeholder\":\"请输入数字\",\"required\":false,\"span\":12}"));
        mappings.add(createDefaultMapping(6, "BIGINT", "number",
                "{\"placeholder\":\"请输入数字\",\"required\":false,\"span\":12}"));
        mappings.add(createDefaultMapping(7, "DECIMAL", "number",
                "{\"placeholder\":\"请输入数字\",\"required\":false,\"span\":12}"));
        mappings.add(createDefaultMapping(8, "FLOAT", "number",
                "{\"placeholder\":\"请输入数字\",\"required\":false,\"span\":12}"));
        mappings.add(createDefaultMapping(9, "DOUBLE", "number",
                "{\"placeholder\":\"请输入数字\",\"required\":false,\"span\":12}"));

        // 日期时间类型
        mappings.add(createDefaultMapping(10, "DATE", "date",
                "{\"placeholder\":\"请选择日期\",\"required\":false,\"span\":12,\"format\":\"YYYY-MM-DD\"}"));
        mappings.add(createDefaultMapping(11, "DATETIME", "datetime",
                "{\"placeholder\":\"请选择日期时间\",\"required\":false,\"span\":12}"));
        mappings.add(createDefaultMapping(12, "TIMESTAMP", "datetime",
                "{\"placeholder\":\"请选择日期时间\",\"required\":false,\"span\":12}"));
        mappings.add(createDefaultMapping(13, "TIME", "time",
                "{\"placeholder\":\"请选择时间\",\"required\":false,\"span\":12}"));

        // 布尔类型
        mappings.add(createDefaultMapping(14, "BOOLEAN", "switch",
                "{\"required\":false,\"span\":12}"));
        mappings.add(createDefaultMapping(15, "TINYINT", "switch",
                "{\"required\":false,\"span\":12}"));

        saveBatch(mappings);

        log.info("默认映射规则初始化完成，共 {} 条", mappings.size());
    }

    /**
     * 标准化字段类型
     */
    private String normalizeFieldType(String fieldType) {
        if (StrUtil.isBlank(fieldType)) {
            return "VARCHAR";
        }
        // 去除括号及内容，转大写
        return fieldType.split("\\(")[0].toUpperCase();
    }

    /**
     * 创建默认映射
     */
    private DbFieldWidgetConfig createDefaultMapping(Integer sortOrder, String defaultForFieldType,
                                                      String widgetType, String widgetConfig) {
        return DbFieldWidgetConfig.builder()
                .isDefault(1)
                .defaultForFieldType(defaultForFieldType)
                .widgetType(widgetType)
                .widgetConfig(widgetConfig)
                .enabled(1)
                .sortOrder(sortOrder)
                .build();
    }

    /**
     * 根据字段生成控件配置
     */
    private String generateFieldConfig(DbFieldWidgetConfig defaultMapping, DbTableField field) {
        // 复制默认配置
        JSONObject config = JSONUtil.parseObj(defaultMapping.getWidgetConfig());

        // 设置显示标签（优先使用用户编辑的标签）
        String label = StrUtil.isNotBlank(field.getFieldLabel()) ? field.getFieldLabel() : field.getFieldComment();
        if (StrUtil.isBlank(label)) {
            label = field.getFieldName();
        }
        config.set("label", label);

        // 设置必填（根据字段是否可空）
        config.set("required", field.getIsNullable() == 0);

        // 设置占位提示
        String placeholder = "请输入" + label;
        if ("select".equals(defaultMapping.getWidgetType()) ||
            "radio".equals(defaultMapping.getWidgetType()) ||
            "date".equals(defaultMapping.getWidgetType()) ||
            "datetime".equals(defaultMapping.getWidgetType())) {
            placeholder = "请选择" + label;
        }
        config.set("placeholder", placeholder);

        // 如果字段有默认值，设置默认值
        if (StrUtil.isNotBlank(field.getColumnDefault())) {
            config.set("defaultValue", field.getColumnDefault());
        }

        return config.toString();
    }

    /**
     * 验证配置
     */
    private void validateConfig(DbFieldWidgetConfig config) {
        if (StrUtil.isBlank(config.getWidgetType())) {
            throw new BusinessException("控件类型不能为空");
        }

        // 支持的控件类型列表
        List<String> supportedTypes = Arrays.asList(
                "input", "textarea", "select", "radio", "checkbox",
                "switch", "date", "datetime", "time", "number",
                "upload", "cascader"
        );

        if (!supportedTypes.contains(config.getWidgetType())) {
            throw new BusinessException("不支持的控件类型: " + config.getWidgetType());
        }

        // 验证默认映射规则
        if (config.getIsDefault() != null && config.getIsDefault() == 1) {
            if (StrUtil.isBlank(config.getDefaultForFieldType())) {
                throw new BusinessException("默认映射规则必须指定字段类型");
            }
        } else {
            // 非默认映射规则必须有表ID或字段ID
            if (config.getTableId() == null && config.getFieldId() == null) {
                throw new BusinessException("配置必须关联表或字段");
            }
        }

        // 验证 JSON 格式
        if (StrUtil.isNotBlank(config.getWidgetConfig())) {
            try {
                JSONUtil.parseObj(config.getWidgetConfig());
            } catch (Exception e) {
                throw new BusinessException("控件配置 JSON 格式错误");
            }
        }
    }
}
