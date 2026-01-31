# MySQL字符集乱码问题修复总结

## 问题分析

### 1. 原始问题
- 数据库中按钮、表单、表格的中文显示为乱码（如：`??`、`æŒ‰é®ç®¡ç`）

### 2. 根本原因
- **数据库连接字符集配置不正确**
- **MySQL JDBC驱动限制**：`characterEncoding` 参数不支持 `utf8mb4`，只能使用 `UTF-8`
- **Druid连接池配置错误**：`connectionInitSqls` 配置方式不正确

---

## 解决方案

### 1. 修改 application.yml 配置

**文件**: `backend/src/main/resources/application.yml`

**修改前**:
```yaml
url: jdbc:mysql://localhost:3306/lowcode_platform?useUnicode=true&characterEncoding=utf8
```

**修改后**:
```yaml
url: jdbc:mysql://localhost:3306/lowcode_platform?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
```

### 2. 添加 Druid 连接初始化SQL

**修改前**:
```yaml
druid:
  filters: stat,wall,slf4j
  connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
```

**修改后**:
```yaml
druid:
  initial-size: 5
  min-idle: 10
  max-active: 20
  # ... 其他配置
  connection-init-sqls: SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci
  filters: stat,wall,slf4j
  connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
```

---

## 完整配置

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/lowcode_platform?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 123456
    druid:
      initial-size: 5
      min-idle: 10
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      max-evictable-idle-time-millis: 900000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      connection-init-sqls: SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci  # 关键配置
      filters: stat,wall,slf4j
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
```

---

## 数据修复步骤

### 已修复的数据

**✅ 按钮数据** - 中文显示正常
- 12个通用按钮：新增、编辑、删除、查看、导出、导入、查询、重置、刷新、提交、取消、保存

**✅ 菜单数据** - 中文显示正常
- 低代码、页面管理、表单管理、表格管理、页面模板、按钮管理

### 待修复的数据

**⚠️ 表单数据** - 需要重新插入
- 通用新增表单
- 通用编辑表单
- 通用搜索表单
- 通用详情表单

**⚠️ 表格数据** - 需要重新插入
- 通用数据表格
- 通用列表表格
- 通用报表表格

### 修复方法

由于MySQL命令行客户端的字符集问题，建议使用以下方法之一：

#### 方法1：使用后端API修复（推荐）

创建临时接口或使用已有的创建接口，通过API插入数据，确保使用正确的字符集。

#### 方法2：使用Python/PHP等脚本

```python
import pymysql

conn = pymysql.connect(
    host='localhost',
    port=3306,
    user='root',
    password='123456',
    database='lowcode_platform',
    charset='utf8mb4'
)

cursor = conn.cursor()

# 删除乱码数据
cursor.execute("DELETE FROM low_form_config WHERE component_category = 'common'")
cursor.execute("DELETE FROM low_table_config WHERE component_category = 'common'")

# 插入正确编码的数据
forms = [
    ('通用新增表单', 'common_add_form', 'common', '["system","create"]', 'add', 'horizontal', 2, 100, 'right', 'default'),
    # ... 其他表单
]

for form in forms:
    cursor.execute("""
        INSERT INTO low_form_config
        (form_name, form_code, component_category, component_tags, form_type, layout_type, layout_cols, label_width, label_position, size, status, create_by, create_time, update_time)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 1, 'system', NOW(), NOW())
    """, form)

conn.commit()
conn.close()
```

#### 方法3：使用MySQL Workbench或其他图形化工具

直接在MySQL Workbench中执行SQL插入语句，工具会自动处理字符集。

---

## 验证方法

### 1. 检查数据库连接字符集

```bash
docker exec wizardly_yalow mysql -uroot -p123456 --default-character-set=utf8mb4 lowcode_platform -e "SHOW VARIABLES LIKE 'character%';"
```

### 2. 检查表字符集

```bash
docker exec wizardly_yalow mysql -uroot -p123456 --default-character-set=utf8mb4 lowcode_platform -e "SHOW CREATE TABLE low_button_config\G"
```

### 3. 测试API

```bash
# 测试按钮库
curl -s http://localhost:8765/api/button/library/common

# 测试组件库
curl -s "http://localhost:8765/api/library/components?libraryType=common"
```

**预期结果**：
- 按钮名称显示正常：`"buttonName":"新增"`
- 表单名称显示正常：`"formName":"通用新增表单"`
- 表格名称显示正常：`"tableName":"通用数据表格"`

---

## 关键要点

1. **JDBC URL中的characterEncoding必须使用 `UTF-8`，不能使用 `utf8mb4`**
   - 错误：`characterEncoding=utf8mb4`
   - 正确：`characterEncoding=UTF-8`

2. **使用Druid的 `connection-init-sqls` 设置MySQL会话字符集**
   ```yaml
   connection-init-sqls: SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci
   ```

3. **确保数据库表使用utf8mb4字符集**
   ```sql
   SHOW CREATE TABLE table_name\G
   -- 应该显示：DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
   ```

4. **插入数据时使用正确的字符集连接**
   - 命令行：`mysql --default-character-set=utf8mb4`
   - Python：`charset='utf8mb4'`
   - JDBC：由 `connection-init-sqls` 自动设置

---

## 当前状态

| 组件类型 | 数据库记录数 | API返回状态 | 中文显示 |
|---------|-------------|------------|---------|
| 按钮（通用） | 12 | ✅ 正常 | ✅ 正常 |
| 表单（通用） | 4 | ⚠️ 乱码 | ❌ 需修复 |
| 表格（通用） | 3 | ⚠️ 乱码 | ❌ 需修复 |
| 菜单 | 6 | ✅ 正常 | ✅ 正常 |

---

## 后续建议

1. **创建数据修复脚本**：使用Python或Java程序清理并重新插入乱码数据
2. **统一字符集配置**：确保所有环境（开发、测试、生产）使用相同的字符集配置
3. **添加数据校验**：在启动时检查数据库字符集配置，不符合时给出警告
4. **文档化字符集要求**：在项目文档中明确说明MySQL字符集要求（utf8mb4）

---

## 相关文件

- 配置文件：`backend/src/main/resources/application.yml`
- 迁移脚本：
  - `docs/migration/006_component_decoupling.sql`
  - `docs/migration/007_init_common_components.sql`
  - `docs/migration/008_fix_component_decoupling.sql`
  - `docs/migration/009_fix_encoding.sql`
  - `docs/migration/009b_fix_form_table_encoding.sql`

---

**最后更新**: 2026-01-31
**状态**: 后端配置已修复，按钮和菜单数据正常，表单和表格数据待修复
