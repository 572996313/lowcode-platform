package com.lowcode.controller;

import com.lowcode.common.Result;
import com.lowcode.dto.DictReferenceResult;
import com.lowcode.service.ISysDictReferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典引用关系接口
 *
 * @author lowcode
 */
@RestController
@RequestMapping("/api/dict/reference")
@RequiredArgsConstructor
@Tag(name = "字典引用管理", description = "字典引用关系相关接口")
public class DictReferenceController {

    private final ISysDictReferenceService dictReferenceService;

    /**
     * 查询字典引用
     */
    @GetMapping("/{dictCode}")
    @Operation(summary = "查询字典引用")
    public Result<DictReferenceResult> getReferences(@PathVariable String dictCode) {
        return Result.success(dictReferenceService.findReferences(dictCode));
    }

    /**
     * 检查字典是否被引用
     */
    @GetMapping("/{dictCode}/check")
    @Operation(summary = "检查字典是否被引用")
    public Result<Boolean> isReferenced(@PathVariable String dictCode) {
        return Result.success(dictReferenceService.isReferenced(dictCode));
    }

    /**
     * 获取字典引用数量
     */
    @GetMapping("/{dictCode}/count")
    @Operation(summary = "获取字典引用数量")
    public Result<Integer> getReferenceCount(@PathVariable String dictCode) {
        return Result.success(dictReferenceService.getReferenceCount(dictCode));
    }

    /**
     * 获取所有字典的引用计数
     */
    @GetMapping("/counts")
    @Operation(summary = "获取所有字典的引用计数")
    public Result<java.util.Map<String, Integer>> getAllDictReferenceCounts() {
        return Result.success(dictReferenceService.getAllDictReferenceCounts());
    }

    /**
     * 查询未使用的字典
     */
    @GetMapping("/unused")
    @Operation(summary = "查询未使用的字典")
    public Result<List<String>> getUnusedDicts() {
        return Result.success(dictReferenceService.getUnusedDictCodes());
    }

    /**
     * 重建引用索引
     */
    @PostMapping("/rebuild")
    @Operation(summary = "重建引用索引")
    public Result<Integer> rebuildReferences() {
        int count = dictReferenceService.rebuildAllReferences();
        return Result.success(count);
    }

    /**
     * 清理无效引用
     */
    @PostMapping("/clean")
    @Operation(summary = "清理无效引用")
    public Result<Integer> cleanUnusedReferences() {
        int count = dictReferenceService.cleanUnusedReferences();
        return Result.success(count);
    }
}
