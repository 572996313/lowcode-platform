/**
 * 自由画布属性面板
 * PropertyPanel for Free Canvas Designer
 */
<template>
  <div class="property-panel-free-canvas">
    <div class="panel-header">
      <h3>属性配置</h3>
    </div>

    <div class="panel-content">
      <el-empty v-if="!selectedComponent" description="请选择组件进行配置" />

      <!-- 组件配置 -->
      <div v-else class="config-form">
        <!-- ========== 标准表格组件配置 ========== -->
        <template v-if="selectedComponent.type === 'table-standard'">
          <el-tabs v-model="tableActiveTab" class="config-tabs">
            <!-- 基本 Tab -->
            <el-tab-pane label="基本" name="basic">
              <section class="config-section">
                <div class="section-title">基本信息</div>
                <el-form label-position="top" size="small">
                  <el-form-item label="组件名称">
                    <el-input v-model="componentName" @change="handleNameChange" />
                  </el-form-item>
                  <el-form-item label="组件类型">
                    <el-tag>{{ componentTypeLabel }}</el-tag>
                  </el-form-item>
                  <el-form-item label="组件角色">
                    <el-radio-group v-model="componentRole" @change="() => {}">
                      <el-radio-button value="main">主组件</el-radio-button>
                      <el-radio-button value="linked">副组件</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="是否启用">
                    <el-switch v-model="componentEnabled" @change="handleEnabledChange" />
                  </el-form-item>
                </el-form>
              </section>
              <section class="config-section">
                <div class="section-title">从表格管理导入</div>
                <el-button type="primary" size="small" plain @click="showTableImportDialog" style="width: 100%">
                  一键导入表格配置
                </el-button>
                <div v-if="importedTableName" class="import-info">
                  <el-tag size="small" type="success">已导入: {{ importedTableName }}</el-tag>
                </div>
              </section>
              <section class="config-section">
                <div class="section-title">数据源</div>
                <el-form label-position="top" size="small">
                  <el-form-item label="接口地址">
                    <el-input v-model="tableConfig.apiUrl" placeholder="/api/xxx" @change="handleTableConfigChange" />
                  </el-form-item>
                  <el-form-item label="请求方式">
                    <el-select v-model="tableConfig.apiMethod" style="width: 100%" @change="handleTableConfigChange">
                      <el-option label="GET" value="GET" />
                      <el-option label="POST" value="POST" />
                    </el-select>
                  </el-form-item>
                </el-form>
              </section>
            </el-tab-pane>

            <!-- 表格设置 Tab -->
            <el-tab-pane label="表格设置" name="tableSettings">
              <section class="config-section">
                <div class="section-title">表格样式</div>
                <el-form label-position="top" size="small">
                  <el-form-item label="显示边框">
                    <el-switch v-model="tableConfig.tableConfig.border" @change="handleTableConfigChange" />
                  </el-form-item>
                  <el-form-item label="斑马纹">
                    <el-switch v-model="tableConfig.tableConfig.stripe" @change="handleTableConfigChange" />
                  </el-form-item>
                  <el-form-item label="组件尺寸">
                    <el-radio-group v-model="tableConfig.tableConfig.size" @change="handleTableConfigChange">
                      <el-radio-button label="large">大</el-radio-button>
                      <el-radio-button label="default">默认</el-radio-button>
                      <el-radio-button label="small">小</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                </el-form>
              </section>
              <section class="config-section">
                <div class="section-title">分页</div>
                <el-form label-position="top" size="small">
                  <el-form-item label="显示分页">
                    <el-switch v-model="tableConfig.tableConfig.showPagination" @change="handleTableConfigChange" />
                  </el-form-item>
                  <el-form-item v-if="tableConfig.tableConfig.showPagination" label="每页条数">
                    <el-input-number v-model="tableConfig.tableConfig.pageSize" :min="5" :max="100" @change="handleTableConfigChange" />
                  </el-form-item>
                  <el-form-item label="显示序号">
                    <el-switch v-model="tableConfig.tableConfig.showIndex" @change="handleTableConfigChange" />
                  </el-form-item>
                  <el-form-item label="显示多选">
                    <el-switch v-model="tableConfig.tableConfig.showSelection" @change="handleTableConfigChange" />
                  </el-form-item>
                </el-form>
              </section>
            </el-tab-pane>

            <!-- 工具栏 Tab -->
            <el-tab-pane label="工具栏" name="toolbar">
              <section class="config-section">
                <div class="section-title">工具栏按钮</div>
                <div class="button-list">
                  <div v-for="(btn, idx) in tableConfig.toolbar.buttons" :key="idx" class="button-item">
                    <div class="button-main-row">
                      <el-input v-model="btn.label" placeholder="按钮名称" size="small" @change="handleTableConfigChange" />
                      <el-select v-model="btn.btnType" placeholder="类型" size="small" style="width: 80px" @change="handleTableConfigChange">
                        <el-option label="主要" value="primary" />
                        <el-option label="成功" value="success" />
                        <el-option label="警告" value="warning" />
                        <el-option label="危险" value="danger" />
                        <el-option label="信息" value="info" />
                        <el-option label="默认" value="" />
                      </el-select>
                      <el-button type="danger" :icon="Delete" size="small" circle @click="removeTableButton(idx)" />
                    </div>
                    <div class="button-detail-row">
                      <el-input v-model="btn.icon" placeholder="图标" size="small" style="width: 80px" @change="handleTableConfigChange" />
                      <el-select v-model="btn.action" placeholder="动作" size="small" @change="handleTableConfigChange">
                        <el-option label="新增" value="add" />
                        <el-option label="编辑" value="edit" />
                        <el-option label="查看" value="view" />
                        <el-option label="删除" value="delete" />
                        <el-option label="自定义" value="custom" />
                      </el-select>
                      <el-radio-group v-model="btn.position" size="small" @change="handleTableConfigChange">
                        <el-radio-button value="toolbar">工具栏</el-radio-button>
                        <el-radio-button value="table-column">操作列</el-radio-button>
                      </el-radio-group>
                    </div>
                    <!-- 动作配置 -->
                    <div v-if="btn.actionConfig" class="action-config-section">
                      <el-form label-position="left" label-width="60px" size="small">
                        <el-form-item label="动作类型">
                          <el-select v-model="btn.actionConfig.type" style="width: 100%" @change="handleTableConfigChange">
                            <el-option label="打开表单" value="openForm" />
                            <el-option label="打开表格" value="openTable" />
                            <el-option label="路由跳转" value="route" />
                            <el-option label="提交数据" value="submit" />
                            <el-option label="调用API" value="api" />
                            <el-option label="自定义" value="custom" />
                          </el-select>
                        </el-form-item>
                        <el-form-item v-if="btn.actionConfig.type === 'openForm' || btn.actionConfig.type === 'openTable'" label="目标组件">
                          <el-select v-model="btn.actionConfig.targetCode" placeholder="选择弹窗组件" style="width: 100%" @change="handleTableConfigChange">
                            <el-option
                              v-for="comp in linkedFormComponents"
                              :key="comp.id"
                              :label="comp.name"
                              :value="comp.id"
                            />
                          </el-select>
                          <div v-if="!linkedFormComponents.length" class="no-linked-hint">暂无副组件，请先添加并设为副组件角色</div>
                        </el-form-item>
                        <el-form-item v-if="btn.actionConfig.type === 'openForm' || btn.actionConfig.type === 'openTable'" label="打开方式">
                          <el-radio-group v-model="btn.actionConfig.openMode" @change="handleTableConfigChange">
                            <el-radio-button label="dialog">弹窗</el-radio-button>
                            <el-radio-button label="drawer">抽屉</el-radio-button>
                            <el-radio-button label="page">页面</el-radio-button>
                          </el-radio-group>
                        </el-form-item>
                        <el-form-item v-if="btn.position !== 'table-column'" label="选择方式">
                          <el-radio-group v-model="btn.actionConfig.selectionMode" @change="handleTableConfigChange">
                            <el-radio-button value="none">不选数据</el-radio-button>
                            <el-radio-button value="single">必须单选</el-radio-button>
                            <el-radio-button value="multiple">多选</el-radio-button>
                          </el-radio-group>
                        </el-form-item>
                        <el-form-item v-if="btn.actionConfig.type === 'route'" label="路由路径">
                          <el-input v-model="btn.actionConfig.routePath" @change="handleTableConfigChange" />
                        </el-form-item>
                        <el-form-item v-if="btn.actionConfig.type === 'submit'" label="确认提示">
                          <el-input v-model="btn.actionConfig.confirmText" @change="handleTableConfigChange" />
                        </el-form-item>
                      </el-form>
                    </div>
                    <el-button v-else size="small" text type="primary" @click="initTableButtonAction(idx)">配置动作</el-button>
                  </div>
                  <el-button type="primary" size="small" plain @click="addTableButton">+ 添加按钮</el-button>
                </div>
              </section>
            </el-tab-pane>

            <!-- 搜索区 Tab -->
            <el-tab-pane label="搜索区" name="search">
              <section class="config-section">
                <div class="section-title">搜索字段</div>
                <div class="field-list">
                  <div v-for="(field, idx) in tableConfig.searchFields" :key="idx" class="field-item">
                    <div class="field-row">
                      <el-input v-model="field.label" placeholder="字段标签" size="small" @change="handleTableConfigChange" />
                      <el-input v-model="field.field" placeholder="字段名" size="small" @change="handleTableConfigChange" />
                      <el-select v-model="field.type" placeholder="类型" size="small" style="width: 90px" @change="handleTableConfigChange">
                        <el-option label="输入框" value="input" />
                        <el-option label="选择器" value="select" />
                        <el-option label="日期" value="date" />
                        <el-option label="日期范围" value="daterange" />
                        <el-option label="数字" value="number" />
                      </el-select>
                      <el-button type="danger" :icon="Delete" size="small" circle @click="removeSearchField(idx)" />
                    </div>
                    <div class="field-detail-row">
                      <el-input v-model="field.placeholder" placeholder="占位文本" size="small" @change="handleTableConfigChange" />
                      <el-switch v-model="field.clearable" active-text="清空" size="small" @change="handleTableConfigChange" />
                    </div>
                    <!-- Select 类型选项 -->
                    <div v-if="field.type === 'select' && field.options" class="select-options-section">
                      <div v-for="(opt, oi) in field.options" :key="oi" class="option-row">
                        <el-input v-model="opt.label" placeholder="显示文本" size="small" @change="handleTableConfigChange" />
                        <el-input v-model="opt.value" placeholder="值" size="small" @change="handleTableConfigChange" />
                        <el-button size="small" type="danger" circle :icon="Delete" @click="removeSearchOption(idx, oi)" />
                      </div>
                      <el-button size="small" @click="addSearchOption(idx)">+ 选项</el-button>
                    </div>
                  </div>
                  <el-button type="primary" size="small" plain @click="addSearchField">+ 添加搜索字段</el-button>
                </div>
              </section>
            </el-tab-pane>

            <!-- 表格列 Tab -->
            <el-tab-pane label="表格列" name="columns">
              <section class="config-section">
                <div class="section-title">表格列</div>
                <div class="field-list">
                  <div v-for="(col, idx) in tableConfig.tableColumns" :key="idx" class="field-item">
                    <div class="field-row">
                      <el-input v-model="col.label" placeholder="列标题" size="small" @change="handleTableConfigChange" />
                      <el-select v-model="col.type" placeholder="类型" size="small" style="width: 80px" clearable @change="handleTableConfigChange">
                        <el-option label="文本" value="text" />
                        <el-option label="标签" value="tag" />
                        <el-option label="日期" value="date" />
                        <el-option label="序号" value="index" />
                        <el-option label="多选" value="selection" />
                      </el-select>
                      <el-input v-model="col.prop" placeholder="字段名" size="small" style="width: 80px" @change="handleTableConfigChange" />
                      <el-input-number v-model="col.width" placeholder="宽度" size="small" :min="0" style="width: 80px" @change="handleTableConfigChange" />
                      <el-select v-model="col.align" placeholder="对齐" size="small" style="width: 65px" clearable @change="handleTableConfigChange">
                        <el-option label="左" value="left" />
                        <el-option label="中" value="center" />
                        <el-option label="右" value="right" />
                      </el-select>
                      <el-button type="danger" :icon="Delete" size="small" circle @click="removeTableColumn(idx)" />
                    </div>
                    <!-- Tag 配置 -->
                    <div v-if="col.type === 'tag' && col.tagConfig" class="tag-config-inline">
                      <div v-for="(mapping, key) in col.tagConfig.mapping" :key="key" class="tag-row">
                        <el-tag size="small" disable-transitions>{{ key }}</el-tag>
                        <el-input v-model="mapping.text" placeholder="文本" size="small" style="width: 70px" @change="handleTableConfigChange" />
                        <el-select v-model="mapping.type" size="small" style="width: 70px" @change="handleTableConfigChange">
                          <el-option label="成功" value="success" />
                          <el-option label="警告" value="warning" />
                          <el-option label="危险" value="danger" />
                          <el-option label="信息" value="info" />
                        </el-select>
                        <el-button size="small" type="danger" circle :icon="Delete" @click="removeTagMapping(idx, key as string)" />
                      </div>
                      <div class="add-tag-row">
                        <el-input v-model="newTagKeys[idx]" placeholder="值" size="small" style="width: 60px" />
                        <el-button size="small" @click="addTagMapping(idx)">+映射</el-button>
                      </div>
                    </div>
                  </div>
                  <el-button type="primary" size="small" plain @click="addTableColumn">+ 添加列</el-button>
                </div>
              </section>
            </el-tab-pane>
          </el-tabs>
        </template>

        <!-- ========== 标准表单组件配置 ========== -->
        <template v-else-if="selectedComponent.type === 'form-standard'">
          <el-tabs v-model="formActiveTab" class="config-tabs">
            <!-- 基本 Tab -->
            <el-tab-pane label="基本" name="basic">
              <section class="config-section">
                <div class="section-title">基本信息</div>
                <el-form label-position="top" size="small">
                  <el-form-item label="组件名称">
                    <el-input v-model="componentName" @change="handleNameChange" />
                  </el-form-item>
                  <el-form-item label="组件类型">
                    <el-tag>{{ componentTypeLabel }}</el-tag>
                  </el-form-item>
                  <el-form-item label="组件角色">
                    <el-radio-group v-model="componentRole" @change="() => {}">
                      <el-radio-button value="main">主组件</el-radio-button>
                      <el-radio-button value="linked">副组件</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="是否启用">
                    <el-switch v-model="componentEnabled" @change="handleEnabledChange" />
                  </el-form-item>
                </el-form>
              </section>
            </el-tab-pane>

            <!-- 工具栏 Tab -->
            <el-tab-pane label="工具栏" name="toolbar">
              <section class="config-section">
                <div class="section-title">工具栏按钮</div>
                <div class="button-list">
                  <div v-for="(btn, idx) in formConfig.toolbar.buttons" :key="idx" class="button-item">
                    <div class="button-main-row">
                      <el-input v-model="btn.label" placeholder="按钮名称" size="small" @change="handleFormConfigChange" />
                      <el-select v-model="btn.btnType" placeholder="类型" size="small" style="width: 90px" @change="handleFormConfigChange">
                        <el-option label="主要" value="primary" />
                        <el-option label="成功" value="success" />
                        <el-option label="警告" value="warning" />
                        <el-option label="危险" value="danger" />
                        <el-option label="默认" value="" />
                      </el-select>
                      <el-button type="danger" :icon="Delete" size="small" circle @click="removeFormButton(idx)" />
                    </div>
                    <!-- 动作配置 -->
                    <div v-if="btn.actionConfig" class="action-config-section compact">
                      <el-form label-position="left" label-width="60px" size="small">
                        <el-form-item label="动作类型">
                          <el-select v-model="btn.actionConfig.type" style="width: 100%" @change="handleFormConfigChange">
                            <el-option label="提交数据" value="submit" />
                            <el-option label="调用API" value="api" />
                            <el-option label="自定义" value="custom" />
                          </el-select>
                        </el-form-item>
                        <el-form-item v-if="btn.actionConfig.type === 'submit' || btn.actionConfig.type === 'api'" label="接口地址">
                          <el-input v-model="btn.actionConfig.apiUrl" placeholder="/table-data/save" @change="handleFormConfigChange" />
                        </el-form-item>
                      </el-form>
                    </div>
                    <el-button v-else size="small" text type="primary" @click="initFormButtonAction(idx)">配置动作</el-button>
                  </div>
                  <el-button type="primary" size="small" plain @click="addFormButton">+ 添加按钮</el-button>
                </div>
              </section>
            </el-tab-pane>

            <!-- 表单字段 Tab -->
            <el-tab-pane label="表单字段" name="fields">
              <section class="config-section">
                <div class="section-title">表单分组</div>
                <el-collapse v-model="formGroupExpanded">
                  <el-collapse-item v-for="(group, gi) in formConfig.groups" :key="gi" :title="group.title || `分组 ${gi + 1}`" :name="gi">
                    <el-form label-position="top" size="small" style="margin-bottom: 8px">
                      <el-form-item label="分组名称">
                        <el-input v-model="group.title" placeholder="分组标题" @change="handleFormConfigChange" />
                      </el-form-item>
                    </el-form>
                    <div class="field-list">
                      <div v-for="(field, fi) in group.fields" :key="fi" class="field-item">
                        <div class="field-row">
                          <el-input v-model="field.label" placeholder="字段标签" size="small" @change="handleFormConfigChange" />
                          <el-input v-model="field.field" placeholder="字段名" size="small" @change="handleFormConfigChange" />
                          <el-select v-model="field.type" placeholder="类型" size="small" style="width: 100px" @change="handleFormConfigChange">
                            <el-option label="输入框" value="input" />
                            <el-option label="文本域" value="textarea" />
                            <el-option label="选择器" value="select" />
                            <el-option label="数字" value="number" />
                            <el-option label="开关" value="switch" />
                            <el-option label="日期" value="date" />
                            <el-option label="单选" value="radio" />
                            <el-option label="多选" value="checkbox" />
                          </el-select>
                          <el-button type="danger" :icon="Delete" size="small" circle @click="removeFormField(gi, fi)" />
                        </div>
                        <div class="field-options-row">
                          <el-input v-model="field.placeholder" placeholder="提示文字" size="small" @change="handleFormConfigChange" />
                          <el-checkbox v-model="field.editable" :false-value="false" @change="handleFormConfigChange">可编辑</el-checkbox>
                          <el-checkbox v-model="field.required" @change="handleFormConfigChange">必填</el-checkbox>
                        </div>
                      </div>
                      <el-button type="primary" size="small" plain @click="addFormField(gi)">+ 添加字段</el-button>
                    </div>
                  </el-collapse-item>
                </el-collapse>
                <el-button type="primary" size="small" plain style="margin-top: 8px" @click="addFormGroup">+ 添加分组</el-button>
              </section>
            </el-tab-pane>

            <!-- 布局 Tab -->
            <el-tab-pane label="布局" name="layout">
              <section class="config-section">
                <div class="section-title">表单布局</div>
                <el-form label-position="top" size="small">
                  <el-form-item label="列数">
                    <el-radio-group v-model="formConfig.layout.columns" @change="handleFormConfigChange">
                      <el-radio-button :value="1">1列</el-radio-button>
                      <el-radio-button :value="2">2列</el-radio-button>
                      <el-radio-button :value="3">3列</el-radio-button>
                      <el-radio-button :value="4">4列</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="标签宽度">
                    <el-input v-model="formConfig.layout.labelWidth" placeholder="120px" @change="handleFormConfigChange" />
                  </el-form-item>
                  <el-form-item label="标签位置">
                    <el-radio-group v-model="formConfig.layout.labelPosition" @change="handleFormConfigChange">
                      <el-radio-button value="left">左</el-radio-button>
                      <el-radio-button value="right">右</el-radio-button>
                      <el-radio-button value="top">上</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="行间距">
                    <el-input-number v-model="formConfig.layout.rowGutter" :min="0" :max="60" @change="handleFormConfigChange" />
                  </el-form-item>
                </el-form>
              </section>
            </el-tab-pane>
          </el-tabs>
        </template>

        <!-- ========== 其他组件（树、图表等）配置 ========== -->
        <template v-else>
          <!-- 组件基本信息 -->
          <section class="config-section">
            <div class="section-title">基本信息</div>
            <el-form label-position="top" size="small">
              <el-form-item label="组件名称">
                <el-input v-model="componentName" @change="handleNameChange" />
              </el-form-item>
              <el-form-item label="组件类型">
                <el-tag>{{ componentTypeLabel }}</el-tag>
              </el-form-item>
              <el-form-item label="是否启用">
                <el-switch v-model="componentEnabled" @change="handleEnabledChange" />
              </el-form-item>
            </el-form>
          </section>

          <!-- 位置配置 -->
          <section class="config-section">
            <div class="section-title">位置</div>
            <PositionEditor :position="position" :grid-size="canvasConfig?.gridSize" @change="handlePositionChange" />
          </section>

          <!-- 数据源配置 -->
          <section v-if="hasDataSource" class="config-section">
            <div class="section-title">数据源</div>
            <el-form label-position="top" size="small">
              <el-form-item label="数据源类型">
                <el-select v-model="dataSourceType" @change="handleDataSourceTypeChange">
                  <el-option label="静态数据" value="static" />
                  <el-option label="API接口" value="api" />
                  <el-option label="SQL查询" value="sql" />
                </el-select>
              </el-form-item>

              <template v-if="dataSourceType === 'static'">
                <el-form-item label="数据（JSON）">
                  <el-input
                    v-model="staticDataJson"
                    type="textarea"
                    :rows="4"
                    placeholder='[{"id": 1, "name": "示例"}]'
                    @blur="handleStaticDataChange"
                  />
                </el-form-item>
              </template>

              <template v-if="dataSourceType === 'api'">
                <el-form-item label="接口地址">
                  <el-input v-model="apiUrl" placeholder="/api/xxx" @change="handleApiConfigChange" />
                </el-form-item>
                <el-form-item label="请求方法">
                  <el-select v-model="apiMethod" @change="handleApiConfigChange">
                    <el-option label="GET" value="GET" />
                    <el-option label="POST" value="POST" />
                    <el-option label="PUT" value="PUT" />
                    <el-option label="DELETE" value="DELETE" />
                  </el-select>
                </el-form-item>
              </template>

              <template v-if="dataSourceType === 'sql'">
                <el-form-item label="SQL语句">
                  <el-input
                    v-model="sqlContent"
                    type="textarea"
                    :rows="4"
                    placeholder="SELECT * FROM table"
                    @change="handleSqlConfigChange"
                  />
                </el-form-item>
              </template>
            </el-form>
          </section>

          <!-- 树组件配置 -->
          <section v-if="selectedComponent.type === 'tree'" class="config-section">
            <div class="section-title">树配置</div>
            <el-form label-position="top" size="small">
              <el-form-item label="显示字段">
                <el-input v-model="treeConfig.displayField" @change="handleTreeConfigChange" />
              </el-form-item>
              <el-form-item label="子节点字段">
                <el-input v-model="treeConfig.childrenField" @change="handleTreeConfigChange" />
              </el-form-item>
              <el-form-item label="ID字段">
                <el-input v-model="treeConfig.idField" @change="handleTreeConfigChange" />
              </el-form-item>
              <el-form-item label="默认展开">
                <el-switch v-model="treeConfig.defaultExpandAll" @change="handleTreeConfigChange" />
              </el-form-item>
              <el-form-item label="显示图标">
                <el-switch v-model="treeConfig.showIcon" @change="handleTreeConfigChange" />
              </el-form-item>
              <el-form-item label="显示复选框">
                <el-switch v-model="treeConfig.showCheckbox" @change="handleTreeConfigChange" />
              </el-form-item>
            </el-form>
          </section>

          <!-- 样式配置 -->
          <section class="config-section">
            <div class="section-title">样式</div>
            <StyleEditor :style="style" @change="handleStyleChange" />
          </section>
        </template>

        <!-- 操作按钮 -->
        <section class="config-section actions">
          <el-button type="danger" :icon="Delete" @click="handleDelete">
            删除组件
          </el-button>
        </section>
      </div>
    </div>

    <!-- 导入表格配置对话框 -->
    <el-dialog v-model="tableImportVisible" title="导入表格配置" width="600px" append-to-body>
      <el-table
        :data="tableImportList"
        v-loading="tableImportLoading"
        highlight-current-row
        @current-change="handleImportSelect"
        size="small"
        max-height="400"
      >
        <el-table-column prop="tableName" label="表格名称" min-width="140" />
        <el-table-column prop="tableCode" label="表格编码" width="140" />
        <el-table-column prop="apiUrl" label="API地址" min-width="180" show-overflow-tooltip />
        <el-table-column label="配置" width="160">
          <template #default="{ row }">
            <span v-if="row.configJson">
              {{ getImportSummary(row) }}
            </span>
            <el-tag v-else type="info" size="small">空</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="tableImportVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedTableImport" @click="confirmTableImport">确认导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, reactive } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type {
  ComponentInstance,
  TreeComponentConfig,
  TableStandardComponentConfig,
  FormStandardComponentConfig,
  ComponentPosition,
  ComponentStyle
} from '@/types/page-free-canvas'
import { getTableList, getTableConfig } from '@/api/table'

// ============ 子组件 ============
const PositionEditor = {
  props: {
    position: { type: Object as () => ComponentPosition, required: true },
    gridSize: { type: Number, default: 1 }
  },
  emits: ['change'],
  template: `
    <el-form label-position="top" size="small">
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px">
        <el-form-item label="X">
          <el-input-number :model-value="position.x" :min="0" :step="gridSize" @change="val => emitPos('x', val)" />
        </el-form-item>
        <el-form-item label="Y">
          <el-input-number :model-value="position.y" :min="0" :step="gridSize" @change="val => emitPos('y', val)" />
        </el-form-item>
        <el-form-item label="宽度">
          <el-input-number :model-value="position.width" :min="50" :step="10" @change="val => emitPos('width', val)" />
        </el-form-item>
        <el-form-item label="高度">
          <el-input-number :model-value="position.height" :min="30" :step="10" @change="val => emitPos('height', val)" />
        </el-form-item>
      </div>
      <el-form-item label="层级">
        <el-input-number :model-value="position.zIndex" :min="1" :max="999" @change="val => emitPos('zIndex', val)" />
      </el-form-item>
    </el-form>
  `,
  methods: {
    emitPos(key: string, val: number) {
      this.$emit('change', { ...this.position, [key]: val })
    }
  }
}

const StyleEditor = {
  props: {
    style: { type: Object as () => Record<string, string>, required: true }
  },
  emits: ['change'],
  template: `
    <el-form label-position="top" size="small">
      <el-form-item label="背景颜色">
        <el-color-picker :model-value="style.backgroundColor" @change="val => emitStyle('backgroundColor', val)" />
      </el-form-item>
      <el-form-item label="边框">
        <el-input :model-value="style.border" placeholder="1px solid #ddd" @change="val => emitStyle('border', val)" />
      </el-form-item>
      <el-form-item label="圆角">
        <el-input :model-value="style.borderRadius" placeholder="4px" @change="val => emitStyle('borderRadius', val)" />
      </el-form-item>
      <el-form-item label="内边距">
        <el-input :model-value="style.padding" placeholder="16px" @change="val => emitStyle('padding', val)" />
      </el-form-item>
      <el-form-item label="阴影">
        <el-select :model-value="style.boxShadow" @change="val => emitStyle('boxShadow', val)">
          <el-option label="无" value="none" />
          <el-option label="小" value="0 2px 4px rgba(0,0,0,0.1)" />
          <el-option label="中" value="0 2px 8px rgba(0,0,0,0.15)" />
          <el-option label="大" value="0 4px 16px rgba(0,0,0,0.2)" />
        </el-select>
      </el-form-item>
    </el-form>
  `,
  methods: {
    emitStyle(key: string, val: string) {
      this.$emit('change', { ...this.style, [key]: val })
    }
  }
}

// ============ Props & Emits ============
interface Props {
  component?: ComponentInstance | null
  allComponents?: ComponentInstance[]
  canvasConfig?: {
    width: number
    height?: number | null
    gridSize?: number
    snapToGrid?: boolean
  }
}

interface Emits {
  (e: 'update', component: ComponentInstance): void
  (e: 'delete', id: string): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// ============ Tab 状态 ============
const tableActiveTab = ref('basic')
const formActiveTab = ref('basic')
const formGroupExpanded = ref<number[]>([0])
const newTagKeys = reactive<Record<number, string>>({})

// ============ 导入表格配置 ============
const tableImportVisible = ref(false)
const tableImportLoading = ref(false)
const tableImportList = ref<any[]>([])
const selectedTableImport = ref<any>(null)
const importedTableName = ref('')

// ============ 弹窗组件列表（用于按钮绑定） ============
const linkedFormComponents = computed(() => {
  return (props.allComponents || []).filter(c => c.role === 'linked')
})

// ============ 角色切换 ============
const componentRole = computed({
  get: () => props.component?.role || 'main',
  set: (val: 'main' | 'linked') => {
    if (props.component) {
      emit('update', { ...props.component, role: val })
    }
  }
})

// ============ 组件类型标签 ============
const componentTypeLabel = computed(() => {
  const labelMap: Record<string, string> = {
    'tree': '树组件',
    'table-standard': '标准表格',
    'form-standard': '标准表单',
    'chart': '图表',
    'tabs': '标签页',
    'card': '卡片',
    'divider': '分割线',
    'spacer': '占位符'
  }
  return props.component ? labelMap[props.component.type] || '未知' : ''
})

// ============ 是否有数据源配置 ============
const hasDataSource = computed(() => {
  return props.component && ['tree', 'chart'].includes(props.component.type)
})

// ============ 公共属性 ============
const selectedComponent = computed(() => props.component)

const componentName = computed({
  get: () => props.component?.name || '',
  set: (val) => {
    if (props.component) {
      emit('update', { ...props.component, name: val })
    }
  }
})

const componentEnabled = computed({
  get: () => props.component?.enabled ?? true,
  set: (val) => {
    if (props.component) {
      emit('update', { ...props.component, enabled: val })
    }
  }
})

const position = computed(() => props.component?.position || {
  x: 0, y: 0, width: 200, height: 100, zIndex: 1
})

// ============ 样式 ============
const style = ref({
  backgroundColor: '',
  border: '',
  borderRadius: '',
  padding: '',
  boxShadow: 'none'
})

watch(() => props.component?.style, (newStyle) => {
  if (newStyle) {
    style.value = {
      backgroundColor: newStyle.backgroundColor || '',
      border: newStyle.border || '',
      borderRadius: newStyle.borderRadius || '',
      padding: newStyle.padding?.toString() || '',
      boxShadow: newStyle.boxShadow || 'none'
    }
  }
}, { immediate: true })

// ============ 数据源 ============
const dataSourceType = ref('static')
const staticDataJson = ref('[]')
const apiUrl = ref('')
const apiMethod = ref('GET')
const sqlContent = ref('')

watch(() => props.component, (comp) => {
  if (comp && hasDataSource.value) {
    const dataSource = (comp.config as any).dataSource
    if (dataSource) {
      dataSourceType.value = dataSource.type || 'static'
      if (dataSource.type === 'static') {
        staticDataJson.value = JSON.stringify(dataSource.static || [])
      } else if (dataSource.type === 'api') {
        apiUrl.value = dataSource.api?.url || ''
        apiMethod.value = dataSource.api?.method || 'GET'
      } else if (dataSource.type === 'sql') {
        sqlContent.value = dataSource.sql?.content || ''
      }
    }
  }
}, { immediate: true })

// ============ 树组件配置 ============
const treeConfig = computed({
  get: () => {
    if (!props.component || props.component.type !== 'tree') return {} as any
    return props.component.config as TreeComponentConfig
  },
  set: (val) => {
    if (props.component && props.component.type === 'tree') {
      emit('update', { ...props.component, config: { ...props.component.config, ...val } })
    }
  }
})

// ============ 标准表格配置 ============
const tableConfig = computed(() => {
  if (!props.component || props.component.type !== 'table-standard') {
    return {
      pageCode: '', pageName: '',
      apiUrl: '', apiMethod: 'GET',
      toolbar: { buttons: [] },
      searchFields: [],
      tableColumns: [],
      tableConfig: { border: true, stripe: true, size: 'default' as const, showPagination: true, pageSize: 10, showIndex: false, showSelection: false }
    } as any as TableStandardComponentConfig
  }
  return props.component.config as TableStandardComponentConfig
})

function handleTableConfigChange() {
  if (!props.component || props.component.type !== 'table-standard') return
  emit('update', {
    ...props.component,
    config: { ...tableConfig.value } as TableStandardComponentConfig
  })
}

function addTableButton() {
  tableConfig.value.toolbar.buttons.push({ label: '新按钮', btnType: 'primary', action: 'custom', position: 'toolbar' })
  handleTableConfigChange()
}

function removeTableButton(idx: number) {
  tableConfig.value.toolbar.buttons.splice(idx, 1)
  handleTableConfigChange()
}

function initTableButtonAction(idx: number) {
  const btn = tableConfig.value.toolbar.buttons[idx]
  if (btn) {
    btn.actionConfig = { type: 'custom', selectionMode: 'none' }
    handleTableConfigChange()
  }
}

function addSearchField() {
  tableConfig.value.searchFields.push({ field: `field_${Date.now()}`, label: '新字段', type: 'input', clearable: true })
  handleTableConfigChange()
}

function removeSearchField(idx: number) {
  tableConfig.value.searchFields.splice(idx, 1)
  handleTableConfigChange()
}

function addSearchOption(fieldIdx: number) {
  const field = tableConfig.value.searchFields[fieldIdx]
  if (field) {
    if (!field.options) field.options = []
    field.options.push({ label: '选项', value: '' })
    handleTableConfigChange()
  }
}

function removeSearchOption(fieldIdx: number, optIdx: number) {
  tableConfig.value.searchFields[fieldIdx]?.options?.splice(optIdx, 1)
  handleTableConfigChange()
}

function addTableColumn() {
  tableConfig.value.tableColumns.push({ prop: `col_${Date.now()}`, label: '新列', width: 120 })
  handleTableConfigChange()
}

function removeTableColumn(idx: number) {
  tableConfig.value.tableColumns.splice(idx, 1)
  handleTableConfigChange()
}

// Tag 映射操作
function addTagMapping(colIdx: number) {
  const col = tableConfig.value.tableColumns[colIdx]
  if (!col) return
  const key = newTagKeys[colIdx] || ''
  if (!key) return
  if (!col.tagConfig) col.tagConfig = { mapping: {} }
  col.tagConfig.mapping[key] = { text: '标签文本', type: 'info' }
  newTagKeys[colIdx] = ''
  handleTableConfigChange()
}

function removeTagMapping(colIdx: number, key: string) {
  const col = tableConfig.value.tableColumns[colIdx]
  if (col?.tagConfig?.mapping) {
    delete col.tagConfig.mapping[key]
    handleTableConfigChange()
  }
}

// ============ 标准表单配置 ============
const formConfig = computed(() => {
  if (!props.component || props.component.type !== 'form-standard') {
    return {
      pageCode: '', pageName: '',
      layout: { columns: 2, labelWidth: '120px', labelPosition: 'right' as const, size: 'default' as const, rowGutter: 20 },
      toolbar: { buttons: [] },
      groups: []
    } as FormStandardComponentConfig
  }
  return props.component.config as FormStandardComponentConfig
})

function handleFormConfigChange() {
  if (!props.component || props.component.type !== 'form-standard') return
  emit('update', {
    ...props.component,
    config: { ...formConfig.value } as FormStandardComponentConfig
  })
}

function addFormButton() {
  formConfig.value.toolbar.buttons.push({ label: '保存', btnType: 'primary', action: 'submit' })
  handleFormConfigChange()
}

function removeFormButton(idx: number) {
  formConfig.value.toolbar.buttons.splice(idx, 1)
  handleFormConfigChange()
}

function initFormButtonAction(idx: number) {
  const btn = formConfig.value.toolbar.buttons[idx]
  if (btn) {
    btn.actionConfig = { type: 'submit', apiUrl: '' }
    handleFormConfigChange()
  }
}

function addFormGroup() {
  formConfig.value.groups.push({ title: '新分组', collapsible: false, fields: [] })
  formGroupExpanded.value.push(formConfig.value.groups.length - 1)
  handleFormConfigChange()
}

function addFormField(groupIdx: number) {
  formConfig.value.groups[groupIdx].fields.push({
    field: `field_${Date.now()}`,
    label: '新字段',
    type: 'input',
    placeholder: '请输入'
  })
  handleFormConfigChange()
}

function removeFormField(groupIdx: number, fieldIdx: number) {
  formConfig.value.groups[groupIdx].fields.splice(fieldIdx, 1)
  handleFormConfigChange()
}

// ============ 公共事件处理 ============
function emitUpdate(component: ComponentInstance) {
  emit('update', component)
}

function handleNameChange(value: string) {
  if (props.component) emitUpdate({ ...props.component, name: value })
}

function handleEnabledChange(value: boolean) {
  if (props.component) emitUpdate({ ...props.component, enabled: value })
}

function handlePositionChange(newPosition: ComponentPosition) {
  if (props.component) emitUpdate({ ...props.component, position: newPosition })
}

function handleStyleChange(newStyle: Record<string, string>) {
  if (!props.component) return
  emitUpdate({
    ...props.component,
    style: {
      ...props.component.style,
      backgroundColor: newStyle.backgroundColor || undefined,
      border: newStyle.border || undefined,
      borderRadius: newStyle.borderRadius || undefined,
      padding: newStyle.padding || undefined,
      boxShadow: newStyle.boxShadow || undefined
    }
  })
}

function handleTreeConfigChange() {
  // computed setter 已处理
}

function handleDataSourceTypeChange(value: string) {
  if (!props.component || !hasDataSource.value) return
  const config = { ...props.component.config } as any
  config.dataSource = { type: value }
  if (value === 'static') config.dataSource.static = []
  else if (value === 'api') config.dataSource.api = { url: '', method: 'GET' }
  else if (value === 'sql') config.dataSource.sql = { content: '' }
  emitUpdate({ ...props.component, config })
}

function handleStaticDataChange() {
  if (!props.component || !hasDataSource.value) return
  try {
    const data = JSON.parse(staticDataJson.value || '[]')
    const config = { ...props.component.config } as any
    config.dataSource = { ...(config as any).dataSource, static: data }
    emitUpdate({ ...props.component, config })
  } catch (e) { /* ignore */ }
}

function handleApiConfigChange() {
  if (!props.component || !hasDataSource.value) return
  const config = { ...props.component.config } as any
  config.dataSource = {
    ...(config as any).dataSource, type: 'api',
    api: { url: apiUrl.value, method: apiMethod.value }
  }
  emitUpdate({ ...props.component, config })
}

function handleSqlConfigChange() {
  if (!props.component || !hasDataSource.value) return
  const config = { ...props.component.config } as any
  config.dataSource = { ...(config as any).dataSource, type: 'sql', sql: { content: sqlContent.value } }
  emitUpdate({ ...props.component, config })
}

// ============ 导入表格配置方法 ============
function showTableImportDialog() {
  tableImportVisible.value = true
  selectedTableImport.value = null
  loadTableImportList()
}

async function loadTableImportList() {
  tableImportLoading.value = true
  try {
    const result = await getTableList({ current: 1, size: 200 })
    tableImportList.value = result.records || []
  } catch (e: any) {
    ElMessage.error('加载表格列表失败')
  } finally {
    tableImportLoading.value = false
  }
}

function handleImportSelect(row: any) {
  selectedTableImport.value = row
}

function getImportSummary(row: any): string {
  try {
    const config = JSON.parse(row.configJson || '{}')
    const parts: string[] = []
    if (config.toolbar?.buttons?.length) parts.push(`${config.toolbar.buttons.length}个按钮`)
    if (config.searchFields?.length) parts.push(`${config.searchFields.length}个搜索`)
    if (config.tableColumns?.length) parts.push(`${config.tableColumns.length}列`)
    return parts.join(' / ') || '有配置'
  } catch {
    return '解析失败'
  }
}

async function confirmTableImport() {
  if (!selectedTableImport.value || !props.component) return
  const row = selectedTableImport.value

  try {
    const detail = await getTableConfig(row.id)

    let parsedConfig: any = {}
    if (detail.configJson) {
      parsedConfig = JSON.parse(detail.configJson)
    }

    const newConfig: TableStandardComponentConfig = {
      pageCode: parsedConfig.pageCode || detail.tableCode || '',
      pageName: parsedConfig.pageName || detail.tableName || '',
      apiUrl: detail.apiUrl || '',
      apiMethod: (detail.apiMethod as 'GET' | 'POST') || 'GET',
      toolbar: parsedConfig.toolbar || { buttons: [] },
      searchFields: parsedConfig.searchFields || [],
      tableColumns: parsedConfig.tableColumns || [],
      tableConfig: parsedConfig.tableConfig || {
        border: true, stripe: true, size: 'default' as const,
        showPagination: true, pageSize: 10,
        showIndex: false, showSelection: false
      }
    }

    const newName = row.tableName || props.component.name

    emit('update', {
      ...props.component,
      name: newName,
      config: newConfig
    })

    importedTableName.value = row.tableName
    tableImportVisible.value = false
    ElMessage.success(`已导入「${row.tableName}」的配置`)
  } catch (e: any) {
    ElMessage.error('导入失败: ' + e.message)
  }
}

function handleDelete() {
  if (props.component) emit('delete', props.component.id)
}
</script>

<style scoped lang="scss">
.property-panel-free-canvas {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-left: 1px solid #e4e7ed;

  .panel-header {
    padding: 12px 16px;
    border-bottom: 1px solid #e4e7ed;

    h3 {
      margin: 0;
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }
  }

  .panel-content {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
  }

  .config-form {
    .config-section {
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      &.actions {
        display: flex;
        justify-content: center;
      }
    }

    .section-title {
      font-size: 13px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 12px;
      padding-left: 8px;
      border-left: 3px solid #409eff;
    }

    :deep(.el-form-item) {
      margin-bottom: 12px;
    }

    :deep(.el-form-item__label) {
      font-size: 12px;
      color: #606266;
    }
  }
}

.config-tabs {
  :deep(.el-tabs__header) {
    margin: 0 0 12px;
  }

  :deep(.el-tabs__item) {
    font-size: 12px;
    padding: 0 8px;
    height: 28px;
    line-height: 28px;
  }
}

.button-list {
  .button-item {
    margin-bottom: 12px;
    padding: 8px;
    background: #f5f7fa;
    border-radius: 4px;

    .button-main-row {
      display: flex;
      gap: 6px;
      align-items: center;
    }

    .button-detail-row {
      display: flex;
      gap: 6px;
      align-items: center;
      margin-top: 6px;
    }

    .action-config-section {
      margin-top: 6px;
      padding-left: 8px;
      border-left: 2px solid #409eff;
    }
  }
}

.field-list {
  .field-item {
    margin-bottom: 8px;
    padding: 6px 8px;
    background: #f5f7fa;
    border-radius: 4px;
  }

  .field-row {
    display: flex;
    gap: 4px;
    align-items: center;

    .el-input {
      flex: 1;
    }
  }

  .field-options-row {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-top: 4px;

    .el-input {
      flex: 1;
    }

    .el-checkbox {
      flex-shrink: 0;
    }
  }

  .field-detail-row {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-top: 4px;
  }
}

.tag-config-inline,
.action-col-config,
.select-options-section {
  margin-top: 4px;
  padding-left: 8px;
  border-left: 2px solid #e6e6e6;
}

.tag-row,
.option-row,
.action-btn-inline {
  display: flex;
  gap: 4px;
  align-items: center;
  margin-bottom: 4px;
}

.action-btn-item {
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #ebeef5;
}

.action-config-section.compact {
  margin-top: 4px;
  padding: 6px 8px;
  background: #f5f7fa;
  border-radius: 4px;

  :deep(.el-form-item) {
    margin-bottom: 4px;
  }
}

.add-tag-row {
  display: flex;
  gap: 4px;
  align-items: center;
  margin-top: 4px;
}

.import-info {
  margin-top: 8px;
}

.no-linked-hint {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
}
</style>
