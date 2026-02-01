package com.lowcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.entity.DbTable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库表管理 Mapper
 */
@Mapper
public interface DbTableMapper extends BaseMapper<DbTable> {
}
