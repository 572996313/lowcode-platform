# 低代码平台重构实施指南

## 已完成的工作

### 1. 数据库表设计（SQL脚本）

SQL脚本已创建在 `docs/sql/refactor/` 目录：

- `001_create_dataset_config.sql` - 数据集配置表
- `002_create_field_metadata.sql` - 字段元数据表
- `003_create_page_config_new.sql` - 页面配置表（重构版）
- `004_create_form_config_new.sql` - 表单配置表（重构版）
- `005_create_table_config_new.sql` - 表格配置表（重构版）

### 2. 后端实现

#### 实体类（Entity）
- `DatasetConfig.java` - 数据集配置实体
- `FieldMetadata.java` - 字段元数据实体
- `PageConfigNew.java` - 页面配置实体（重构版）
- `FormConfigNew.java` - 表单配置实体（重构版）
- `TableConfigNew.java` - 表格配置实体（重构版）

#### Mapper接口
- `DatasetConfigMapper.java`
- `FieldMetadataMapper.java`
- `PageConfigNewMapper.java`
- `FormConfigNewMapper.java`
- `TableConfigNewMapper.java`

#### DTO类
- `CreateFromTableRequest.java` - 创建数据集请求（表）
- `CreateFromSQLRequest.java` - 创建数据集请求（SQL）
- `CreateFromAPIRequest.java` - 创建数据集请求（API）

#### Service层
- `IDatasetConfigService.java` + `DatasetConfigServiceImpl.java` - 数据集核心服务
  - 自动扫描表结构
  - 字段元数据提取
  - 数据源执行（Table/View/SQL/API）
- `IFieldMetadataService.java` + `FieldMetadataServiceImpl.java` - 字段元数据服务
- `IPageConfigNewService.java` + `PageConfigNewServiceImpl.java` - 页面配置服务

#### Controller层
- `DatasetConfigController.java` - 数据集管理API
- `PageConfigNewController.java` - 页面配置管理API

### 3. 前端实现

#### 类型定义
- `frontend/src/types/dataset.ts` - 数据集相关类型定义

#### API接口
- `frontend/src/api/dataset.ts` - 数据集API封装

#### 页面组件
- `frontend/src/views/dataset/DatasetManage.vue` - 数据集管理界面
- `frontend/src/views/page/PageDesignerNew.vue` - 页面设计器（重构版）

---

## 需要手动完成的步骤

### 步骤1：执行SQL脚本

**方式一：使用MySQL命令行**
```bash
# Windows
mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/001_create_dataset_config.sql
mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/002_create_field_metadata.sql
mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/003_create_page_config_new.sql
mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/004_create_form_config_new.sql
mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/005_create_table_config_new.sql
```

**方式二：使用Docker**
```bash
# 查找MySQL容器ID
docker ps | grep mysql

# 执行SQL脚本（替换CONTAINER_ID）
docker exec -i <CONTAINER_ID> mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/001_create_dataset_config.sql
docker exec -i <CONTAINER_ID> mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/002_create_field_metadata.sql
docker exec -i <CONTAINER_ID> mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/003_create_page_config_new.sql
docker exec -i <CONTAINER_ID> mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/004_create_form_config_new.sql
docker exec -i <CONTAINER_ID> mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < docs/sql/refactor/005_create_table_config_new.sql
```

**方式三：使用数据库管理工具**
- 使用Navicat、DBeaver、MySQL Workbench等工具
- 连接到数据库：`lowcode_platform`
- 依次执行上述5个SQL脚本文件

### 步骤2：配置前端路由

编辑 `frontend/src/router/index.ts`，添加数据集管理和页面设计器路由：

```typescript
{
  path: '/dataset',
  component: Layout,
  redirect: '/dataset/manage',
  meta: { title: '数据集管理', icon: 'database' },
  children: [
    {
      path: 'manage',
      name: 'DatasetManage',
      component: () => import('@/views/dataset/DatasetManage.vue'),
      meta: { title: '数据集管理', icon: 'database' }
    }
  ]
},
{
  path: '/page',
  component: Layout,
  redirect: '/page/designer-new',
  meta: { title: '页面管理', icon: 'page' },
  children: [
    {
      path: 'designer-new',
      name: 'PageDesignerNew',
      component: () => import('@/views/page/PageDesignerNew.vue'),
      meta: { title: '页面设计器（新版）', icon: 'edit' }
    }
  ]
}
```

### 步骤3：添加菜单配置

在系统菜单表中添加菜单项：

```sql
-- 数据集管理菜单
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, route_path, icon, sort_order, status) VALUES
(0, '数据集管理', 'dataset_manage', 'menu', '/dataset/manage', 'database', 100, 1);

-- 页面设计器菜单（新版）
INSERT INTO sys_menu (parent_id, menu_name, menu_code, menu_type, route_path, icon, sort_order, status) VALUES
(0, '页面设计器', 'page_designer_new', 'menu', '/page/designer-new', 'edit', 101, 1);
```

### 步骤4：重启后端服务

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 步骤5：重启前端服务

```bash
cd frontend
npm run dev
```

---

## 验证步骤

### 1. 验证数据库表

```sql
-- 查看创建的表
SHOW TABLES LIKE 'low_%';
SHOW TABLES LIKE 'low_dataset%';
SHOW TABLES LIKE '%_new';

-- 查看测试数据
SELECT * FROM low_dataset_config;
SELECT * FROM low_field_metadata;
SELECT * FROM low_page_config_new;
```

### 2. 验证后端API

访问Knife4j文档：http://localhost:8765/doc.html

检查是否有以下API：
- 数据集管理（DatasetConfig）
- 页面配置管理（PageConfig）

测试API：
```bash
# 获取数据集列表
curl http://localhost:8765/api/dataset/list?current=1&size=10

# 获取数据集详情
curl http://localhost:8765/api/dataset/1

# 执行数据源
curl -X POST http://localhost:8765/api/dataset/1/execute \
  -H "Content-Type: application/json" \
  -d '{"page": 1, "size": 10}'
```

### 3. 验证前端功能

1. 登录系统：http://localhost:3000
2. 访问"数据集管理"菜单
3. 测试创建数据集功能（从数据库表）
4. 查看"字段元数据"对话框
5. 测试"执行"功能
6. 访问"页面设计器（新版）"菜单
7. 测试创建页面配置流程

---

## 核心功能说明

### 数据集管理功能

**支持的数据源类型：**
1. **数据库表（table）**：自动扫描表结构，生成字段元数据
2. **视图（view）**：自动扫描视图结构，生成字段元数据
3. **自定义SQL（sql）**：支持参数化查询
4. **API接口（api）**：调用外部API获取数据

**核心功能：**
- 自动提取字段元数据（字段名、类型、长度、是否可空、主键等）
- 智能映射数据库类型到通用字段类型
- 支持分页查询
- 支持自定义SQL参数
- 数据源执行测试

### 页面设计器功能

**设计流程：**
1. 选择数据集
2. 配置显示字段（支持拖拽排序）
3. 配置布局（上下布局、树表布局、标签页布局、自由画布）
4. 保存页面配置

**核心特性：**
- 数据驱动：基于数据集配置
- 字段复用：同一数据集可被多个页面使用
- 分离关注点：数据定义与展示定义清晰分离
- 可视化设计：拖拽式字段选择和排序

---

## API接口文档

### 数据集管理API

| 方法 | 路径 | 说明 |
|-----|------|------|
| GET | /api/dataset/list | 数据集列表（分页） |
| GET | /api/dataset/{id} | 获取数据集详情 |
| POST | /api/dataset/create/table | 创建数据集（从表） |
| POST | /api/dataset/create/view | 创建数据集（从视图） |
| POST | /api/dataset/create/sql | 创建数据集（SQL） |
| POST | /api/dataset/create/api | 创建数据集（API） |
| POST | /api/dataset/{id}/execute | 执行数据源 |
| PUT | /api/dataset/{id} | 更新数据集 |
| DELETE | /api/dataset/{id} | 删除数据集 |
| GET | /api/dataset/check-code | 检查编码是否存在 |

### 页面配置API

| 方法 | 路径 | 说明 |
|-----|------|------|
| GET | /api/page-config-new/list | 页面配置列表（分页） |
| GET | /api/page-config-new/{id} | 获取页面配置详情 |
| POST | /api/page-config-new | 创建页面配置 |
| PUT | /api/page-config-new/{id} | 更新页面配置 |
| DELETE | /api/page-config-new/{id} | 删除页面配置 |
| PUT | /api/page-config-new/{id}/publish | 发布页面 |
| PUT | /api/page-config-new/{id}/unpublish | 取消发布页面 |

---

## 下一步工作

### 待实现功能

1. **表单设计器（重构版）**
   - 基于数据集配置字段控件
   - 支持多种控件类型（输入框、下拉框、日期选择器等）
   - 控件属性配置（必填、占位符、选项等）

2. **表格设计器（重构版）**
   - 基于数据集配置列显示
   - 支持列格式化（标签、图片、链接等）
   - 表格属性配置（分页、边框、选择等）

3. **页面渲染引擎（重构版）**
   - 加载页面配置
   - 加载数据集和字段元数据
   - 执行数据源获取数据
   - 根据布局类型渲染页面

4. **表单渲染引擎（重构版）**
   - 根据字段控件配置渲染表单
   - 数据提交和校验

5. **表格渲染引擎（重构版）**
   - 根据列显示配置渲染表格
   - 支持分页、排序、筛选

---

## 常见问题

### Q1：SQL脚本执行后中文乱码？

**A：** 确保使用 `--default-character-set=utf8mb4` 参数：
```bash
mysql -uroot -p1234 --default-character-set=utf8mb4 lowcode_platform < script.sql
```

### Q2：后端启动报错"表不存在"？

**A：** 检查SQL脚本是否正确执行，确认表已创建：
```sql
SHOW TABLES LIKE 'low_dataset%';
```

### Q3：前端无法访问API？

**A：** 检查：
1. 后端服务是否启动（http://localhost:8765）
2. 前端API代理配置（vite.config.ts）
3. 浏览器控制台网络请求

### Q4：数据集字段未自动提取？

**A：** 检查：
1. 数据库连接配置（application.yml）
2. 表名和Schema是否正确
3. 数据库用户是否有 `INFORMATION_SCHEMA` 查询权限

---

## 技术支持

如有问题，请查看：
1. 后端日志：`logs/lowcode-platform.log`
2. 前端控制台：浏览器开发者工具Console
3. API文档：http://localhost:8765/doc.html
