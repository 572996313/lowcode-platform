package ${packagePath}.service.impl;

import com.lowcode.common.PageResult;
import com.lowcode.common.exception.BusinessException;
import com.lowcode.dto.ApiRequest;
import com.lowcode.dto.ComponentDetailData;
import com.lowcode.dto.PageQueryData;
import com.lowcode.modules.${moduleName}.entity.${entityName}Entity;
import com.lowcode.modules.${moduleName}.mapper.${entityName}Mapper;
import com.lowcode.modules.${moduleName}.service.I${entityName}Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ${entityName}ServiceImpl extends ServiceImpl<${entityName}Mapper, ${entityName}Entity>
        implements I${entityName}Service {

    // ========== 公共接口实现 ==========

    @Override
    public PageResult<Map<String, Object>> queryTableData(ApiRequest<PageQueryData> request) {
        PageQueryData queryData = request.getData();
        Integer current = queryData.getCurrent() != null ? queryData.getCurrent() : 1;
        Integer size = queryData.getSize() != null ? queryData.getSize() : 10;
        Map<String, Object> filters = queryData.getFilters();

        LambdaQueryWrapper<${entityName}Entity> wrapper = new LambdaQueryWrapper<>();

        // 按条件构建查询
        if (filters != null && !filters.isEmpty()) {
            filters.forEach((key, value) -> {
                if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                    return;
                }
                addFilterCondition(wrapper, key, value);
            });
        }

        // 排序
        if (StringUtils.hasText(queryData.getSortField())) {
            wrapper.orderBy(true, "desc".equalsIgnoreCase(queryData.getSortOrder()),
                    this.getClass(), queryData.getSortField());
        } else {
            wrapper.orderByDesc(${entityName}Entity::getCreateTime);
        }

        IPage<${entityName}Entity> page = this.page(new Page<>(current, size), wrapper);

        List<Map<String, Object>> records = page.getRecords().stream()
                .map(this::entityToMap)
                .toList();

        return PageResult.of(records, page.getTotal(), (long) current, (long) size);
    }

    @Override
    public Map<String, Object> getDetailData(ApiRequest<ComponentDetailData> request) {
        Object id = request.getData().getId();
        if (id == null) {
            throw new BusinessException("ID不能为空");
        }

        ${entityName}Entity entity = this.getById(id.toString());
        if (entity == null) {
            throw new BusinessException("数据不存在");
        }

        return entityToMap(entity);
    }

    @Override
    @Transactional
    public String saveData(ApiRequest<Map<String, Object>> request) {
        Map<String, Object> data = request.getData();
        Object id = data.get("id");

        ${entityName}Entity entity = new ${entityName}Entity();
        copyMapToEntity(data, entity);

        if (id != null && !id.toString().isEmpty()) {
            entity.setId(id.toString());
            this.updateById(entity);
            return entity.getId();
        } else {
            this.save(entity);
            return entity.getId();
        }
    }

    // ========== 标准 CRUD 实现 ==========

    @Override
    @Transactional
    public String create(${entityName}Entity entity) {
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public boolean update(${entityName}Entity entity) {
        if (entity.getId() == null) {
            throw new BusinessException("ID不能为空");
        }
        return this.updateById(entity);
    }

    @Override
    @Transactional
    public boolean deleteById(String id) {
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean deleteBatch(List<String> ids) {
        return this.removeByIds(ids);
    }

    @Override
    @Transactional
    public boolean updateEnabled(String id, Boolean enabled) {
        ${entityName}Entity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException("数据不存在");
        }
        entity.setEnabled(enabled);
        return this.updateById(entity);
    }

    // ========== 工具方法 ==========

    private Map<String, Object> entityToMap(${entityName}Entity entity) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", entity.getId());
        map.put("deleted", entity.getDeleted());
        map.put("createBy", entity.getCreateBy());
        map.put("createTime", entity.getCreateTime());
        map.put("updateBy", entity.getUpdateBy());
        map.put("updateTime", entity.getUpdateTime());
        map.put("status", entity.getStatus());
        map.put("enabled", entity.getEnabled());

        // 业务字段
        Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(f -> !isSystemField(f.getName()))
                .forEach(f -> {
                    f.setAccessible(true);
                    try {
                        map.put(f.getName(), f.get(entity));
                    } catch (IllegalAccessException ignored) {}
                });
        return map;
    }

    private boolean isSystemField(String fieldName) {
        return fieldName.equals("id") || fieldName.equals("deleted")
            || fieldName.equals("createBy") || fieldName.equals("createTime")
            || fieldName.equals("updateBy") || fieldName.equals("updateTime")
            || fieldName.equals("status") || fieldName.equals("enabled");
    }

    private void copyMapToEntity(Map<String, Object> map, ${entityName}Entity entity) {
        map.forEach((key, value) -> {
            try {
                var field = ${entityName}Entity.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(entity, value);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        });
    }

    private void addFilterCondition(LambdaQueryWrapper<${entityName}Entity> wrapper, String key, Object value) {
        try {
            var field = ${entityName}Entity.class.getDeclaredField(key);
            if (field.getType() == String.class) {
                // 字符串类型使用模糊查询
                wrapper.like(true, com.lowcode.generator.ColumnInfo.toCamelCase(key), value.toString());
            } else {
                wrapper.eq(true, com.lowcode.generator.ColumnInfo.toCamelCase(key), value);
            }
        } catch (NoSuchFieldException ignored) {}
    }
}