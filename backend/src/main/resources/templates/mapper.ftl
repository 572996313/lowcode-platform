package ${packagePath}.mapper;

import com.lowcode.modules.${moduleName}.entity.${entityName}Entity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${entityName}Mapper extends BaseMapper<${entityName}Entity> {
}