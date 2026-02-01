# 低代码平台开发规范

## 1. 路由命名规范

### 1.1 前端路由路径规范

**基本原则：**
- 使用 PascalCase（大驼峰）命名管理页面组件路由
- 使用 kebab-case（短横线）命名功能页面路由
- 避免使用可能被误解为参数的路由段

**路由路径格式：**

```
/模块/功能/操作
```

**示例：**

| 功能类型 | 路由路径 | 说明 |
|---------|---------|------|
| 管理页面 | `/lowcode/PageManage` | 页面管理，使用大驼峰 |
| 设计器 | `/lowcode/PageDesigner` | 页面设计器，使用大驼峰 |
| 列表页面 | `/lowcode/FormList` | 表单列表，使用大驼峰 |
| 预览页面 | `/page/preview` | 页面预览，使用短横线 |
| 动态页面 | `/page/publish/:id` | 带ID参数的动态页面 |

### 1.2 禁止的路由模式

```typescript
// ❌ 错误：容易与参数混淆
/lowcode/page        // 可能被解析为 /lowcode/:page
/lowcode/form        // 可能被解析为 /lowcode/:form
/api/:id             // 太宽泛，容易误匹配

// ✅ 正确：明确的功能路径
/lowcode/PageManage  // 明确是管理页面
/lowcode/FormList    // 明确是列表页面
/api/page/:id        // 限定资源类型
```

### 1.3 动态路由参数规范

**参数命名：**
- 使用具体的资源名称，如 `pageId`、`formId`、`menuId`
- 避免使用通用的 `id`

**示例：**

```typescript
// ✅ 推荐
{
  path: 'pages/:pageId',
  name: 'PageDetail',
  component: PageDetail
}

// ❌ 不推荐
{
  path: ':id',        // 太宽泛
  name: 'Detail',
  component: Detail
}
```

---

## 2. API 路径规范

### 2.1 RESTful API 设计原则

**资源路径格式：**

```
/api/{资源}/{操作}
```

**资源命名：**
- 使用复数形式表示资源集合
- 使用 kebab-case（短横线）

**示例：**

| 操作 | HTTP方法 | API路径 | 说明 |
|------|---------|---------|------|
| 列表 | GET | `/api/page/list` | 分页查询 |
| 详情 | GET | `/api/page/{id}` | 获取单个资源 |
| 创建 | POST | `/api/page` | 创建资源 |
| 更新 | PUT | `/api/page/{id}` | 更新资源 |
| 删除 | DELETE | `/api/page/{id}` | 删除资源 |
| 自定义 | POST | `/api/page/{id}/publish` | 自定义操作 |

### 2.2 特殊资源的API路径

**模板相关：**
```
/api/page/template           # 页面模板CRUD
/api/page/template/list      # 模板列表
/api/page/template/{id}      # 模板详情
/api/page/template/from-template  # 从模板创建
```

**组件相关：**
```
/api/component-template      # 组件模板CRUD
/api/button                  # 按钮配置CRUD
```

### 2.3 禁止的API路径

```typescript
// ❌ 错误示例
GET  /api/{id}               // 资源类型不明确
POST /api/page/page          // 路径重复
GET  /api/getPageList        // 不符合RESTful规范
```

---

## 3. 参数验证规范

### 3.1 前端参数验证

**路由参数验证：**

```typescript
// ✅ 正确：严格的参数验证
const getPageId = (): number => {
  const metaPageId = route.meta.pageId
  const paramsId = route.params.id
  const queryId = route.query.id

  // 验证参数类型和格式
  const pageIdFromMeta = typeof metaPageId === 'number' ? metaPageId : null
  const pageIdFromParams = typeof paramsId === 'string' && /^\d+$/.test(paramsId) ? Number(paramsId) : null
  const pageIdFromQuery = typeof queryId === 'string' && /^\d+$/.test(queryId) ? Number(queryId) : null

  const pageId = pageIdFromMeta || pageIdFromParams || pageIdFromQuery

  if (!pageId || isNaN(pageId) || pageId <= 0) {
    throw new Error(`无效的页面ID: ${pageId}`)
  }

  return pageId
}

// ❌ 错误：缺少类型验证
const pageId = Number(route.params.id || route.query.id)
if (!pageId) {
  throw new Error('无效ID')  // 错误信息不明确
}
```

**表单参数验证：**

```typescript
// ✅ 使用表单验证规则
const formRules = {
  pageCode: [
    { required: true, message: '请输入页面编码', trigger: 'blur' },
    { pattern: /^[a-z][a-z0-9_]*$/, message: '编码格式不正确', trigger: 'blur' }
  ]
}
```

### 3.2 后端参数验证

**PathVariable 参数处理：**

```java
// ✅ 正确：添加参数验证和异常处理
@GetMapping("/{id}")
public Result<PageConfig> getPage(@PathVariable Long id) {
    if (id == null || id <= 0) {
        return Result.fail("无效的页面ID");
    }

    PageConfig config = pageService.getById(id);
    if (config == null) {
        return Result.fail("页面不存在");
    }

    return Result.success(config);
}

// ❌ 错误：缺少验证
@GetMapping("/{id}")
public Result<PageConfig> getPage(@PathVariable Long id) {
    return Result.success(pageService.getById(id));
}
```

**全局异常处理已配置：**

```java
@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public Result<Void> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
    String paramName = e.getName();
    String paramValue = e.getValue() != null ? e.getValue().toString() : "null";
    String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "未知";
    String message = String.format("参数 '%s' 的值 '%s' 无法转换为 %s 类型", paramName, paramValue, requiredType);
    log.error("参数类型转换失败: {}", message);
    return Result.fail(400, message);
}
```

---

## 4. 错误处理规范

### 4.1 前端错误处理

**统一错误处理：**

```typescript
// ✅ 推荐：详细的错误信息
try {
  const data = await getPage(pageId)
  pageConfig.value = data
} catch (error: any) {
  const errorMsg = error.message || '加载失败'
  console.error('页面加载错误:', { pageId, error })
  ElMessage.error(errorMsg)
  error.value = errorMsg
}

// ❌ 不推荐：吞掉错误
try {
  await getPage(pageId)
} catch (error) {
  console.log('出错了')  // 信息不足
}
```

### 4.2 后端错误处理

**业务异常：**

```java
// ✅ 使用业务异常
if (template.getIsSystem()) {
    throw new BusinessException(400, "系统模板不允许删除");
}

// ✅ 参数验证
if (pageId == null || pageId <= 0) {
    throw new BusinessException(400, "无效的页面ID");
}
```

---

## 5. 命名约定总结

### 5.1 文件命名

| 类型 | 命名规则 | 示例 |
|-----|---------|------|
| Vue组件 | PascalCase | `PageManage.vue` |
| TypeScript文件 | PascalCase | `api/page.ts` |
| 工具类 | PascalCase | `DateUtils.java` |

### 5.2 代码命名

| 类型 | 命名规则 | 示例 |
|-----|---------|------|
| 类名 | PascalCase | `PageConfigController` |
| 方法名 | camelCase | `getPageList` |
| 变量名 | camelCase | `pageId` |
| 常量 | UPPER_SNAKE_CASE | `MAX_PAGE_SIZE` |
| 路由路径 | kebab-case | `/page-manage` |
| API路径 | kebab-case | `/api/page-template` |

### 5.3 数据库字段

| 类型 | 命名规则 | 示例 |
|-----|---------|------|
| 表名 | snake_case | `low_page_config` |
| 字段名 | snake_case | `page_name` |
| 主键 | `id` | `id` |
| 外键 | `{资源}_id` | `page_id` |

---

## 6. 常见问题和解决方案

### 6.1 路由冲突问题

**问题：** 静态路由被动态路由错误匹配

**解决方案：**
1. 静态路由使用明确的功能名称
2. 动态路由放在最后
3. 添加路由元信息标记

```typescript
// ✅ 正确配置
{
  path: 'lowcode/PageManage',  // 明确的管理页面
  name: 'PageManage',
  meta: { type: 'static' }
}

// 动态路由使用更具体的路径
{
  path: 'page/publish/:id',
  name: 'PagePublish',
  meta: { type: 'dynamic' }
}
```

### 6.2 参数类型转换错误

**问题：** `参数 'id' 的值 'page' 无法转换为 Long 类型`

**原因：**
1. 路由配置不当，导致参数被错误解析
2. 前端传递了字符串 "page" 而不是数字

**解决方案：**
1. 检查路由配置，避免使用通用的路径段
2. 前端添加参数验证
3. 后端已有全局异常处理

### 6.3 避免的最佳实践

```typescript
// ❌ 不推荐
const id = route.params.id  // 直接使用，未验证
await api.get(id)           // 可能传非数字

// ✅ 推荐
const validateId = (id: any): number => {
  const num = typeof id === 'string' ? parseInt(id, 10) : id
  if (isNaN(num) || num <= 0) {
    throw new Error(`无效的ID: ${id}`)
  }
  return num
}

const id = validateId(route.params.id)
await api.get(id)
```

---

## 7. 代码审查检查清单

### 7.1 路由配置检查

- [ ] 路由路径符合命名规范
- [ ] 避免使用可能被误解为参数的路径段
- [ ] 动态路由参数使用具体的资源名称
- [ ] 添加必要的路由元信息

### 7.2 API 设计检查

- [ ] API路径符合 RESTful 规范
- [ ] 路径中资源类型明确
- [ ] 使用合适的 HTTP 方法
- [ ] 参数验证完善

### 7.3 参数验证检查

- [ ] 路径参数类型验证
- [ ] 查询参数格式验证
- [ ] 表单数据规则验证
- [ ] 错误信息清晰明确

### 7.4 错误处理检查

- [ ] 前端异常捕获和提示
- [ ] 后端业务异常处理
- [ ] 日志记录完整
- [ ] 用户友好的错误信息

---

## 8. 版本更新记录

| 版本 | 日期 | 说明 |
|-----|------|------|
| 1.0 | 2026-01-31 | 初始版本，解决路由参数转换错误问题 |

---

## 9. 参考文档

- [Vue Router 官方文档](https://router.vuejs.org/)
- [RESTful API 设计指南](https://restfulapi.net/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
