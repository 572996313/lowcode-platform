# 表格标准页面 - 接口对接文档

> 本文档定义了"表格标准页面"前后端对接的全部接口。前端已按此协议实现，当前使用 mock 数据，后端实现后只需将 API 函数中的 `Promise.resolve(mockXxx)` 替换为 `request.get/post(...)` 即可。

## 接口总览

| 序号 | 方法   | 路径                        | 说明           | 优先级 |
|------|--------|-----------------------------|----------------|--------|
| 1    | GET    | /api/table-standard/config  | 获取页面配置   | P0     |
| 2    | GET    | /api/table-standard/page    | 分页查询数据   | P0     |
| 3    | POST   | /api/table-standard         | 新增数据       | P0     |
| 4    | PUT    | /api/table-standard/{id}    | 更新数据       | P0     |
| 5    | DELETE | /api/table-standard/{id}    | 删除数据       | P0     |

---

## 1. 获取页面配置

**GET** `/api/table-standard/config`

返回页面的完整配置：工具栏按钮、搜索字段、表格列、表单字段、表格样式。前端根据这些配置动态渲染整个页面。

### 请求参数

无（后续可扩展 `?pageCode=xxx` 支持多页面复用）

### 响应结构

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "pageCode": "table_standard_demo",
    "pageName": "表格标准页面",
    "toolbar": { ... },
    "searchFields": [ ... ],
    "tableColumns": [ ... ],
    "tableConfig": { ... },
    "formFields": [ ... ]
  }
}
```

### toolbar - 工具栏按钮配置

| 字段     | 类型   | 必填 | 说明                                                        |
|----------|--------|------|-------------------------------------------------------------|
| label    | string | 是   | 按钮文本                                                    |
| btnType  | string | 否   | 按钮样式：primary/success/warning/danger/info               |
| icon     | string | 否   | 图标名称（Element Plus 图标组件名，如 Plus/Download/Delete） |
| action   | string | 是   | 操作标识：`add`(新增)/`export`(导出)/`batchDelete`(批量删除)/自定义 |

**示例：**

```json
"toolbar": {
  "buttons": [
    { "label": "新增", "btnType": "primary", "icon": "Plus", "action": "add" },
    { "label": "导出", "icon": "Download", "action": "export" },
    { "label": "批量删除", "btnType": "danger", "icon": "Delete", "action": "batchDelete" }
  ]
}
```

### searchFields - 搜索字段配置

| 字段        | 类型     | 必填 | 说明                                      |
|------------|----------|------|------------------------------------------|
| field      | string   | 是   | 字段名，对应查询参数的 key                  |
| label      | string   | 是   | 显示标签                                  |
| type       | string   | 是   | 组件类型：`input`/`select`/`date`/`daterange`/`number` |
| placeholder| string   | 否   | 占位文本                                  |
| clearable  | boolean  | 否   | 是否可清空，默认 true                      |
| options    | array    | 否   | 下拉选项（type=select 时必填）              |
| width      | number   | 否   | 组件宽度（px）                             |
| defaultValue| any     | 否   | 默认值                                    |

**options 子项：**

| 字段   | 类型   | 说明     |
|--------|--------|----------|
| label  | string | 显示文本 |
| value  | any    | 选项值   |

**示例：**

```json
"searchFields": [
  { "field": "name", "label": "名称", "type": "input", "placeholder": "请输入名称", "clearable": true },
  { "field": "status", "label": "状态", "type": "select", "placeholder": "请选择状态", "clearable": true, "width": 120,
    "options": [
      { "label": "启用", "value": 1 },
      { "label": "禁用", "value": 0 }
    ]
  },
  { "field": "createDate", "label": "创建日期", "type": "daterange", "clearable": true }
]
```

### tableColumns - 表格列配置

| 字段               | 类型    | 必填 | 说明                                                         |
|-------------------|---------|------|--------------------------------------------------------------|
| prop              | string  | 否*  | 字段名（action 类型不需要）                                    |
| label             | string  | 是   | 列标题                                                       |
| width             | number  | 否   | 列宽度（px）                                                  |
| minWidth          | number  | 否   | 最小列宽（px）                                                 |
| align             | string  | 否   | 对齐：`left`/`center`/`right`                                  |
| fixed             | string  | 否   | 固定列：`left`/`right`                                         |
| showOverflowTooltip| boolean| 否   | 超出显示 tooltip                                               |
| type              | string  | 否   | 列渲染类型：`text`(默认)/`tag`/`action`/`date`                 |
| tagConfig         | object  | 否   | type=tag 时的标签映射配置                                       |
| actionConfig      | object  | 否   | type=action 时的按钮配置                                       |

**tagConfig 结构：**

```json
{
  "mapping": {
    "1": { "text": "启用", "type": "success" },
    "0": { "text": "禁用", "type": "danger" }
  }
}
```

> `mapping` 的 key 是字段值的字符串形式，`type` 对应 el-tag 的 type 属性（success/danger/warning/info/""）。

**actionConfig 结构：**

```json
{
  "buttons": [
    { "label": "编辑", "btnType": "primary", "size": "small", "action": "edit" },
    { "label": "删除", "btnType": "danger", "size": "small", "action": "delete" }
  ]
}
```

| 字段     | 类型   | 说明                                                    |
|----------|--------|---------------------------------------------------------|
| label    | string | 按钮文本                                                |
| btnType  | string | 按钮样式：primary/success/warning/danger/info            |
| size     | string | 按钮大小：large/default/small                           |
| action   | string | 操作标识：`edit`(编辑)/`delete`(删除)/自定义              |
| showWhen | string | 显示条件，字段名为 true 时显示（预留，当前未使用）         |

**完整示例：**

```json
"tableColumns": [
  { "prop": "name", "label": "名称", "width": 150 },
  { "prop": "code", "label": "编码", "width": 150 },
  { "prop": "category", "label": "分类", "width": 120 },
  { "prop": "description", "label": "描述", "showOverflowTooltip": true },
  {
    "prop": "status", "label": "状态", "width": 100, "align": "center",
    "type": "tag",
    "tagConfig": {
      "mapping": {
        "1": { "text": "启用", "type": "success" },
        "0": { "text": "禁用", "type": "danger" }
      }
    }
  },
  { "prop": "createTime", "label": "创建时间", "width": 180 },
  {
    "label": "操作", "width": 180, "align": "center", "fixed": "right",
    "type": "action",
    "actionConfig": {
      "buttons": [
        { "label": "编辑", "btnType": "primary", "size": "small", "action": "edit" },
        { "label": "删除", "btnType": "danger", "size": "small", "action": "delete" }
      ]
    }
  }
]
```

### tableConfig - 表格整体配置

```json
{
  "border": true,
  "stripe": true,
  "size": "default",
  "showPagination": true,
  "pageSize": 10,
  "pageSizes": [10, 20, 50, 100],
  "showIndex": false,
  "showSelection": false
}
```

| 字段           | 类型     | 默认值    | 说明                                |
|----------------|----------|-----------|-------------------------------------|
| border         | boolean  | true      | 是否显示边框                         |
| stripe         | boolean  | true      | 是否斑马纹                           |
| size           | string   | "default" | 表格尺寸：large/default/small       |
| showPagination | boolean  | true      | 是否显示分页                          |
| pageSize       | number   | 10        | 默认每页条数                          |
| pageSizes      | number[] | [10,20,50,100] | 分页选项                        |
| showIndex      | boolean  | false     | 是否显示序号列                        |
| showSelection  | boolean  | false     | 是否显示多选列                        |

### formFields - 表单字段配置

| 字段           | 类型     | 必填 | 说明                                                    |
|----------------|----------|------|---------------------------------------------------------|
| field          | string   | 是   | 字段名                                                  |
| label          | string   | 是   | 显示标签                                                |
| type           | string   | 是   | 组件类型：`input`/`select`/`textarea`/`number`/`switch`/`date` |
| placeholder    | string   | 否   | 占位文本                                                |
| required       | boolean  | 否   | 是否必填（简化写法，等于 required rule）                   |
| rules          | array    | 否   | 自定义校验规则（优先于 required）                          |
| options        | array    | 否   | 下拉选项（type=select 时）                                |
| disabledOnEdit | boolean  | 否   | 编辑模式下是否禁用                                        |
| rows           | number   | 否   | textarea 行数，默认 3                                     |
| activeValue    | any      | 否   | switch 开启值，默认 true                                  |
| inactiveValue  | any      | 否   | switch 关闭值，默认 false                                 |

**rules 子项：**

| 字段      | 类型    | 说明                                     |
|-----------|---------|------------------------------------------|
| required  | boolean | 是否必填                                  |
| message   | string  | 校验失败提示                              |
| trigger   | string  | 触发方式：blur/change                     |
| pattern   | string  | 正则表达式（字符串形式，前端会转 RegExp）  |
| min       | number  | 最小长度                                  |
| max       | number  | 最大长度                                  |

**示例：**

```json
"formFields": [
  {
    "field": "name", "label": "名称", "type": "input", "placeholder": "请输入名称",
    "required": true,
    "rules": [{ "required": true, "message": "请输入名称", "trigger": "blur" }]
  },
  {
    "field": "code", "label": "编码", "type": "input", "placeholder": "请输入编码（英文）",
    "required": true, "disabledOnEdit": true,
    "rules": [
      { "required": true, "message": "请输入编码", "trigger": "blur" },
      { "pattern": "^[a-zA-Z0-9_]+$", "message": "编码只能包含字母、数字和下划线", "trigger": "blur" }
    ]
  },
  {
    "field": "category", "label": "分类", "type": "select", "placeholder": "请选择分类",
    "required": true,
    "rules": [{ "required": true, "message": "请选择分类", "trigger": "change" }],
    "options": [
      { "label": "分类A", "value": "分类A" },
      { "label": "分类B", "value": "分类B" },
      { "label": "分类C", "value": "分类C" }
    ]
  },
  { "field": "description", "label": "描述", "type": "textarea", "placeholder": "请输入描述", "rows": 3 },
  { "field": "status", "label": "状态", "type": "switch", "activeValue": 1, "inactiveValue": 0 }
]
```

---

## 2. 分页查询数据

**GET** `/api/table-standard/page`

### 请求参数（Query）

| 参数    | 类型   | 必填 | 说明         |
|---------|--------|------|--------------|
| current | number | 是   | 当前页码     |
| size    | number | 是   | 每页条数     |
| {field} | any    | 否   | 搜索字段（由 searchFields 配置决定） |

> `{field}` 是动态的，对应 searchFields 中定义的 field 名称。例如 searchFields 配置了 name、status，则支持 `name=xxx&status=1` 查询参数。

### 响应结构

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "records": [
      {
        "id": 1,
        "name": "示例数据1",
        "code": "DEMO_001",
        "category": "分类A",
        "description": "这是描述信息",
        "status": 1,
        "createTime": "2026-01-15 10:30:00"
      }
    ],
    "current": 1,
    "size": 10,
    "pages": 10
  }
}
```

> records 中的字段由 tableColumns 配置决定，后端应返回所有 tableColumns 中 prop 对应的字段。

---

## 3. 新增数据

**POST** `/api/table-standard`

### 请求体

```json
{
  "name": "新数据",
  "code": "NEW_001",
  "category": "分类A",
  "description": "描述信息",
  "status": 1
}
```

> 字段由 formFields 配置决定。

### 响应结构

```json
{
  "code": 200,
  "message": "success",
  "data": 1
}
```

> `data` 返回新增记录的 ID。

---

## 4. 更新数据

**PUT** `/api/table-standard/{id}`

### 路径参数

| 参数 | 类型   | 说明   |
|------|--------|--------|
| id   | number | 记录ID |

### 请求体

```json
{
  "name": "修改后的名称",
  "category": "分类B",
  "status": 0
}
```

### 响应结构

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 5. 删除数据

**DELETE** `/api/table-standard/{id}`

### 路径参数

| 参数 | 类型   | 说明   |
|------|--------|--------|
| id   | number | 记录ID |

### 响应结构

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 后端实现指引

### 方案一：单表配置（推荐起步）

在数据库中创建一张 `low_table_page_config` 表存储页面配置 JSON：

```sql
CREATE TABLE low_table_page_config (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  page_code VARCHAR(64) NOT NULL COMMENT '页面编码',
  page_name VARCHAR(128) NOT NULL COMMENT '页面名称',
  config_json TEXT COMMENT '完整页面配置JSON（searchFields + tableColumns + tableConfig + formFields）',
  data_source_config VARCHAR(256) COMMENT '数据源配置（表名或SQL）',
  status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_page_code (page_code)
) COMMENT='表格页面配置表';
```

### 方案二：拆表配置（可扩展）

将 searchFields、tableColumns、formFields 分别存表，支持复用和独立管理。

### 前端切换 Mock → 真实接口

只需修改 `frontend/src/api/table-standard.ts` 中每个函数：

```typescript
// Mock 版本（当前）
export const getTableStandardConfig = () => {
  return Promise.resolve(mockPageConfig)
}

// 真实接口版本（后端就绪后替换）
export const getTableStandardConfig = () => {
  return request.get<PageConfigResponse>('/table-standard/config')
}
```

每个函数内部都已用 `// TODO: 替换为真实后端接口` 标注，全局搜索即可定位所有需要替换的位置。
