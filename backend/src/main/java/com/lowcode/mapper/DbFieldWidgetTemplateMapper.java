package com.lowcode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.entity.DbFieldWidgetTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字段控件配置模板 Mapper 接口
 *
 * @author lowcode-platform
 * @since 2025-02-01
 */
@Mapper
public interface DbFieldWidgetTemplateMapper extends BaseMapper<DbFieldWidgetTemplate> {

    /**
     * 根据字段ID获取所有启用的模板
     *
     * @param fieldId 字段ID
     * @return 模板列表
     */
    List<DbFieldWidgetTemplate> selectEnabledTemplatesByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 根据表ID获取所有模板
     *
     * @param tableId 表ID
     * @return 模板列表
     */
    List<DbFieldWidgetTemplate> selectTemplatesByTableId(@Param("tableId") Long tableId);

    /**
     * 根据字段ID和模板编码获取模板
     *
     * @param fieldId      字段ID
     * @param templateCode 模板编码
     * @return 模板对象
     */
    DbFieldWidgetTemplate selectByFieldIdAndCode(@Param("fieldId") Long fieldId,
                                                  @Param("templateCode") String templateCode);
}
