# 表单/表格设计器简化说明

## 表单设计器简化（已完成）

### 修改内容
1. ✅ 删除按钮配置标签页
2. ✅ 删除按钮相关的所有代码（变量、函数、样式）
3. ✅ 在 formConfig 中添加 componentCategory 和 componentTags 字段
4. ✅ 在表单属性中添加组件分类选择
5. ✅ 修改保存逻辑，不再保存按钮配置
6. ✅ 删除按钮相关的导入和类型定义

### 新增功能
- 组件分类选择（通用组件库 / 业务组件库）
- 组件标签输入（用逗号分隔）

## 表格设计器简化（待实施）

### 需要修改的文件
`frontend/src/views/lowcode/TableDesigner.vue`

### 主要修改步骤

#### 1. 删除按钮配置标签页
```vue
<!-- 删除这段代码 -->
<el-tab-pane label="按钮配置" name="buttons">
  ...
</el-tab-pane>
```

#### 2. 修改 tableConfig
```typescript
const tableConfig = reactive({
  tableName: '',
  tableCode: '',
  componentCategory: 'business' as 'common' | 'business',  // 新增
  componentTags: '' as string,                               // 新增
  dataSourceType: 'api',
  // ... 其他字段
})

// 移除按钮相关代码
// const tableButtons = ref<TableButton[]>([])
// const selectedButton = ref<TableButton | null>(null)
```

#### 3. 在表格属性中添加组件分类
```vue
<el-tab-pane label="表格属性" name="table">
  <el-form label-width="100px" size="small">
    <el-form-item label="表格名称">
      <el-input v-model="tableConfig.tableName" />
    </el-form-item>
    <el-form-item label="表格编码">
      <el-input v-model="tableConfig.tableCode" />
    </el-form-item>
    <el-form-item label="组件分类">
      <el-radio-group v-model="tableConfig.componentCategory">
        <el-radio label="common">通用组件库</el-radio>
        <el-radio label="business">业务组件库</el-radio>
      </el-radio-group>
      <div class="form-tip">
        通用组件可在所有页面复用，业务组件仅用于特定业务场景
      </div>
    </el-form-item>
    <el-form-item label="组件标签">
      <el-input
        v-model="tableConfig.componentTags"
        type="textarea"
        :rows="2"
        placeholder="请输入标签，用逗号分隔（如：system,list,report）"
      />
    </el-form-item>
    <!-- ... 其他属性 -->
  </el-form>
</el-tab-pane>
```

#### 4. 删除按钮相关函数
```typescript
// 删除这些函数
// const addTableButton = () => { ... }
// const selectButton = (button: TableButton) => { ... }
// const removeButton = (index: number) => { ... }
// const moveButtonUp = (index: number) => { ... }
// const moveButtonDown = (index: number) => { ... }
```

#### 5. 修改加载逻辑
```typescript
const loadTableConfig = async () => {
  // ...
  tableConfig.tableName = data.tableName
  tableConfig.tableCode = data.tableCode
  tableConfig.componentCategory = (data.componentCategory as any) || 'business'  // 新增
  tableConfig.componentTags = (data.componentTags as any) || ''                     // 新增
  // ...
}
```

#### 6. 修改保存逻辑
```typescript
const saveData: TableConfig = {
  tableName: tableConfig.tableName,
  tableCode: tableConfig.tableCode,
  componentCategory: tableConfig.componentCategory,  // 新增
  componentTags: tableConfig.componentTags,           // 新增
  // ... 其他字段
}

// 删除保存按钮配置的代码
// if (tableButtons.value.length > 0) {
//   await batchSaveButtonsByTableId(tableId.value!, buttonsToSave)
// }
```

#### 7. 删除导入
```typescript
// 删除这些导入
// import type { ButtonConfig } from '@/api/button'
// import { batchSaveButtonsByTableId, getButtonsByTableId } from '@/api/button'

// 删除类型定义
// interface TableButton extends ButtonConfig {
//   id: string
// }
```

#### 8. 删除样式
```scss
// 删除这些样式
// .buttons-config { ... }
// .button-list { ... }
// .button-item { ... }
```

### 注意事项
- 确保所有按钮相关的代码都已删除
- 确保保存和加载逻辑正确处理新字段
- 测试表格设计器的完整流程

## 后续工作

1. 适配渲染引擎（PageRender.vue 和 ButtonRenderer.vue）
2. 功能测试
3. 文档更新
