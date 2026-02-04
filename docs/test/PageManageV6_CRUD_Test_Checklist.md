# 页面管理 CRUD 功能测试清单

## 📋 实现概述

已为页面管理模块（PageManageV6.vue）添加完整的 CRUD 功能，包括：
- ✅ 列表查询（带搜索和分页）
- ✅ 新增页面（对话框表单）
- ✅ 编辑页面（对话框表单）
- ✅ 删除页面（带确认）
- ✅ 发布/取消发布
- ✅ 启用/禁用状态切换
- ✅ 预览功能

## 🚀 访问地址

- **前端地址**: http://localhost:3002/lowcode-v6/PageManageV6
- **后端 API**: http://localhost:8765/doc.html

## 🧪 测试清单

### 1. 列表查询测试

- [ ] **默认加载**
  - 访问页面后列表自动加载第一页数据
  - 默认每页显示 10 条记录

- [ ] **搜索功能**
  - 输入"页面名称"进行搜索
  - 输入"页面编码"进行搜索
  - 选择"页面类型"（列表页/表单页/详情页/自定义）
  - 选择"发布状态"（已发布/草稿）
  - 点击"搜索"按钮触发搜索
  - 点击"重置"按钮清空搜索条件

- [ ] **分页功能**
  - 切换页码（第 1、2、3 页...）
  - 修改每页显示条数（10/20/50/100）

### 2. 新增页面测试

- [ ] **打开对话框**
  - 点击"新建页面"按钮
  - 对话框标题显示"新建页面"

- [ ] **表单验证**
  - 页面名称：必填，2-50 个字符
  - 页面编码：必填，2-30 个字符，小写字母开头，只能包含小写字母、数字、下划线
  - 页面类型：必选（列表页/表单页/详情页/自定义）
  - 布局类型：可选（自由画布/上下布局/左右布局）
  - 描述：可选，最多 200 字符
  - 状态：开关（启用/禁用）

- [ ] **创建流程**
  1. 填写完整表单
  2. 点击"创建并设计"按钮
  3. 成功提示："创建成功"
  4. 自动跳转到自由画布设计器（`/lowcode/FreeCanvasDesigner/{newId}`）

- [ ] **错误处理**
  - 页面编码重复时显示错误提示
  - 表单验证失败时显示验证错误

### 3. 编辑页面测试

- [ ] **打开对话框**
  - 点击列表中的"编辑"按钮
  - 对话框标题显示"编辑页面"
  - 表单自动填充当前页面数据

- [ ] **更新流程**
  1. 修改表单内容
  2. 点击"保存"按钮
  3. 成功提示："更新成功"
  4. 对话框关闭，列表自动刷新

- [ ] **错误处理**
  - 页面编码与其他页面重复时显示错误提示
  - 更新失败时显示错误信息

### 4. 删除页面测试

- [ ] **删除确认**
  - 点击"删除"按钮
  - 弹出确认对话框：`确定要删除页面"{pageName}"吗？删除后无法恢复。`
  - 选择"取消"：对话框关闭，不执行删除
  - 选择"确认"：执行删除

- [ ] **删除成功**
  - 成功提示："删除成功"
  - 列表自动刷新

### 5. 发布/取消发布测试

- [ ] **发布页面**
  - 点击"发布"按钮（仅对未发布页面显示）
  - 弹出输入对话框：`请输入页面路由路径（如：/pages/custom）`
  - 输入路由路径（必须以 `/` 开头）
  - 点击"确认发布"
  - 成功提示："发布成功"
  - 列表中的状态变为"已发布"
  - 操作按钮变为"取消发布"

- [ ] **取消发布**
  - 点击"取消发布"按钮（仅对已发布页面显示）
  - 弹出确认对话框：`取消发布后页面将不再可访问，是否继续？`
  - 点击"确认"
  - 成功提示："已取消发布"
  - 列表中的状态变为"草稿"
  - 操作按钮变为"发布"

### 6. 启用/禁用状态测试

- [ ] **状态切换**
  - 点击列表中的"启用状态"开关
  - 成功提示："状态更新成功"
  - 开关状态立即更新

- [ ] **错误恢复**
  - 更新失败时开关自动恢复原状态
  - 显示错误提示

### 7. 预览功能测试

- [ ] **预览已配置页面**
  - 点击"预览"按钮
  - 弹出预览对话框
  - 显示页面渲染效果

- [ ] **预览空配置页面**
  - 点击"预览"按钮
  - 提示："页面配置为空，无法预览"

- [ ] **全屏切换**
  - 点击"全屏"按钮：对话框全屏显示
  - 点击"退出全屏"按钮：恢复对话框大小

- [ ] **配置格式错误**
  - 点击"预览"按钮
  - 提示："页面配置格式错误"

## 📊 表格字段说明

| 字段名 | 说明 | 数据来源 |
|--------|------|----------|
| ID | 页面 ID | `id` |
| 页面名称 | 页面显示名称 | `pageName` |
| 页面编码 | 页面唯一编码 | `pageCode` |
| 页面类型 | list/form/detail/custom | `pageType` |
| 描述 | 页面描述 | `remark` |
| 状态 | 已发布/草稿 | `published` |
| 启用状态 | 启用/禁用开关 | `status` (1/0) |
| 更新时间 | 最后更新时间 | `updateTime` |

## 🔧 API 接口说明

### 1. 分页查询
```typescript
GET /api/page/list
参数: current, size, pageName?, pageCode?, pageType?, published?
返回: { records: [...], total: 100, current: 1, size: 10 }
```

### 2. 创建页面
```typescript
POST /api/page
Body: { pageName, pageCode, pageType, layoutType, configJson, configTemplate, configVersion, remark, status }
返回: 新创建的页面 ID (number)
```

### 3. 更新页面
```typescript
PUT /api/page/{id}
Body: { pageName, pageCode, pageType, layoutType, configJson, configTemplate, configVersion, remark, status }
返回: void
```

### 4. 删除页面
```typescript
DELETE /api/page/{id}
返回: void
```

### 5. 发布页面
```typescript
POST /api/page/{id}/publish
Body: { routePath: string }
返回: void
```

### 6. 取消发布
```typescript
POST /api/page/{id}/unpublish
返回: void
```

### 7. 获取页面详情
```typescript
GET /api/page/{id}
返回: { id, pageName, pageCode, configJson, configTemplate, ... }
```

## ⚠️ 注意事项

1. **响应拦截器已自动解包**
   ```typescript
   // ✅ 正确用法
   const res = await getPageList({ current: 1, size: 10 })
   console.log(res.records)  // 直接访问业务数据

   // ❌ 错误用法
   console.log(res.data.records)  // undefined
   ```

2. **页面编码唯一性**
   - 创建和更新时会检查编码重复
   - 编码格式：`/^[a-z][a-z0-9_]*$/`

3. **状态值类型**
   - `status` 字段使用数字类型：`1` = 启用，`0` = 禁用
   - `published` 字段使用布尔类型：`true` = 已发布，`false` = 草稿

4. **错误处理**
   - 所有 API 调用都包含 try-catch 错误处理
   - 失败时显示友好的错误提示

## 🐛 已知问题

### 编辑按钮行为
当前实现中，点击"编辑"按钮会打开对话框编辑页面基本信息。

**如需跳转到设计器**，可以修改 `handleEdit` 函数：
```typescript
async function handleEdit(row: any) {
  // 跳转到设计器
  router.push(`/lowcode/FreeCanvasDesigner/${row.id}`)
}
```

## 📝 后续优化建议

1. **批量操作**
   - 批量删除
   - 批量发布/取消发布
   - 批量启用/禁用

2. **高级搜索**
   - 按创建时间范围搜索
   - 按更新时间范围搜索
   - 多条件组合搜索

3. **列表优化**
   - 列表排序功能
   - 列宽调整
   - 列显示/隐藏控制

4. **操作日志**
   - 记录谁在何时创建了页面
   - 记录谁在何时修改了页面
   - 记录发布/取消发布历史

## 📚 相关文件

- **前端组件**: `frontend/src/views/lowcode-v6/PageManageV6.vue`
- **API 接口**: `frontend/src/api/page.ts`
- **类型定义**: `frontend/src/types/page-v6.ts`
- **后端 Controller**: `backend/src/main/java/com/lowcode/controller/PageConfigController.java`
- **后端 Service**: `backend/src/main/java/com/lowcode/service/impl/LowPageConfigServiceImpl.java`
