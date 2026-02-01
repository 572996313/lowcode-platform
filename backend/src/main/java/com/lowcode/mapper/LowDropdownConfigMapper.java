package com.lowcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.entity.LowDropdownConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 下拉配置 Mapper
 */
@Mapper
public interface LowDropdownConfigMapper extends BaseMapper<LowDropdownConfig> {

    /**
     * 根据配置编码查询
     */
    LowDropdownConfig selectByConfigCode(@Param("configCode") String configCode);

    /**
     * 执行 SQL 查询（用于测试）
     */
    List<Map<String, Object>> executeQuery(@Param("sql") String sql, @Param("params") Map<String, Object> params);
}
