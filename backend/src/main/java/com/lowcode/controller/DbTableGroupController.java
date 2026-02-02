package com.lowcode.controller;

import com.lowcode.common.Result;
import com.lowcode.entity.DbTableGroup;
import com.lowcode.service.IDbTableGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库表分组控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/db-table-group")
@RequiredArgsConstructor
@Tag(name = "库表分组管理", description = "库表分组管理相关接口")
public class DbTableGroupController {

    private final IDbTableGroupService dbTableGroupService;

    @GetMapping("/tree")
    @Operation(summary = "获取分组树")
    public Result<List<DbTableGroup>> getGroupTree() {
        List<DbTableGroup> tree = dbTableGroupService.getGroupTree();
        return Result.success(tree);
    }

    @PostMapping
    @Operation(summary = "创建分组")
    public Result<Long> createGroup(@RequestBody DbTableGroup group) {
        Long id = dbTableGroupService.createGroup(group);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新分组")
    public Result<Void> updateGroup(@PathVariable Long id, @RequestBody DbTableGroup group) {
        dbTableGroupService.updateGroup(id, group);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分组")
    public Result<Void> deleteGroup(@PathVariable Long id) {
        dbTableGroupService.deleteGroup(id);
        return Result.success();
    }
}
