package com.lowcode.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.engine.entity.DbTableField;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字段管理 Mapper
 */
@Mapper
public interface DbTableFieldMapper extends BaseMapper<DbTableField> {
}
