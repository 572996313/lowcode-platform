package ${packagePath}.controller;

import com.lowcode.common.Result;
import com.lowcode.common.PageResult;
import com.lowcode.dto.ApiRequest;
import com.lowcode.dto.PageQueryData;
import com.lowcode.dto.ComponentDetailData;
import com.lowcode.modules.${moduleName}.entity.${entityName}Entity;
import com.lowcode.modules.${moduleName}.service.I${entityName}Service;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Tag(name = "${tableNameCN}", description = "${tableNameCN}管理")
@RestController
@RequestMapping("${apiPath}")
@RequiredArgsConstructor
public class ${entityName}Controller {

    private final I${entityName}Service ${entityNameLower}Service;

    // ========== 公共接口 - 符合低代码平台规范 ==========

    @Operation(summary = "查询列表")
    @PostMapping("/query")
    public Result<PageResult<Map<String, Object>>> queryTableData(
            @RequestBody ApiRequest<PageQueryData> request) {
        return Result.success(${entityNameLower}Service.queryTableData(request));
    }

    @Operation(summary = "详情")
    @PostMapping("/detail")
    public Result<Map<String, Object>> getDetailData(
            @RequestBody ApiRequest<ComponentDetailData> request) {
        return Result.success(${entityNameLower}Service.getDetailData(request));
    }

    @Operation(summary = "保存（新增/编辑）")
    @PostMapping("/save")
    public Result<String> saveData(@RequestBody ApiRequest<Map<String, Object>> request) {
        return Result.success(${entityNameLower}Service.saveData(request));
    }

    // ========== 标准 CRUD 接口 ==========

    @Operation(summary = "新增")
    @PostMapping("/create")
    public Result<String> create(@RequestBody ${entityName}Entity entity) {
        return Result.success(${entityNameLower}Service.create(entity));
    }

    @Operation(summary = "编辑")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody ${entityName}Entity entity) {
        return Result.success(${entityNameLower}Service.update(entity));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteById(@PathVariable String id) {
        return Result.success(${entityNameLower}Service.deleteById(id));
    }

    @Operation(summary = "启用/停用")
    @PostMapping("/enabled")
    public Result<Boolean> updateEnabled(@RequestBody ApiRequest<Map<String, Object>> request) {
        String id = String.valueOf(request.getData().get("id"));
        Boolean enabled = (Boolean) request.getData().get("enabled");
        return Result.success(${entityNameLower}Service.updateEnabled(id, enabled));
    }

    @Operation(summary = "批量删除")
    @DeleteMapping("/batch")
    public Result<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return Result.success(${entityNameLower}Service.deleteBatch(ids));
    }
}