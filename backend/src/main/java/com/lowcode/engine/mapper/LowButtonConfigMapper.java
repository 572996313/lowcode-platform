package com.lowcode.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.engine.entity.LowButtonConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 按钮配置 Mapper 接口
 */
@Mapper
public interface LowButtonConfigMapper extends BaseMapper<LowButtonConfig> {

}
