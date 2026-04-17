# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

低代码平台，前后端分离。前端 Vue 3 + Vite + TypeScript + Element Plus，后端 Spring Boot 3.x + Java 21 + MyBatis Plus。

**核心模块：** 页面设计器（PageDesigner）、表单设计器（FormDesigner）、表格设计器（TableDesigner）、页面渲染引擎（PageRender）

**核心表：** `low_page_config`（页面配置）、`low_form_config`（表单配置）、`low_table_config`（表格配置）、`low_button_config`（按钮配置）、`sys_menu`（菜单）

## 常用命令

```bash
# 前端 (frontend/)
cd frontend
npm run dev                    # 启动开发服务器 (http://localhost:3000)
npm run build                  # 构建生产版本
npm run gen:component-map      # 新增页面后需运行

# 后端 (backend/)
cd backend
mvn compile                    # 编译
mvn spring-boot:run            # 运行 (http://localhost:8765)
```

## 项目架构

### 前端

- `unplugin-auto-import` 自动导入 Vue API 和 Router/Pinia 方法
- `unplugin-vue-components` 自动导入 Element Plus 组件
- `src/utils/request.ts` — Axios 封装，自动 JWT token + 响应拦截
- `src/api/` — API 接口定义
- `src/views/lowcode-v6/` — 低代码核心页面（FreeCanvasDesigner, SmartPageRender 等）
- `src/views/lowcode/` — 低代码基础功能（TableDesigner, FormDesigner 等）

**重要：** Vue API 和 Element Plus 组件无需手动 import。

### 后端

Spring Boot 3.2.2 + Java 21 + MyBatis Plus 3.5.5 + Knife4j 4.4.0 (http://localhost:8765/doc.html)

```
controller/     REST API 层
service/        业务逻辑（接口 + impl）
entity/         实体类（@TableName + @TableLogic + 自动填充）
mapper/         MyBatis Mapper
dto/            DTO 类（ApiRequest<T>, PageQueryData 等）
common/         Result<T>, PageResult<T>, BusinessException
config/         SecurityConfig, MybatisPlusConfig, CorsConfig
```

**数据库：** `mysql://localhost:3306/lowcode_platform`（root/1234），逻辑删除 `deleted`，主键自增，自动填充 `createTime/updateTime`。

## 统一 API 请求/响应规范

### 请求格式 — `ApiRequest<T>` 信封

所有标准数据查询接口统一使用 POST + 信封格式：

```java
// 后端 DTO: com.lowcode.dto.ApiRequest<T>
@Data
public class ApiRequest<T> {
    private Long pageId;        // 页面上下文
    private String componentId; // 组件上下文
    private T data;             // 业务数据（泛型）
}
```

**请求示例：**
```json
POST /api/table-data/query
{
  "pageId": 123,
  "componentId": "table-standard_abc",
  "data": {
    "current": 1,
    "size": 10,
    "filters": { "name": "张三", "status": "active" },
    "sortField": "createTime",
    "sortOrder": "desc"
  }
}
```

**分页查询业务参数 `PageQueryData`：**
```java
// 后端 DTO: com.lowcode.dto.PageQueryData
@Data
public class PageQueryData {
    private Integer current = 1;
    private Integer size = 10;
    private Map<String, Object> filters;  // 搜索过滤参数
    private String sortField;             // 排序字段
    private String sortOrder;             // asc / desc
}
```

**前端构造请求：**
```typescript
const params = {
  pageId: props.pageId,
  componentId: props.componentId,
  data: {
    current: pagination.current,
    size: pagination.size,
    filters: { ...searchParams }
  }
}
await request.post('/api/table-data/query', params)
```

### 响应格式 — `Result<T>`

```java
// 后端: com.lowcode.common.Result<T>
{
  "success": true,           // 布尔，是否成功
  "code": 200,               // 状态码（200=成功，500=失败）
  "message": "操作成功",      // 消息
  "data": { ... },           // 实际业务数据
  "timestamp": 1710000000000 // 时间戳
}
```

### 分页响应 — `PageResult<T>`

当 `data` 为分页数据时，结构为 `PageResult<T>`：
```json
{
  "success": true,
  "code": 200,
  "data": {
    "records": [ { "id": 1, "name": "..." }, ... ],
    "total": 56,
    "current": 1,
    "size": 10,
    "pages": 6
  }
}
```

### 前端响应拦截器

```typescript
// src/utils/request.ts — 当 code === 200 或 success === true 时，自动解包返回 data
const { code, success, message, data } = response.data
if (code === 200 || success === true) {
  return data  // 直接返回业务数据
}
```

**调用方直接拿到 data，不需要再 `.data`：**
```typescript
// ✅ 正确
const result = await request.post('/api/table-data/query', params)
console.log(result.records)  // 数据列表
console.log(result.total)    // 总条数

// ❌ 错误 — 拦截器已解包，二次 .data 为 undefined
console.log(result.data.records)
```

### Controller 编写规范

```java
// ✅ 数据查询接口：使用 ApiRequest<T> 信封
@PostMapping("/query")
public Result<PageResult<Map<String, Object>>> queryTableData(
        @RequestBody ApiRequest<PageQueryData> request) {
    return Result.success(tableDataService.queryTableData(request));
}

// ✅ CRUD 接口：使用实体类或具体 DTO
@PostMapping
public Result<Long> create(@RequestBody LowTableConfig config) {
    return Result.success(service.create(config));
}

// ✅ 统一返回：Result.success(data) / Result.fail(message)
```

**新增数据接口时，优先使用 POST + `ApiRequest<PageQueryData>` 格式。**

## 开发规范

### 命名约定

| 类型 | 规则 | 示例 |
|-----|------|------|
| Vue 组件 | PascalCase | `PageManage.vue` |
| Java 类 | PascalCase | `PageConfigController` |
| 路由(管理) | PascalCase | `/lowcode/PageManage` |
| 路由(功能) | kebab-case | `/page/preview` |
| API 路径 | kebab-case | `/api/table-data` |
| 数据库表 | snake_case | `low_page_config` |

### 路由规范
- 管理页面用 PascalCase：`/lowcode/PageManage`、`/lowcode/FormList`
- 动态参数用具体名称：`:pageId`，避免通用 `:id`
- **不要**用小写路由名如 `/lowcode/page`（会被误解析为参数）

### 后端实体类注解

| 注解 | 用途 |
|-----|------|
| `@TableName` | 指定表名 |
| `@TableId(type = IdType.AUTO)` | 主键自增 |
| `@TableLogic` | 逻辑删除字段 |
| `@TableField(fill = FieldFill.INSERT)` | 插入时自动填充 |
| `@TableField(fill = FieldFill.INSERT_UPDATE)` | 插入和更新时自动填充 |
| `@TableField(exist = false)` | 非数据库字段 |

### 前端路由注册

新增页面后，需在 `router/componentMap.ts` 中注册组件路径映射，然后运行 `npm run gen:component-map`。

## 常见问题

| 问题 | 原因 | 解决 |
|-----|------|------|
| 路由参数 'page' 无法转 Long | `/lowcode/page` 被误解析 | 改用 PascalCase `/lowcode/PageManage` |
| `ClassCastException: String→Integer` | `@RequestParam Map` 值都是 String | 用 `Integer.valueOf(val.toString())` |
| `Unknown column 'xxx'` | 实体类字段与表结构不一致 | 加 `@TableField(exist = false)` 或加列 |
| `res.data.xxx` 为 undefined | 拦截器已解包，不需要 `.data` | 直接 `res.xxx` |

## 调试

- **API 文档：** http://localhost:8765/doc.html（Knife4j）
- **Druid 监控：** http://localhost:8765/druid/（admin/admin123）
- **数据库：** `mysql -uroot -p1234 lowcode_platform`
- **后端日志：** `logs/lowcode-platform.log`（com.lowcode 包 DEBUG 级别）

## 字符集

执行 SQL 脚本必须指定字符集，否则中文乱码：

```bash
mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < script.sql
```

SQL 文件开头加：
```sql
SET NAMES utf8mb4;
```

建表语句加：`ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci`
