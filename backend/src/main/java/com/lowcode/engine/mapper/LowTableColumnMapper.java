package com.lowcode.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.engine.entity.LowTableColumn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表格列配置 Mapper
 */
@Mapper
public interface LowTableColumnMapper extends BaseMapper<LowTableColumn> {
}
