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
npm run gen:component-map  # 生成组件路径映射（新增页面后需运行）
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
- MySQL 在本地安装的 MySQL
- 逻辑删除字段：`deleted`（1=删除，0=存在）
- 主键策略：`AUTO`（数据库自增）
- 字段自动填充：`createTime`（插入时）、`updateTime`（插入和更新时）

**MyBatis Plus 配置：**
- Mapper XML 位置：`classpath*:/mapper/**/*.xml`
- 实体类包扫描：`com.lowcode.entity`
- 驼峰转下划线：自动启用
- 分页插件：`PaginationInnerInterceptor`（MySQL 数据库）

**数据库初始化：**
- 初始化脚本：``
- 迁移脚本：

### 字符集和乱码问题

**重要：** 执行 SQL 脚本时必须使用正确的字符集参数，否则会导致乱码。

**问题类型：**

1. **表结构注释乱码**（`??`）
   - **原因**: 创建表时没有指定 `--default-character-set=utf8mb4` 参数
   - **影响**: 表的 COMMENT 显示为 `??`，但数据本身可能正常

2. **数据内容乱码**（如 `ä¿­`）
   - **原因**: 命令行在执行 SQL 时进行了错误的字符集转换
   - **影响**: 插入的中文数据显示为乱码

**正确的 SQL 执行方式：**

#### 场景 1：MySQL 在 Docker 中
```bash
# 查找 MySQL 容器
docker ps | grep mysql

# ✅ 正确方式 - 使用文件导入，避免命令行字符集转换
docker exec -i <容器名或ID> mysql -uroot -p<密码> --default-character-set=utf8mb4 lowcode_platform < script.sql

# 示例（容器名为 wizardly_yalow）
docker exec -i wizardly_yalow mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < script.sql

# 或者在 SQL 文件开头设置字符集
docker exec -i wizardly_yalow mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < script.sql
```

#### 场景 2：本地 MySQL
```bash
# ✅ 正确方式 - 指定字符集参数
mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < script.sql

# 或在 MySQL 命令行中设置
mysql -uroot -p1234 lowcode_platform
mysql> SET NAMES utf8mb4;
mysql> SOURCE script.sql;
```

**通用 SQL 文件模板：**

在 SQL 脚本开头添加字符集设置：
```sql
-- 设置字符集（防止乱码）
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

-- 你的 SQL 语句...
```

**字符集验证：**

```bash
# Docker 环境
docker exec <容器名> mysql -uroot -p<密码> -e "SHOW VARIABLES LIKE 'character_set%';"

# 本地 MySQL
mysql -uroot -p<密码> -e "SHOW VARIABLES LIKE 'character_set%';"

# 查看表结构（注释应正常显示中文）
docker exec <容器名> mysql -uroot -p<密码> --default-character-set=utf8mb4 -e "USE lowcode_platform; SHOW CREATE TABLE low_button_config\G"
```

**修复乱码的步骤：**

1. 备份现有数据到临时表
2. 使用正确的字符集重建表结构
3. 从备份表恢复数据（确保使用 `--default-character-set=utf8mb4`）
4. 验证数据是否正常显示

参考脚本：`docs/migration/008_fix_button_charset.sql`

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

### API 响应数据结构规范

**核心原则：响应拦截器在 `code === 200` 时已自动解包返回 `data`，调用方直接获取业务数据，无需再次访问 `.data`。**

**后端统一响应格式（Result<T>）：**
```java
{
  "code": 200,
  "message": "success",
  "data": { ... }  // 实际业务数据
}
```

**前端响应拦截器行为（`src/utils/request.ts`）：**
```typescript
// 当 code === 200 时，拦截器返回 data
service.interceptors.response.use((response) => {
  const { code, message, data } = response.data
  if (code === 200) {
    return data  // ✅ 直接返回 data，不是 response.data
  }
  // ...
})
```

**API 调用后的返回值类型：**

| 调用方式 | 返回值 | 类型 |
|---------|--------|------|
| 单个对象 | `{ id: 1, name: '...' }` | `T` |
| 列表数据 | `[ { id: 1, ... }, { id: 2, ... } ]` | `T[]` |
| 分页数据 | `{ records: [...], total: 100, ... }` | `PageResult<T>` |

**❌ 错误写法（二次访问 .data）：**
```typescript
// 错误：访问 res.data（data 已经被拦截器解包）
const res = await request.get(`/page/${id}`)
console.log(res.data.configVersion)  // undefined
console.log(res.data.pageName)       // undefined
```

**✅ 正确写法（直接访问字段）：**
```typescript
// 正确：res 已经是业务数据对象
const res = await request.get(`/page/${id}`)
console.log(res.configVersion)  // 10
console.log(res.pageName)       // '页面名称'

// 解析 JSON 字段
const config = JSON.parse(res.configJson || '{}')
console.log(config.version)       // 'free-canvas'
```

**分页数据处理：**
```typescript
// 正确：直接访问分页信息
const result = await request.get('/page/list', { page: 1, size: 10 })
console.log(result.records)  // 数据列表
console.log(result.total)    // 总条数
console.log(result.current)  // 当前页

// 错误：不要访问 result.data.records
console.log(result.data.records)  // undefined
```

**错误处理模式：**
```typescript
try {
  const data = await request.get('/api/endpoint')
  // data 是直接的业务数据，无需 .data
  console.log(data.fieldName)
} catch (error: any) {
  // error.message 已经被拦截器处理
  console.error('请求失败:', error.message)
  ElMessage.error(error.message)
}
```

**类型定义建议：**
```typescript
// 在 API 文件中定义明确的返回类型
interface PageConfigResponse {
  id: number
  pageName: string
  pageCode: string
  configVersion?: number
  configJson?: string
  // ...
}

export async function getPageConfig(id: number): Promise<PageConfigResponse> {
  return request.get(`/page/${id}`)
}

// 使用时获得类型提示
const page = await getPageConfig(1)
console.log(page.pageName)      // ✅ 类型安全
console.log(page.data.pageName)   // ❌ TypeScript 会报错
```

**调试技巧：**
```typescript
// 遇到数据问题时，先打印返回值
const res = await request.get('/api/endpoint')
console.log('API 返回值:', res)
console.log('API 返回值类型:', typeof res)
console.log('包含字段:', Object.keys(res))

// 判断返回值结构
if ('records' in res && 'total' in res) {
  console.log('这是分页数据')
} else if ('id' in res) {
  console.log('这是单个对象')
} else if (Array.isArray(res)) {
  console.log('这是数组')
}
```

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
3. **数据库连接诊断**：
   - 检查 MySQL 是否运行：`docker ps | grep mysql` 或 `systemctl status mysql`
   - 测试连接：`mysql -h localhost -P 3306 -u root -p`
   - 查看字符集：确保所有字符集变量为 `utf8mb4`
4. **日志配置**：
   - 后端日志文件：`logs/lowcode-platform.log`
   - 日志级别：com.lowcode 包为 DEBUG，com.lowcode.mapper 为 DEBUG
   - 控制台输出：使用 ANSI 彩色日志
5. **Vue DevTools**：浏览器安装 Vue DevTools 扩展进行前端调试

## 开发规范（核心）

> 详细的开发规范文档请参考：`docs/DEVELOPMENT_GUIDE.md`

### 路由命名规范
- **管理页面**：PascalCase，如 `/lowcode/PageManage`、`/lowcode/FormList`
- **设计器**：PascalCase，如 `/lowcode/PageDesigner`
- **动态参数**：使用具体名称 `:pageId`，避免通用 `:id`

**❌ 错误示例**：`/lowcode/page` → 可能被误解析为参数
**✅ 正确示例**：`/lowcode/PageManage` → 明确的管理页面

### API 路径规范
```
GET    /api/page/list           # 分页查询
GET    /api/page/{id}           # 获取详情
POST   /api/page                # 创建
PUT    /api/page/{id}           # 更新
DELETE /api/page/{id}           # 删除
POST   /api/page/{id}/publish   # 自定义操作
```

### 参数验证规范
```typescript
// 前端：严格验证
const pageId = Number(route.params.id)
if (!pageId || isNaN(pageId) || pageId <= 0) {
  throw new Error(`无效的页面ID: ${route.params.id}`)
}
```

```java
// 后端：参数校验
@GetMapping("/{id}")
public Result<PageConfig> getPage(@PathVariable Long id) {
    if (id == null || id <= 0) {
        return Result.fail("无效的页面ID");
    }
    // 业务逻辑...
}
```

### 命名约定

| 类型 | 规则 | 示例 |
|-----|------|------|
| Vue组件 | PascalCase | `PageManage.vue` |
| Java类 | PascalCase | `PageConfigController` |
| Java方法 | camelCase | `getPageList` |
| 变量 | camelCase | `pageId` |
| 路由(管理) | PascalCase | `/lowcode/PageManage` |
| 路由(功能) | kebab-case | `/page/preview` |
| API路径 | kebab-case | `/api/page-template` |
| 数据库表 | snake_case | `low_page_config` |

### 常见问题

**问题**：参数 'id' 的值 'page' 无法转换为 Long 类型
- **原因**：路由 `/lowcode/page` 被误解析为动态参数
- **解决**：改为 `/lowcode/PageManage`（PascalCase）
- **预防**：管理页面统一使用 PascalCase 路由

**问题**：`ClassCastException: String cannot be cast to Integer`
- **原因**：`@RequestParam Map<String, Object> params` 中，URL 查询参数值都是 **String 类型**，直接强制转换导致异常
- **解决**：使用 `Integer.valueOf(params.getOrDefault("key", "defaultValue").toString())`
- **预防**：参考下面「后端参数类型转换规范」

**问题**：`Unknown column 'xxx' in 'field list'`
- **原因**：实体类字段与数据库表结构不一致
- **解决**：
  - 方案一：给实体类字段添加 `@TableField(exist = false)` 注解（非数据库字段）
  - 方案二：给数据库表添加对应字段
- **预防**：参考下面「实体类与数据库表一致性规范」

**问题**：API 返回数据访问不到（如 `res.data.configVersion` 为 `undefined`）
- **原因**：响应拦截器在 `code === 200` 时已返回 `data`，调用方错误地二次访问 `.data`
- **解决**：直接访问业务字段，如 `res.configVersion` 而非 `res.data.configVersion`
- **预防**：参考「API 响应数据结构规范」，记住拦截器已自动解包
- **示例**：
  ```typescript
  // ❌ 错误
  const res = await request.get(`/page/${id}`)
  console.log(res.data.configVersion)  // undefined

  // ✅ 正确
  const res = await request.get(`/page/${id}`)
  console.log(res.configVersion)  // 10
  ```

## 后端开发专项规范

### 参数类型转换规范

**Spring MVC `@RequestParam Map<String, Object>` 的类型特性：**

所有 URL 查询参数值（`?key=value`）在 `Map<String, Object>` 中都是 **String 类型**，不是目标类型。

**❌ 错误写法：**
```java
// 直接强制转换会抛出 ClassCastException
Integer current = (Integer) params.getOrDefault("current", 1);
Long size = (Long) params.getOrDefault("size", 10L);
Boolean status = (Boolean) params.get("status");
```

**✅ 正确写法：**
```java
// 先转为 String，再解析为目标类型
Integer current = Integer.valueOf(params.getOrDefault("current", "1").toString());
Integer size = Integer.valueOf(params.getOrDefault("size", "10").toString());
Boolean status = params.get("status") != null ? Boolean.valueOf(params.get("status").toString()) : null;
```

**推荐：创建参数解析工具类**
```java
public class ParamUtil {
    public static Integer getInt(Map<String, Object> params, String key, Integer defaultValue) {
        Object value = params.get(key);
        if (value == null) return defaultValue;
        return Integer.valueOf(value.toString());
    }

    public static Long getLong(Map<String, Object> params, String key, Long defaultValue) {
        Object value = params.get(key);
        if (value == null) return defaultValue;
        return Long.valueOf(value.toString());
    }

    public static Boolean getBoolean(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value != null ? Boolean.valueOf(value.toString()) : null;
    }
}
```

### 实体类与数据库表一致性规范

**实体类字段必须与数据库表结构保持一致，否则会导致 SQL 错误。**

**不一致时的处理方案：**

| 场景 | 解决方案 | 示例 |
|-----|---------|------|
| 实体类有字段，表没有 | 添加 `@TableField(exist = false)` | `@TableField(exist = false) private String createBy;` |
| 表有字段，实体类没有 | 在实体类添加对应字段 | `private String createBy;` |
| 字段名不一致 | 使用 `@TableField` 指定列名 | `@TableField("user_name") private String userName;` |

**检查清单（新增实体类/修改表结构后）：**
1. 确认实体类字段与数据库表字段一一对应
2. 确认字段类型匹配（String → VARCHAR，Integer → INT 等）
3. 确认逻辑删除字段 `@TableLogic` 注解正确
4. 确认自动填充字段 `@TableField(fill = ...)` 注解正确
5. 运行应用后检查日志，确认没有 SQL 字段错误

**建表脚本规范：**
- 建表脚本应包含所有实体类字段对应的列
- 标准字段：`id`, `create_time`, `update_time`, `deleted`, `create_by`（可选）
- 字符集：`ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci`

