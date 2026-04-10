package com.lowcode.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lowcode.common.Result;
import com.lowcode.dto.DashboardStatsVO;
import com.lowcode.entity.LowPageConfig;
import com.lowcode.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.SpringBootVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘控制器 - 首页统计数据
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表盘", description = "首页统计数据接口")
public class DashboardController {

    private final ILowPageConfigService pageConfigService;
    private final ILowFormConfigService formConfigService;
    private final ILowTableConfigService tableConfigService;
    private final ISysMenuService menuService;
    private final ILowButtonConfigService buttonConfigService;
    private final IDbTableService dbTableService;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/stats")
    @Operation(summary = "获取首页统计数据")
    public Result<DashboardStatsVO> getDashboardStats() {
        // 统计各模块数量（IService.count() 自带逻辑删除过滤）
        long pageCount = pageConfigService.count();
        long formCount = formConfigService.count();
        long tableCount = tableConfigService.count();
        long menuCount = menuService.count();
        long buttonCount = buttonConfigService.count();
        long dbTableCount = dbTableService.count();

        // 已发布页面数
        long publishedPageCount = pageConfigService.count(
                Wrappers.lambdaQuery(LowPageConfig.class).eq(LowPageConfig::getPublished, true)
        );

        // 数据库版本
        String dbVersion = "unknown";
        try {
            dbVersion = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
        } catch (Exception e) {
            log.warn("获取数据库版本失败: {}", e.getMessage());
        }

        // 组装结果
        return Result.success(DashboardStatsVO.builder()
                .pageCount(pageCount)
                .publishedPageCount(publishedPageCount)
                .formCount(formCount)
                .tableCount(tableCount)
                .menuCount(menuCount)
                .buttonCount(buttonCount)
                .dbTableCount(dbTableCount)
                .systemVersion("1.0.0")
                .javaVersion(System.getProperty("java.version"))
                .springBootVersion(SpringBootVersion.getVersion())
                .databaseVersion(dbVersion)
                .frontendFramework("Vue 3 + Element Plus")
                .osName(System.getProperty("os.name"))
                .osArch(System.getProperty("os.arch"))
                .build());
    }
}
