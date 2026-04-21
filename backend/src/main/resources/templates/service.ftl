package ${packagePath}.service;

import com.lowcode.common.PageResult;
import com.lowcode.dto.ApiRequest;
import com.lowcode.dto.ComponentDetailData;
import com.lowcode.modules.${moduleName}.entity.${entityName}Entity;
import java.util.Map;

public interface I${entityName}Service {

    // ========== 公共接口 - 集成低代码平台 ==========

    /** 分页查询列表数据 */
    PageResult<Map<String, Object>> queryTableData(ApiRequest<com.lowcode.dto.PageQueryData> request);

    /** 获取详情数据 */
    Map<String, Object> getDetailData(ApiRequest<ComponentDetailData> request);

    /** 保存数据（新增/编辑） */
    String saveData(ApiRequest<Map<String, Object>> request);

    // ========== 标准 CRUD ==========

    String create(${entityName}Entity entity);

    boolean update(${entityName}Entity entity);

    boolean deleteById(String id);

    boolean deleteBatch(List<String> ids);

    boolean updateEnabled(String id, Boolean enabled);
}