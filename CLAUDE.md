# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个低代码平台项目，前后端分离架构。前端使用 Vue 3 + Vite + TypeScript + Element Plus，后端使用 Spring Boot 3.x + Java 21 + MyBatis Plus。

### 核心功能模块

**低代码核心模块：**
- **页面设计器** (`PageDesigner`) - 可视化设计页面布局，支持拖拽组件
- **表单设计器** (`FormDesigner`) - 设计表单字段和校验规则
- **表格设计器** (`TableDesigner`) - 设计表格列和数据展示
- **页面渲染引擎** (`PageRender`) - 运行时渲染低代码页面，支持多种布局类型

**数据库核心表：**
- `sys_menu` - 菜单配置表（支持树形结构，类型：目录/菜单/按钮）
- `low_page_config` - 页面配置表（存储页面JSON配置、路由信息、发布状态）
- `low_page_template` - 页面模板表（预定义的页面布局模板）
- `low_form_config` - 表单配置表（表单布局、校验规则）
- `low_form_field` - 表单字段表（字段类型、选项、联动配置）
- `low_table_config` - 表格配置表（表格列、数据源配置）
- `low_table_column` - 表格列配置表（列类型、格式化、排序）
- `low_button_config` - 按钮配置表（按钮动作、权限、API调用）
- `low_component_template` - 组件模板表（可复用的组件配置）

## 常用命令

### 前端 (frontend/)
```bash
cd frontend
npm run dev          # 启动开发服务器 (http://localhost:3000)
npm run build        # 构建生产版本 (vue-tsc && vite build)
npm run preview      # 预览生产构建
npm run lint         # 代码检查和自动修复
```

### 后端 (backend/)
```bash
cd backend
mvn clean install    # 构建项目
mvn spring-boot:run  # 运行项目 (http://localhost:8765)
mvn test             # 运行测试
```

## 项目架构

### 前端架构

**配置特点：**
- 使用 `unplugin-auto-import` 自动导入 Vue API（ref, computed, watch, onMounted 等）和 Vue Router/Pinia 方法
- 使用 `unplugin-vue-components` 自动导入 Element Plus 组件
- 使用 `pinia-plugin-persistedstate` 持久化状态（localStorage）
- 使用 `@vueuse/core` 提供组合式工具函数
- 使用 `vue-draggable-plus` 实现拖拽功能（页面设计器）
- 使用 `monaco-editor` 提供代码编辑功能
- 使用 `echarts` + `vue-echarts` 实现数据可视化

**目录结构：**
- `src/api/` - API 接口定义（form.ts, menu.ts, table.ts, page.ts）
- `src/router/` - 路由配置（使用 `meta.hideInMenu` 控制菜单显示）
- `src/stores/` - Pinia 状态管理（menu 等）
- `src/utils/request.ts` - Axios 请求封装，自动添加 JWT token
- `src/views/` - 页面组件
  - `lowcode/` - 低代码核心功能（PageDesigner, FormDesigner, TableDesigner, PageRender 等）
  - `system/` - 系统管理（MenuManage 等）
  - `layout/` - 主布局
  - `login/` - 登录页
  - `home/` - 首页

**重要：** 由于自动导入配置，代码中可以直接使用 Vue API 和 Element Plus 组件，无需手动 import。

### 后端架构

**技术栈：**
- Spring Boot 3.2.2 + Java 21
- MyBatis Plus 3.5.5 (Spring Boot 3 版本)
- Spring Security + JWT (jjwt 0.12.5)
- Druid 连接池 + MySQL
- Redis + Commons Pool2
- Knife4j 4.4.0 (API 文档，访问地址：http://localhost:8765/doc.html)
- MapStruct 1.5.5 (对象映射)
- Hutool 5.8.25 (工具类库)
- EasyExcel 3.3.4 (Excel 导入导出)

**后端架构：**
- `controller/` - REST API 层（PageConfigController, FormConfigController, TableConfigController, ButtonConfigController, MenuController 等）
- `service/` + `service/impl/` - 业务逻辑层（接口 + 实现类模式）
- `entity/` - 实体类（LowFormConfig, LowTableConfig, LowFormField, LowTableColumn, LowPageConfig, LowButtonConfig, SysMenu 等）
- `mapper/` - MyBatis Mapper 接口（使用 @Mapper 注解）
- `config/` - 配置类
  - `SecurityConfig` - Spring Security 配置（当前开发模式：允许所有请求）
  - `MybatisPlusConfig` - 分页插件、自动填充（createTime/updateTime）
  - `CorsConfig` - 跨域配置
- `common/` - 通用类
  - `Result<T>` - 统一响应封装（code=200 表示成功）
  - `PageResult<T>` - 分页响应封装
  - `BusinessException` - 业务异常类
  - `GlobalExceptionHandler` - 全局异常处理
- `handler/` - MyBatis TypeHandler（如 DefaultValueTypeHandler 处理 JSON 字段）

**数据库配置：**
- 默认数据源：`mysql://localhost:3306/lowcode_platform`（用户名 root / 密码 1234）
- 逻辑删除字段：`deleted`（1=删除，0=存在）
- 主键策略：`AUTO`（数据库自增）
- 字段自动填充：`createTime`（插入时）、`updateTime`（插入和更新时）

**MyBatis Plus 配置：**
- Mapper XML 位置：`classpath*:/mapper/**/*.xml`
- 实体类包扫描：`com.lowcode.entity`
- 驼峰转下划线：自动启用
- 分页插件：`PaginationInnerInterceptor`（MySQL 数据库）

**数据库初始化：**
- 初始化脚本：`docs/init.sql`
- 迁移脚本：`docs/migration/` 目录（如 `002_add_page_publish.sql`）

## 配置文件

**后端配置** (backend/src/main/resources/application.yml):
- 服务端口: 8765
- JWT 配置：密钥、过期时间（86400000ms）、Header 名称、前缀 "Bearer "
- 上传配置：路径 `/data/upload/`，最大大小 10MB
- Druid 监控：`/druid/*`（用户 admin / admin123）

**前端配置** (frontend/vite.config.ts):
- 开发端口: 3000
- 路径别名：`@` → `src`
- API 代理：`/api` → `http://localhost:8765`
- 自动导入配置：Vue API、Vue Router、Pinia、@vueuse/core、Element Plus 组件
- 构建分包：vue-vendor、element-plus、echarts、monaco 独立打包

**环境变量** (.env):
- `VITE_API_BASE_URL` - API 基础路径（默认 `/api`）

## 开发注意事项

### 后端开发
1. **实体类注解**：
   - `@TableName` 指定表名
   - `@TableId(type = IdType.AUTO)` 主键自增
   - `@TableLogic` 逻辑删除字段
   - `@TableField(fill = FieldFill.INSERT)` 插入时自动填充
   - `@TableField(fill = FieldFill.INSERT_UPDATE)` 插入和更新时自动填充
   - `@TableField(exist = false)` 非数据库字段

2. **Mapper 接口**：使用 `@Mapper` 注解，继承 `BaseMapper<Entity>` 即可获得 CRUD 方法

3. **Service 层**：
   - 接口继承 `IService<Entity>`
   - 实现类继承 `ServiceImpl<Mapper, Entity>` 并实现接口
   - 可直接使用 MyBatis Plus 提供的批量操作、分页查询等方法

4. **Controller 返回值**：统一使用 `Result.success(data)` 或 `Result.fail(message)` 封装响应

5. **分页查询**：使用 `Page<T>` 对象，配合 MyBatis Plus 的分页插件

### 前端开发
1. **组件自动导入**：在 `.vue` 文件中使用 Element Plus 组件时，无需导入，直接使用即可
2. **Vue API 自动导入**：ref、computed、watch、onMounted 等 Vue API 无需手动导入
3. **路由组件注册**：新增页面后，需在 `router/componentMap.ts` 中注册组件路径映射
4. **API 调用**：使用 `src/utils/request.ts` 封装的 request 对象（get、post、put、delete），会自动处理 JWT token 和响应拦截
5. **状态管理**：使用 Pinia stores（如 `useMenuStore()`），支持持久化到 localStorage

### 低代码页面开发
1. **页面设计流程**：
   - 在 PageDesigner 中设计页面布局（选择布局类型、配置区域）
   - 为各区域配置表单/表格/按钮组件
   - 保存页面配置（存储到 `low_page_config` 表）
   - 发布页面（设置 `published=true`）
   - 访问路由路径查看渲染效果

2. **页面渲染机制**：
   - `PageRender.vue` 根据 `pageId` 加载页面配置
   - 根据 `layoutType` 选择对应的布局组件（TopBottomLayout、TreeTableLayout、TabsLayout 等）
   - 各区域使用对应的渲染组件（FormRender、TableRender、ButtonRenderer）
   - 支持搜索区、工具栏、内容区的联动交互

3. **组件配置 JSON**：
   - 页面配置：`configTemplate` 字段（v2 版本），包含完整的页面结构配置
   - 表单字段：`options_json`（选项配置）、`rules_json`（校验规则）、`linkage_json`（联动配置）
   - 表格列：`formatter_json`（格式化配置）、`filter_json`（筛选配置）

### 调试和诊断
1. **API 文档**：后端启动后访问 http://localhost:8765/doc.html 查看 Knife4j 文档
2. **Druid 监控**：访问 http://localhost:8765/druid/（用户 admin / admin123）查看 SQL 监控和数据库连接池状态
3. **日志配置**：
   - 后端日志文件：`logs/lowcode-platform.log`
   - 日志级别：com.lowcode 包为 DEBUG，com.lowcode.mapper 为 DEBUG
   - 控制台输出：使用 ANSI 彩色日志
4. **Vue DevTools**：浏览器安装 Vue DevTools 扩展进行前端调试
