package com.lowcode.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 首页统计数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 统计数据 ====================

    /**
     * 页面配置总数
     */
    private Long pageCount;

    /**
     * 已发布页面数
     */
    private Long publishedPageCount;

    /**
     * 表单配置总数
     */
    private Long formCount;

    /**
     * 表格配置总数
     */
    private Long tableCount;

    /**
     * 菜单配置总数
     */
    private Long menuCount;

    /**
     * 按钮配置总数
     */
    private Long buttonCount;

    /**
     * 数据库表数量
     */
    private Long dbTableCount;

    // ==================== 系统信息 ====================

    /**
     * 系统版本
     */
    private String systemVersion;

    /**
     * Java 版本
     */
    private String javaVersion;

    /**
     * Spring Boot 版本
     */
    private String springBootVersion;

    /**
     * 数据库版本
     */
    private String databaseVersion;

    /**
     * 前端框架
     */
    private String frontendFramework;

    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * 操作系统架构
     */
    private String osArch;
}
