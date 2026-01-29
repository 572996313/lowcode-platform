# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个低代码平台项目，前后端分离架构。前端使用 Vue 3 + Vite + TypeScript + Element Plus，后端使用 Spring Boot 3.x + Java 17 + MyBatis Plus。

## 常用命令

### 前端 (frontend/)
```bash
cd frontend
npm run dev          # 启动开发服务器 (http://localhost:3000)
npm run build        # 构建生产版本 (vue-tsc && vite build)
npm run lint         # 代码检查和自动修复
```

### 后端 (backend/)
```bash
cd backend
mvn clean install    # 构建项目
mvn spring-boot:run  # 运行项目 (http://localhost:8080)
mvn test             # 运行测试
```

## 项目架构

### 前端架构

**配置特点：**
- 使用 `unplugin-auto-import` 自动导入 Vue API（ref, computed, watch, onMounted 等）和 Vue Router/Pinia 方法
- 使用 `unplugin-vue-components` 自动导入 Element Plus 组件
- Vite 代理配置：`/api` 转发到 `http://localhost:8080`

**目录结构：**
- `src/api/` - API 接口定义（form.ts, menu.ts, table.ts）
- `src/router/` - 路由配置（使用 `meta.hideInMenu` 控制菜单显示）
- `src/utils/request.ts` - Axios 请求封装，自动添加 JWT token
- `src/views/` - 页面组件
  - `lowcode/` - 低代码核心功能（FormDesigner, TableDesigner 等）
  - `system/` - 系统管理
  - `layout/` - 主布局
  - `login/` - 登录页

**重要：** 由于自动导入配置，代码中可以直接使用 Vue API 和 Element Plus 组件，无需手动 import。

### 后端架构

**技术栈：**
- Spring Boot 3.2.2 + Java 17
- MyBatis Plus (Spring Boot 3 版本)
- Spring Security + JWT (jjwt 0.12.5)
- Druid 连接池 + MySQL
- Redis + Commons Pool2
- Knife4j (API 文档，访问地址：http://localhost:8080/doc.html)
- MapStruct (对象映射)

**包结构：**
- `controller/` - REST API 层（FormConfigController, TableConfigController 等）
- `service/` + `service/impl/` - 业务逻辑层
- `entity/` - 实体类（LowFormConfig, LowTableConfig, LowFormField, LowTableColumn 等）
- `mapper/` - MyBatis Mapper 接口（使用 @Mapper 注解）
- `config/` - 配置类（CorsConfig, SecurityConfig, MybatisPlusConfig）
- `common/` - 通用类（Result 统一响应、PageResult 分页、异常处理）

**数据库配置：**
- 默认数据源：`mysql://localhost:3306/lowcode_platform`（用户名 root / 密码 123456）
- 逻辑删除字段：`deleted`（1=删除，0=存在）
- 主键策略：`AUTO`（数据库自增）

**MyBatis Plus 配置：**
- Mapper XML 位置：`classpath*:/mapper/**/*.xml`
- 实体类包扫描：`com.lowcode.entity`
- 驼峰转下划线：自动启用

## 配置文件

**后端配置** (backend/src/main/resources/application.yml):
- 服务端口: 8080
- JWT 配置：密钥、过期时间（86400000ms）、Header 名称、前缀 "Bearer "
- 上传配置：路径 `/data/upload/`，最大大小 10MB
- Druid 监控：`/druid/*`（用户 admin / admin123）

**前端配置** (frontend/vite.config.ts):
- 开发端口: 3000
- 路径别名：`@` → `src`
- API 代理：`/api` → `http://localhost:8080`

## 开发注意事项

1. **前端组件自动导入**：在 `.vue` 文件中使用 Element Plus 组件时，无需导入，直接使用即可
2. **后端逻辑删除**：实体类添加逻辑删除支持时，确保 `deleted` 字段存在且默认为 0
3. **MapStruct**：创建新的 Mapper 接口后，需要使用 `@Mapper(componentModel = "spring")` 注解
4. **API 文档**：后端启动后访问 http://localhost:8080/doc.html 查看 Knife4j 文档
5. **JWT 认证**：前端请求会自动从 localStorage 读取 token 并添加到 Authorization Header
