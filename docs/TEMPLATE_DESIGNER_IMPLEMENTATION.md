# 基于模板的可视化拖拽编辑 - 实现文档

## 概述

已实现基于模板的页面设计器系统，用户可以从预设模板中选择，然后在可视化编辑器中通过拖拽组件来构建页面。

## 已实现的功能

### ✅ 核心功能（已实现）
- 模板注册机制 - 支持动态加载模板
- 设计器组件 - 可视化拖拽编辑
- 渲染器组件 - 运行时页面渲染
- 真实组件预览 - 表单/表格/按钮真实渲染
- 保存功能 - 支持创建和编辑页面
- 页面名称编辑 - 可点击标题编辑

### ✅ 组件支持（已实现）
- **表单组件** - 可拖拽到查询区或内容区，显示真实表单字段
- **表格组件** - 可拖拽到内容区，显示带数据的表格
- **按钮组件** - 可拖拽到工具栏，显示按钮样式

## 已创建的文件

### 核心类型定义
- `frontend/src/types/template.ts` - 模板相关类型定义（PageConfigData、ComponentRef、TemplateMeta、ButtonConfig）
- `frontend/src/types/component.ts` - 组件相关类型定义（DragState、AreaConfig）

### 模板配置
- `frontend/src/config/templates/registry.ts` - 模板注册表（定义了3个模板：标准列表页、左树右表页、表单页）
- `frontend/src/config/templates/index.ts` - 模板导出

### 模板设计器组件
- `frontend/src/designer-templates/BaseTemplate.vue` - 基础模板组件（提供通用功能）
- `frontend/src/designer-templates/StandardListTemplate.vue` - 标准列表页设计器
- `frontend/src/designer-templates/TreeTableTemplate.vue` - 左树右表页设计器
- `frontend/src/designer-templates/FormPageTemplate.vue` - 表单页设计器

### 模板渲染器组件
- `frontend/src/render-templates/StandardListRender.vue` - 标准列表页渲染器
- `frontend/src/render-templates/TreeTableRender.vue` - 左树右表页渲染器
- `frontend/src/render-templates/FormPageRender.vue` - 表单页渲染器

### 设计器辅助组件
- `frontend/src/components/designer/DesignArea.vue` - 设计区域组件（支持拖拽）
- `frontend/src/components/designer/PageComponentLibrary.vue` - 页面组件库面板（支持表单、表格、按钮）
- `frontend/src/components/designer/PropertyPanel.vue` - 组件属性面板
- `frontend/src/components/designer/TemplateSelectDialog.vue` - 模板选择对话框
- `frontend/src/components/designer/FormRenderDesign.vue` - 表单真实预览组件
- `frontend/src/components/designer/TableRenderDesign.vue` - 表格真实预览组件

### 页面组件
- `frontend/src/views/lowcode/PageDesignerV5.vue` - 新的页面设计器（V5版本，支持保存）
- `frontend/src/views/lowcode/PageRenderV5.vue` - 新的页面渲染器（V5版本）
- `frontend/src/views/lowcode/TemplateTest.vue` - 模板测试入口页面

## 如何使用

### 1. 启动项目

```bash
# 前端
cd lowcode-platform/frontend
npm run dev

# 后端
cd lowcode-platform/backend
mvn spring-boot:run
```

### 2. 访问模板测试页面

在浏览器中访问：`http://localhost:3000/lowcode/TemplateTest`

这个页面会显示所有可用的模板，点击任一模板卡片即可进入对应的设计器。

### 3. 从页面管理进入

1. 访问 `http://localhost:3000/lowcode/PageManage`
2. 点击"新增页面（V5模板）"按钮
3. 在弹出的对话框中选择模板
4. 进入设计器

### 4. 设计器使用

设计器布局为三栏式：

**左侧 - 组件库**
- 显示所有可用的表单配置和表格配置
- 可以拖拽组件到中间的设计区域

**中间 - 设计画布**
- 显示当前模板的布局结构
- 不同区域有明确的标识（查询区、内容区、工具栏等）
- 支持从组件库拖拽组件到区域
- 已放置的组件会显示预览

**右侧 - 属性面板**
- 当选中某个组件时，显示其属性
- 可以修改组件的自定义属性和样式

### 5. 保存和预览

- 点击"保存"按钮保存配置
- 点击"预览"按钮可以在新窗口中预览页面效果

## 配置格式（v5）

新的配置格式如下：

```json
{
  "version": "v5",
  "templateId": "standard-list",
  "components": {
    "search": [
      {
        "id": "comp_001",
        "type": "form",
        "refId": 123,
        "name": "用户搜索表单"
      }
    ],
    "content": [
      {
        "id": "comp_002",
        "type": "table",
        "refId": 456,
        "name": "用户列表表格",
        "props": {
          "height": "500px"
        }
      }
    ]
  }
}
```

## 添加新模板

要添加新的模板，需要：

1. 在 `frontend/src/designer-templates/` 下创建新的设计器组件
2. 在 `frontend/src/render-templates/` 下创建对应的渲染器组件
3. 在 `frontend/src/config/templates/registry.ts` 中注册模板

例如：

```typescript
// registry.ts
{
  id: 'custom-template',
  name: '自定义模板',
  type: 'custom',
  category: '高级',
  description: '我的自定义模板',

  designer: () => import('@/designer-templates/CustomTemplate.vue'),
  renderer: () => import('@/render-templates/CustomRender.vue')
}
```

## 技术架构

### 核心概念

- **模板**：一个独立的 Vue 组件，定义了页面的布局结构
- **设计器**：用于编辑页面配置的模板组件
- **渲染器**：用于运行时渲染页面的模板组件
- **组件库**：已有的表单和表格配置，可以被拖拽到页面中
- **组件引用**：页面配置中不直接存储组件配置，而是存储引用ID

### 数据流

```
用户选择模板 → 加载模板设计器组件 → 拖拽组件到区域 → 生成配置 → 保存到数据库
                                                            ↓
预览/运行 ← 加载渲染器组件 ← 解析配置 ← 加载组件配置 ← 发布页面
```

### 设计器组件通信

- 使用 `provide/inject` 在模板组件内部共享状态
- 使用事件传递组件选择信息
- 使用 v-model 双向绑定组件数据

## 后续开发建议

### 短期

1. **实现保存功能**：连接后端API，保存页面配置
2. **完善组件预览**：在 DesignArea 中显示真实的组件预览
3. **添加更多模板**：如标签页模板、图表模板等
4. **支持按钮组件**：从组件库拖拽按钮到工具栏

### 中期

1. **组件属性编辑**：在属性面板中编辑组件的详细属性
2. **样式自定义**：支持自定义区域的样式
3. **撤销/重做**：添加操作历史记录
4. **复制/粘贴组件**：支持复制组件配置

### 长期

1. **模板市场**：支持导入/导出模板
2. **可视化搭建**：支持通过拖拽搭建自定义模板
3. **版本控制**：支持页面配置的版本管理
4. **组件扩展**：支持自定义组件类型

## 兼容性

- 新的 v5 配置格式与现有的 v3 格式**不兼容**
- 现有的 PageDesigner 继续使用 v3 格式
- 新功能只在 PageDesignerV5 中可用
- 旧页面可以继续正常使用

## 注意事项

1. **开发模式**：目前处于开发阶段，保存功能尚未实现
2. **数据模拟**：组件库数据通过 API 获取，需要后端支持
3. **性能优化**：大量组件时需要考虑虚拟滚动
4. **错误处理**：需要添加更完善的错误提示和边界情况处理

## 路由配置

已添加以下路由：

- `/lowcode/TemplateTest` - 模板测试页面
- `/lowcode/PageDesignerV5` - V5 页面设计器
- `/lowcode/PageDesignerV5?id=:pageId` - 编辑已有页面
- `/page/previewV5/:id` - V5 页面预览

## 相关 API

需要在后端实现以下 API（目前使用模拟数据）：

```typescript
// 创建页面
POST /api/page
{
  pageName: string
  pageCode: string
  pageType: string
  configTemplate: string  // JSON字符串
}

// 更新页面
PUT /api/page/{id}

// 获取页面
GET /api/page/{id}

// 获取表单列表
GET /api/form/list

// 获取表格列表
GET /api/table/list
```

## 总结

该实现提供了一个灵活的、可扩展的模板化页面设计系统。前端开发者可以通过编写 Vue 组件来添加新模板，业务用户可以通过可视化拖拽快速构建页面，无需编写代码。

### 新增文件（22个）

**类型定义（2个）**
- types/template.ts
- types/component.ts

**模板配置（2个）**
- config/templates/registry.ts
- config/templates/index.ts

**设计器组件（4个）**
- designer-templates/BaseTemplate.vue
- designer-templates/StandardListTemplate.vue
- designer-templates/TreeTableTemplate.vue
- designer-templates/FormPageTemplate.vue

**渲染器组件（3个）**
- render-templates/StandardListRender.vue
- render-templates/TreeTableRender.vue
- render-templates/FormPageRender.vue

**辅助组件（6个）**
- components/designer/DesignArea.vue
- components/designer/PageComponentLibrary.vue
- components/designer/PropertyPanel.vue
- components/designer/TemplateSelectDialog.vue
- components/designer/FormRenderDesign.vue
- components/designer/TableRenderDesign.vue

**页面组件（3个）**
- views/lowcode/PageDesignerV5.vue
- views/lowcode/PageRenderV5.vue
- views/lowcode/TemplateTest.vue

**文档（1个）**
- docs/TEMPLATE_DESIGNER_IMPLEMENTATION.md

**路由修改（1个）**
- router/index.ts - 添加了V5相关路由

**页面管理修改（1个）**
- views/lowcode/PageManage.vue - 添加V5模板入口

### 已完成任务 ✅

1. ✅ 实现页面保存API - 连接后端，支持创建和编辑
2. ✅ 完善组件真实预览 - 显示真实的表单、表格渲染
3. ✅ 添加按钮组件支持 - 支持拖拽按钮到工具栏
