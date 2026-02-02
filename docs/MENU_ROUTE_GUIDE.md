# 菜单与路由机制说明

## 目录
1. [菜单配置机制](#菜单配置机制)
2. [组件路径映射](#组件路径映射)
3. [完整流程](#完整流程)
4. [常见问题](#常见问题)

---

## 菜单配置机制

### 数据库菜单表 sys_menu

| 字段 | 说明 | 示例 |
|-----|------|------|
| id | 菜单ID | 5 |
| parent_id | 父菜单ID | 1 |
| menu_name | 菜单名称 | 字典管理 |
| menu_code | 菜单编码（唯一） | system:dict |
| menu_type | 菜单类型：1=目录，2=菜单，3=按钮 | 2 |
| route_path | 路由路径 | /system/dict |
| component_path | 组件路径 | /views/system/DictManage.vue |
| icon | 图标名称 | Collection |
| status | 状态：1=启用，0=禁用 | 1 |

### 菜单类型说明

| 类型 | menu_type | 说明 | 是否有 component_path |
|-----|-----------|------|----------------------|
| 目录 | 1 | 一级菜单，展开后显示子菜单 | 否 |
| 菜单 | 2 | 可点击的菜单项，对应一个页面 | 是 |
| 按钮 | 3 | 页面内的操作按钮 | 否 |

---

## 组件路径映射

### componentMap.ts 的作用

`frontend/src/router/componentMap.ts` 是一个**组件路径映射表**，将字符串路径映射到实际的组件导入函数。

```typescript
export const componentMap: Record<string, () => Promise<any>> = {
  '/views/system/DictManage.vue': () => import('@/views/system/DictManage.vue'),
  '/views/lowcode/DropdownManage.vue': () => import('@/views/lowcode/DropdownManage.vue'),
  // ...
}

export const getComponent = (componentPath: string) => {
  const normalizedPath = componentPath.startsWith('/') ? componentPath : `/${componentPath}`
  return componentMap[normalizedPath] || null
}
```

### 为什么需要 componentMap？

Vite 不支持完全动态的导入路径，以下写法会失败：

```typescript
// ❌ 不工作
const component = () => import(`@/views/${menu.componentPath}.vue`)
```

必须使用静态路径：

```typescript
// ✅ 正确方式
const component = () => import('@/views/system/DictManage.vue')
```

### 生成 componentMap

新增 `.vue` 页面后，运行以下命令自动生成映射：

```bash
cd frontend
npx tsx scripts/generate-component-map.ts
```

或者（如果配置了 npm 脚本）：
```bash
npm run gen:component-map
```

### componentPath 的正确格式

| 格式 | 是否正确 | 说明 |
|-----|---------|------|
| `/views/system/DictManage.vue` | ✅ | 完整路径，带前导斜杠 |
| `views/system/DictManage.vue` | ✅ | 完整路径，不带前导斜杠（会自动添加） |
| `system/DictManage.vue` | ❌ | 缺少 views 前缀 |
| `/system/DictManage.vue` | ❌ | 缺少 views 前缀 |

---

## 完整流程

### 前端开发人员的工作流程

1. **创建页面文件**
   ```
   frontend/src/views/system/DictManage.vue
   ```

2. **生成组件映射**
   ```bash
   cd frontend
   npx tsx scripts/generate-component-map.ts
   ```

3. **验证 componentMap**
   打开 `frontend/src/router/componentMap.ts`，确认包含新组件：
   ```typescript
   '/views/system/DictManage.vue': () => import('@/views/system/DictManage.vue'),
   ```

4. **提交代码（包含 componentMap.ts 的更新）**

### 业务人员的工作流程

1. **登录系统，进入菜单管理**

2. **创建菜单**
   - 菜单名称：字典管理
   - 菜单编码：system:dict
   - 菜单类型：菜单
   - 父菜单：系统管理
   - 路由路径：/system/dict
   - **组件路径**：`/views/system/DictManage.vue` ⚠️ 必须与 componentMap 一致
   - 图标：Collection
   - 状态：启用

3. **保存并刷新页面**

### 路由加载机制

**前端静态路由（router/index.ts）：**
- 登录页面 (`/login`)
- 首页 (`/home`)
- 页面预览 (`/page/preview/:id`，隐藏在菜单中)

**动态路由（从数据库加载）：**
- 所有其他页面路由均通过 `addDynamicRoutes()` 从 `sys_menu` 表动态加载
- 菜单类型为 2（菜单）且配置了 `route_path` 的菜单项会被注册为路由
- 路由优先级：动态路由会被添加到 Layout 下，与静态路由同级

---

## 常见问题

### Q1: 菜单配置了但页面不显示？

**可能原因：**

1. **componentPath 填写错误**
   - 检查是否与 `componentMap.ts` 中的键完全一致
   - 必须包含 `/views/` 前缀和 `.vue` 后缀

2. **componentMap 没有更新**
   - 运行 `npx tsx scripts/generate-component-map.ts` 重新生成

3. **菜单状态未启用**
   - 检查 `status` 字段是否为 1

4. **路由冲突**
   - 检查 `router/index.ts` 中是否已存在相同路径的静态路由

### Q2: 如何检查组件路径是否正确？

打开浏览器控制台，查看是否有警告：

```
✗ 菜单 xxx 使用占位页面
  - componentPath: /views/system/DictManage.vue
  - pageId: null
```

如果看到这个警告，说明 `componentPath` 在 `componentMap` 中找不到。

### Q3: 能否不手动维护 componentMap？

**当前方案**：需要手动运行脚本生成。

**未来改进方向**：
- 方案1：使用 Vite 的 `import.meta.glob` 动态导入所有组件
- 方案2：在菜单管理页面提供组件选择器，从实际文件中选择
- 方案3：约定路由规则，自动根据 routePath 推导 componentPath

### Q4: 业务人员填错路径怎么办？

**预防措施**：
1. 在菜单管理页面提供组件路径下拉选择
2. 添加路径验证，输入时实时检查 componentMap

**补救措施**：
1. 检查浏览器控制台警告
2. 在菜单管理页面显示组件加载状态

---

## 最佳实践

### 命名规范

| 类型 | 规范 | 示例 |
|-----|------|------|
| Vue 文件 | PascalCase | `DictManage.vue` |
| 文件路径 | 与路由对应 | `/views/system/DictManage.vue` |
| 路由路径 | PascalCase（管理页面） | `/system/DictManage` |
| 菜单编码 | `模块:功能` | `system:dict` |

### 开发流程

1. 前端开发人员创建页面
2. 运行生成脚本更新 componentMap
3. 提交代码（包含 componentMap.ts 的更新）
4. 通知业务人员可以配置菜单

### 验证清单

- [ ] 页面文件存在
- [ ] componentMap 已更新
- [ ] 菜单配置正确
- [ ] componentPath 与 componentMap 一致
- [ ] 菜单状态已启用
- [ ] 刷新页面后检查