<template>
  <div class="table-designer" v-loading="loading">
    <div class="designer-header">
      <div class="header-left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>返回列表
        </el-button>
        <span class="title">表格设计器{{ tableId ? ' - 编辑模式' : ' - 新建模式' }}</span>
      </div>
      <div class="header-right">
        <el-button @click="handlePreview">
          <el-icon><View /></el-icon>预览
        </el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">
          <el-icon><Check /></el-icon>保存
        </el-button>
      </div>
    </div>

    <div class="designer-body">
      <!-- 左侧配置面板 -->
      <div class="config-panel">
        <el-tabs v-model="activeTab">
          <!-- 基础配置 -->
          <el-tab-pane label="基础配置" name="basic">
            <el-form label-width="100px" size="small">
              <el-form-item label="表格名称">
                <el-input v-model="designerConfig.tableName" placeholder="请输入表格名称" />
              </el-form-item>
              <el-form-item label="表格编码">
                <el-input v-model="designerConfig.tableCode" placeholder="请输入表格编码" />
              </el-form-item>
              <el-form-item label="组件分类">
                <el-radio-group v-model="designerConfig.componentCategory">
                  <el-radio label="common">通用组件库</el-radio>
                  <el-radio label="business">业务组件库</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="组件标签">
                <el-input
                  v-model="designerConfig.componentTags"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入标签，用逗号分隔"
                />
              </el-form-item>
              <el-form-item label="数据接口">
                <el-input v-model="designerConfig.apiUrl" placeholder="请输入API地址" />
              </el-form-item>
              <el-form-item label="请求方式">
                <el-select v-model="designerConfig.apiMethod" style="width: 100%">
                  <el-option label="GET" value="GET" />
                  <el-option label="POST" value="POST" />
                </el-select>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 表格设置 -->
          <el-tab-pane label="表格设置" name="tableSettings">
            <el-form label-width="100px" size="small">
              <el-form-item label="显示边框">
                <el-switch v-model="designerConfig.tableConfig.border" />
              </el-form-item>
              <el-form-item label="斑马纹">
                <el-switch v-model="designerConfig.tableConfig.stripe" />
              </el-form-item>
              <el-form-item label="组件尺寸">
                <el-radio-group v-model="designerConfig.tableConfig.size">
                  <el-radio-button label="large">大</el-radio-button>
                  <el-radio-button label="default">默认</el-radio-button>
                  <el-radio-button label="small">小</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="显示分页">
                <el-switch v-model="designerConfig.tableConfig.showPagination" />
              </el-form-item>
              <el-form-item label="每页条数" v-if="designerConfig.tableConfig.showPagination">
                <el-input-number v-model="designerConfig.tableConfig.pageSize" :min="5" :max="100" />
              </el-form-item>
              <el-form-item label="分页选项" v-if="designerConfig.tableConfig.showPagination">
                <el-select
                  v-model="designerConfig.tableConfig.pageSizes"
                  multiple
                  style="width: 100%"
                  placeholder="选择分页选项"
                >
                  <el-option :value="5" label="5" />
                  <el-option :value="10" label="10" />
                  <el-option :value="20" label="20" />
                  <el-option :value="50" label="50" />
                  <el-option :value="100" label="100" />
                </el-select>
              </el-form-item>
              <el-form-item label="显示序号">
                <el-switch v-model="designerConfig.tableConfig.showIndex" />
              </el-form-item>
              <el-form-item label="显示多选">
                <el-switch v-model="designerConfig.tableConfig.showSelection" />
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 工具栏配置 -->
          <el-tab-pane label="工具栏" name="toolbar">
            <div class="section-header">
              <span>工具栏按钮</span>
              <el-button type="primary" size="small" @click="addToolbarButton">
                <el-icon><Plus /></el-icon>添加按钮
              </el-button>
            </div>
            <div class="item-list">
              <div
                v-for="(btn, index) in designerConfig.toolbar.buttons"
                :key="index"
                class="item-card"
                :class="{ active: isSelected('toolbar', index) }"
                @click="selectElement('toolbar', index)"
              >
                <div class="item-info">
                  <el-tag :type="(btn.btnType as any) || 'info'" size="small">{{ btn.label }}</el-tag>
                  <span class="item-detail">{{ btn.action }}</span>
                </div>
                <div class="item-actions">
                  <el-icon v-if="index > 0" @click.stop="moveToolbarButton(index, -1)"><Top /></el-icon>
                  <el-icon v-if="index < designerConfig.toolbar.buttons.length - 1" @click.stop="moveToolbarButton(index, 1)"><Bottom /></el-icon>
                  <el-icon @click.stop="removeToolbarButton(index)"><Delete /></el-icon>
                </div>
              </div>
              <el-empty v-if="!designerConfig.toolbar.buttons.length" description="暂无工具栏按钮" :image-size="60" />
            </div>
          </el-tab-pane>

          <!-- 搜索配置 -->
          <el-tab-pane label="搜索配置" name="search">
            <div class="section-header">
              <span>搜索字段</span>
              <el-button type="primary" size="small" @click="addSearchField">
                <el-icon><Plus /></el-icon>添加字段
              </el-button>
            </div>
            <div class="item-list">
              <div
                v-for="(field, index) in designerConfig.searchFields"
                :key="index"
                class="item-card"
                :class="{ active: isSelected('search', index) }"
                @click="selectElement('search', index)"
              >
                <div class="item-info">
                  <el-tag size="small">{{ field.label }}</el-tag>
                  <span class="item-detail">{{ field.field }} ({{ getSearchTypeLabel(field.type) }})</span>
                </div>
                <div class="item-actions">
                  <el-icon v-if="index > 0" @click.stop="moveSearchField(index, -1)"><Top /></el-icon>
                  <el-icon v-if="index < designerConfig.searchFields.length - 1" @click.stop="moveSearchField(index, 1)"><Bottom /></el-icon>
                  <el-icon @click.stop="removeSearchField(index)"><Delete /></el-icon>
                </div>
              </div>
              <el-empty v-if="!designerConfig.searchFields.length" description="暂无搜索字段" :image-size="60" />
            </div>
          </el-tab-pane>

          <!-- 列配置 -->
          <el-tab-pane label="列配置" name="columns">
            <div class="section-header">
              <span>表格列</span>
              <el-dropdown @command="handleAddColumnType">
                <el-button type="primary" size="small">
                  <el-icon><Plus /></el-icon>添加列
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="text">文本列</el-dropdown-item>
                    <el-dropdown-item command="tag">标签列</el-dropdown-item>
                    <el-dropdown-item command="date">日期列</el-dropdown-item>
                    <el-dropdown-item command="index">序号列</el-dropdown-item>
                    <el-dropdown-item command="selection">多选列</el-dropdown-item>
                    <el-dropdown-item command="action">操作列</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <div class="item-list">
              <div
                v-for="(col, index) in designerConfig.tableColumns"
                :key="index"
                class="item-card"
                :class="{ active: isSelected('column', index) }"
                @click="selectElement('column', index)"
              >
                <div class="item-info">
                  <el-tag size="small" :type="getColumnTagType(col.type)">{{ col.label }}</el-tag>
                  <span class="item-detail">{{ col.prop || col.type }} ({{ getColumnTypeLabel(col.type) }})</span>
                </div>
                <div class="item-actions">
                  <el-icon v-if="index > 0" @click.stop="moveColumn(index, -1)"><Top /></el-icon>
                  <el-icon v-if="index < designerConfig.tableColumns.length - 1" @click.stop="moveColumn(index, 1)"><Bottom /></el-icon>
                  <el-icon @click.stop="removeColumn(index)"><Delete /></el-icon>
                </div>
              </div>
              <el-empty v-if="!designerConfig.tableColumns.length" description="请添加至少一个列" :image-size="60" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 中间预览区域 -->
      <div class="preview-panel">
        <div class="panel-title">预览效果</div>
        <div class="preview-area">
          <ConfigToolbar
            :title="designerConfig.tableName || '表格预览'"
            :buttons="designerConfig.toolbar.buttons"
            @action="() => {}"
          />
          <ConfigSearch
            v-if="designerConfig.searchFields.length"
            :fields="designerConfig.searchFields"
            :model-value="previewSearchParams"
            @update:model-value="updatePreviewSearchParams"
            @search="() => {}"
            @reset="resetPreviewSearchParams"
          />
          <ConfigTable
            :columns="designerConfig.tableColumns"
            :data="mockData"
            :config="designerConfig.tableConfig"
            :total="mockData.length"
            @action="() => {}"
            @page-change="() => {}"
            @selection-change="() => {}"
          />
        </div>
      </div>

      <!-- 右侧属性配置面板 -->
      <div class="property-panel">
        <div class="panel-title">属性配置</div>
        <div class="property-content">
          <!-- 无选中 -->
          <div v-if="!selectedElement" class="empty-tip">
            <p>请选择一个元素进行配置</p>
          </div>

          <!-- 列属性 -->
          <template v-else-if="selectedElement.type === 'column'">
            <el-form label-width="80px" size="small">
              <el-divider content-position="left">列属性</el-divider>
              <el-form-item label="列类型">
                <el-select v-model="currentColumn!.type" style="width: 100%" @change="handleColumnChange">
                  <el-option label="文本列" value="text" />
                  <el-option label="标签列" value="tag" />
                  <el-option label="日期列" value="date" />
                  <el-option label="序号列" value="index" />
                  <el-option label="多选列" value="selection" />
                  <el-option label="操作列" value="action" />
                </el-select>
              </el-form-item>
              <el-form-item label="列标题">
                <el-input v-model="currentColumn!.label" @change="handleColumnChange" />
              </el-form-item>
              <el-form-item label="字段名" v-if="currentColumn!.type !== 'index' && currentColumn!.type !== 'selection'">
                <el-input v-model="currentColumn!.prop" @change="handleColumnChange" />
              </el-form-item>
              <el-form-item label="列宽度">
                <el-input-number v-model="currentColumn!.width" :min="0" @change="handleColumnChange" />
              </el-form-item>
              <el-form-item label="最小宽度">
                <el-input-number v-model="currentColumn!.minWidth" :min="0" @change="handleColumnChange" />
              </el-form-item>
              <el-form-item label="对齐方式" v-if="currentColumn!.type !== 'index' && currentColumn!.type !== 'selection'">
                <el-select v-model="currentColumn!.align" style="width: 100%" @change="handleColumnChange">
                  <el-option label="左对齐" value="left" />
                  <el-option label="居中" value="center" />
                  <el-option label="右对齐" value="right" />
                </el-select>
              </el-form-item>
              <el-form-item label="固定列" v-if="currentColumn!.type !== 'index' && currentColumn!.type !== 'selection'">
                <el-select v-model="currentColumn!.fixed" style="width: 100%" clearable @change="handleColumnChange">
                  <el-option label="左侧固定" value="left" />
                  <el-option label="右侧固定" value="right" />
                </el-select>
              </el-form-item>
              <el-form-item label="溢出提示" v-if="currentColumn!.type === 'text' || !currentColumn!.type">
                <el-switch v-model="currentColumn!.showOverflowTooltip" @change="handleColumnChange" />
              </el-form-item>

              <!-- Tag 配置 -->
              <template v-if="currentColumn!.type === 'tag'">
                <el-divider content-position="left">标签映射</el-divider>
                <div v-for="(mapping, key) in currentColumn!.tagConfig?.mapping" :key="key" class="tag-config-item">
                  <div class="tag-config-row">
                    <el-input :model-value="key" disabled style="width: 80px" />
                    <el-input v-model="mapping.text" placeholder="显示文本" @change="handleColumnChange" />
                    <el-select v-model="mapping.type" style="width: 90px" @change="handleColumnChange">
                      <el-option label="主要" value="primary" />
                      <el-option label="成功" value="success" />
                      <el-option label="警告" value="warning" />
                      <el-option label="危险" value="danger" />
                      <el-option label="信息" value="info" />
                      <el-option label="默认" value="" />
                    </el-select>
                    <el-button size="small" type="danger" @click="removeTagMapping(key as string)">删除</el-button>
                  </div>
                </div>
                <div class="add-tag-mapping">
                  <el-input v-model="newTagKey" placeholder="值" style="width: 80px" />
                  <el-button size="small" @click="addTagMapping">添加映射</el-button>
                </div>
              </template>

              <!-- 操作列配置 -->
              <template v-if="currentColumn!.type === 'action'">
                <el-divider content-position="left">操作按钮</el-divider>
                <div class="action-buttons-config">
                  <div v-for="(btn, bi) in currentColumn!.actionConfig?.buttons" :key="bi" class="action-btn-item">
                    <div class="action-btn-row">
                      <el-input v-model="btn.label" placeholder="按钮名称" @change="handleColumnChange" />
                      <el-select v-model="btn.btnType" style="width: 90px" @change="handleColumnChange">
                        <el-option label="主要" value="primary" />
                        <el-option label="成功" value="success" />
                        <el-option label="警告" value="warning" />
                        <el-option label="危险" value="danger" />
                        <el-option label="信息" value="info" />
                        <el-option label="默认" value="" />
                      </el-select>
                      <el-button size="small" type="danger" @click="removeActionBtn(bi)">删除</el-button>
                    </div>
                    <!-- 按钮动作配置 -->
                    <div class="action-config-section">
                      <el-form label-width="70px" size="small">
                        <el-form-item label="动作标识">
                          <el-input v-model="btn.action" @change="handleColumnChange" />
                        </el-form-item>
                        <template v-if="btn.actionConfig">
                          <el-form-item label="动作类型">
                            <el-select v-model="btn.actionConfig.type" style="width: 100%" @change="handleColumnChange">
                              <el-option label="打开表单" value="openForm" />
                              <el-option label="打开表格" value="openTable" />
                              <el-option label="路由跳转" value="route" />
                              <el-option label="提交数据" value="submit" />
                              <el-option label="调用API" value="api" />
                              <el-option label="自定义" value="custom" />
                            </el-select>
                          </el-form-item>
                          <el-form-item label="目标编码" v-if="btn.actionConfig.type === 'openForm' || btn.actionConfig.type === 'openTable'">
                            <el-input v-model="btn.actionConfig.targetCode" @change="handleColumnChange" />
                          </el-form-item>
                          <el-form-item label="打开方式" v-if="btn.actionConfig.type === 'openForm' || btn.actionConfig.type === 'openTable'">
                            <el-radio-group v-model="btn.actionConfig.openMode" @change="handleColumnChange">
                              <el-radio-button label="dialog">弹窗</el-radio-button>
                              <el-radio-button label="drawer">抽屉</el-radio-button>
                              <el-radio-button label="page">页面</el-radio-button>
                            </el-radio-group>
                          </el-form-item>
                          <el-form-item label="路由路径" v-if="btn.actionConfig.type === 'route'">
                            <el-input v-model="btn.actionConfig.routePath" @change="handleColumnChange" />
                          </el-form-item>
                          <el-form-item label="确认提示" v-if="btn.actionConfig.type === 'submit'">
                            <el-input v-model="btn.actionConfig.confirmText" @change="handleColumnChange" />
                          </el-form-item>
                        </template>
                      </el-form>
                    </div>
                  </div>
                  <el-button type="primary" size="small" plain @click="addActionBtn">+ 添加操作按钮</el-button>
                </div>
              </template>
            </el-form>
          </template>

          <!-- 工具栏按钮属性 -->
          <template v-else-if="selectedElement.type === 'toolbar'">
            <el-form label-width="80px" size="small">
              <el-divider content-position="left">按钮属性</el-divider>
              <el-form-item label="按钮名称">
                <el-input v-model="currentToolbarButton!.label" @change="handleToolbarButtonChange" />
              </el-form-item>
              <el-form-item label="按钮类型">
                <el-select v-model="currentToolbarButton!.btnType" style="width: 100%" @change="handleToolbarButtonChange">
                  <el-option label="主要" value="primary" />
                  <el-option label="成功" value="success" />
                  <el-option label="警告" value="warning" />
                  <el-option label="危险" value="danger" />
                  <el-option label="信息" value="info" />
                  <el-option label="默认" value="" />
                </el-select>
              </el-form-item>
              <el-form-item label="图标">
                <el-input v-model="currentToolbarButton!.icon" placeholder="如 Plus, Download" @change="handleToolbarButtonChange" />
              </el-form-item>
              <el-form-item label="动作标识">
                <el-input v-model="currentToolbarButton!.action" @change="handleToolbarButtonChange" />
              </el-form-item>

              <el-divider content-position="left">动作配置</el-divider>
              <template v-if="currentToolbarButton!.actionConfig">
                <el-form-item label="动作类型">
                  <el-select v-model="currentToolbarButton!.actionConfig.type" style="width: 100%" @change="handleToolbarButtonChange">
                    <el-option label="打开表单" value="openForm" />
                    <el-option label="打开表格" value="openTable" />
                    <el-option label="路由跳转" value="route" />
                    <el-option label="提交数据" value="submit" />
                    <el-option label="调用API" value="api" />
                    <el-option label="自定义" value="custom" />
                  </el-select>
                </el-form-item>
                <el-form-item label="目标编码" v-if="currentToolbarButton!.actionConfig.type === 'openForm' || currentToolbarButton!.actionConfig.type === 'openTable'">
                  <el-input v-model="currentToolbarButton!.actionConfig.targetCode" @change="handleToolbarButtonChange" />
                </el-form-item>
                <el-form-item label="打开方式" v-if="currentToolbarButton!.actionConfig.type === 'openForm' || currentToolbarButton!.actionConfig.type === 'openTable'">
                  <el-radio-group v-model="currentToolbarButton!.actionConfig.openMode" @change="handleToolbarButtonChange">
                    <el-radio-button label="dialog">弹窗</el-radio-button>
                    <el-radio-button label="drawer">抽屉</el-radio-button>
                    <el-radio-button label="page">页面</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="路由路径" v-if="currentToolbarButton!.actionConfig.type === 'route'">
                  <el-input v-model="currentToolbarButton!.actionConfig.routePath" placeholder="/path" @change="handleToolbarButtonChange" />
                </el-form-item>
                <el-form-item label="选择模式" v-if="currentToolbarButton!.actionConfig.type === 'submit'">
                  <el-radio-group v-model="currentToolbarButton!.actionConfig.selectionMode" @change="handleToolbarButtonChange">
                    <el-radio-button label="none">无需选择</el-radio-button>
                    <el-radio-button label="single">单选</el-radio-button>
                    <el-radio-button label="multiple">多选</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="确认提示" v-if="currentToolbarButton!.actionConfig.type === 'submit'">
                  <el-input v-model="currentToolbarButton!.actionConfig.confirmText" @change="handleToolbarButtonChange" />
                </el-form-item>
              </template>
              <el-button v-else size="small" @click="initToolbarButtonAction">配置动作</el-button>
            </el-form>
          </template>

          <!-- 搜索字段属性 -->
          <template v-else-if="selectedElement.type === 'search'">
            <el-form label-width="80px" size="small">
              <el-divider content-position="left">搜索字段属性</el-divider>
              <el-form-item label="字段标签">
                <el-input v-model="currentSearchField!.label" @change="handleSearchFieldChange" />
              </el-form-item>
              <el-form-item label="字段名">
                <el-input v-model="currentSearchField!.field" @change="handleSearchFieldChange" />
              </el-form-item>
              <el-form-item label="组件类型">
                <el-select v-model="currentSearchField!.type" style="width: 100%" @change="handleSearchFieldChange">
                  <el-option label="输入框" value="input" />
                  <el-option label="选择器" value="select" />
                  <el-option label="日期" value="date" />
                  <el-option label="日期范围" value="daterange" />
                  <el-option label="数字" value="number" />
                </el-select>
              </el-form-item>
              <el-form-item label="占位文本">
                <el-input v-model="currentSearchField!.placeholder" @change="handleSearchFieldChange" />
              </el-form-item>
              <el-form-item label="可清空">
                <el-switch v-model="currentSearchField!.clearable" @change="handleSearchFieldChange" />
              </el-form-item>
              <el-form-item label="宽度(px)">
                <el-input-number v-model="currentSearchField!.width" :min="0" @change="handleSearchFieldChange" />
              </el-form-item>

              <!-- Select 类型选项配置 -->
              <template v-if="currentSearchField!.type === 'select'">
                <el-divider content-position="left">下拉选项</el-divider>
                <div v-for="(opt, oi) in currentSearchField!.options" :key="oi" class="option-item">
                  <div class="option-row">
                    <el-input v-model="opt.label" placeholder="显示文本" @change="handleSearchFieldChange" />
                    <el-input v-model="opt.value" placeholder="值" @change="handleSearchFieldChange" />
                    <el-button size="small" type="danger" @click="removeSearchOption(oi)">删除</el-button>
                  </div>
                </div>
                <el-button size="small" @click="addSearchOption">+ 添加选项</el-button>
              </template>
            </el-form>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createTable, updateTable, getTableConfig } from '@/api/table'
import type {
  ToolbarButton,
  SearchFieldConfig,
  TableColumnConfig,
  TableConfig,
  ButtonActionConfig,
  OptionItem
} from '@/api/table-standard'
import ConfigToolbar from '@/views/lowcode/components/ConfigToolbar.vue'
import ConfigSearch from '@/views/lowcode/components/ConfigSearch.vue'
import ConfigTable from '@/views/lowcode/components/ConfigTable.vue'

// ============ 路由 ============
const route = useRoute()
const router = useRouter()
const tableId = ref<number | null>(route.query.id ? Number(route.query.id) : null)

// ============ 状态 ============
const activeTab = ref('basic')
const loading = ref(false)
const newTagKey = ref('')

interface SelectedElement {
  type: 'column' | 'toolbar' | 'search'
  index: number
}

const selectedElement = ref<SelectedElement | null>(null)

// ============ 设计器配置（标准页格式） ============
const designerConfig = reactive({
  // 顶层 API 字段
  tableName: '',
  tableCode: '',
  componentCategory: 'business' as 'common' | 'business',
  componentTags: '',
  apiUrl: '',
  apiMethod: 'GET',

  // 标准页配置（存入 configJson）
  toolbar: {
    buttons: [] as ToolbarButton[]
  },
  searchFields: [] as SearchFieldConfig[],
  tableColumns: [] as TableColumnConfig[],
  tableConfig: {
    border: true,
    stripe: true,
    size: 'default' as 'large' | 'default' | 'small',
    showPagination: true,
    pageSize: 10,
    pageSizes: [10, 20, 50, 100],
    showIndex: false,
    showSelection: false
  } as TableConfig
})

// 预览搜索参数
const previewSearchParams = ref<Record<string, any>>({})

function updatePreviewSearchParams(val: Record<string, any>) {
  previewSearchParams.value = val
}

function resetPreviewSearchParams() {
  previewSearchParams.value = {}
}

// ============ 计算属性：当前选中元素 ============
const currentColumn = computed(() => {
  if (selectedElement.value?.type === 'column') {
    return designerConfig.tableColumns[selectedElement.value.index]
  }
  return null
})

const currentToolbarButton = computed(() => {
  if (selectedElement.value?.type === 'toolbar') {
    return designerConfig.toolbar.buttons[selectedElement.value.index]
  }
  return null
})

const currentSearchField = computed(() => {
  if (selectedElement.value?.type === 'search') {
    return designerConfig.searchFields[selectedElement.value.index]
  }
  return null
})

// ============ 预览 Mock 数据 ============
const mockData = computed(() => {
  const dataColumns = designerConfig.tableColumns.filter(c => c.prop && c.type !== 'action' && c.type !== 'index' && c.type !== 'selection')
  if (!dataColumns.length) return []

  const names = ['张三', '李四', '王五', '赵六', '孙七']
  return names.map((name, i) => {
    const row: Record<string, any> = { id: i + 1 }
    dataColumns.forEach(col => {
      if (col.prop) {
        if (col.prop.includes('name') || col.prop.includes('Name')) {
          row[col.prop] = `示例${i + 1}`
        } else if (col.prop.includes('time') || col.prop.includes('Time') || col.prop.includes('date') || col.prop.includes('Date')) {
          row[col.prop] = `2026-0${(i % 3) + 1}-${String((i + 1) * 5).padStart(2, '0')} 10:30:00`
        } else if (col.prop.includes('status') || col.prop.includes('Status')) {
          row[col.prop] = i % 2 === 0 ? '1' : '0'
        } else if (col.type === 'tag') {
          row[col.prop] = ['A', 'B', 'C'][i % 3]
        } else {
          row[col.prop] = `数据_${i + 1}`
        }
      }
    })
    return row
  })
})

// ============ 选中逻辑 ============
function isSelected(type: string, index: number): boolean {
  return selectedElement.value?.type === type && selectedElement.value.index === index
}

function selectElement(type: 'column' | 'toolbar' | 'search', index: number) {
  selectedElement.value = { type, index }
}

// ============ 工具栏按钮操作 ============
function addToolbarButton() {
  const btn: ToolbarButton = {
    label: '新按钮',
    btnType: 'primary',
    action: 'custom'
  }
  designerConfig.toolbar.buttons.push(btn)
  selectElement('toolbar', designerConfig.toolbar.buttons.length - 1)
}

function removeToolbarButton(index: number) {
  designerConfig.toolbar.buttons.splice(index, 1)
  if (selectedElement.value?.type === 'toolbar') {
    if (designerConfig.toolbar.buttons.length === 0) {
      selectedElement.value = null
    } else if (selectedElement.value.index >= designerConfig.toolbar.buttons.length) {
      selectedElement.value.index = designerConfig.toolbar.buttons.length - 1
    }
  }
}

function moveToolbarButton(index: number, direction: number) {
  const newIndex = index + direction
  const list = designerConfig.toolbar.buttons
  const item = list.splice(index, 1)[0]
  list.splice(newIndex, 0, item)
  if (selectedElement.value?.type === 'toolbar' && selectedElement.value.index === index) {
    selectedElement.value.index = newIndex
  }
}

function initToolbarButtonAction() {
  if (currentToolbarButton.value) {
    currentToolbarButton.value.actionConfig = {
      type: 'custom'
    }
  }
}

function handleToolbarButtonChange() {
  // 响应式已自动处理
}

// ============ 搜索字段操作 ============
function addSearchField() {
  const field: SearchFieldConfig = {
    field: `field_${designerConfig.searchFields.length + 1}`,
    label: `字段${designerConfig.searchFields.length + 1}`,
    type: 'input',
    placeholder: '请输入',
    clearable: true
  }
  designerConfig.searchFields.push(field)
  selectElement('search', designerConfig.searchFields.length - 1)
}

function removeSearchField(index: number) {
  designerConfig.searchFields.splice(index, 1)
  if (selectedElement.value?.type === 'search') {
    if (designerConfig.searchFields.length === 0) {
      selectedElement.value = null
    } else if (selectedElement.value.index >= designerConfig.searchFields.length) {
      selectedElement.value.index = designerConfig.searchFields.length - 1
    }
  }
}

function moveSearchField(index: number, direction: number) {
  const newIndex = index + direction
  const list = designerConfig.searchFields
  const item = list.splice(index, 1)[0]
  list.splice(newIndex, 0, item)
  if (selectedElement.value?.type === 'search' && selectedElement.value.index === index) {
    selectedElement.value.index = newIndex
  }
}

function handleSearchFieldChange() {
  // 响应式已自动处理
}

function addSearchOption() {
  if (currentSearchField.value) {
    if (!currentSearchField.value.options) {
      currentSearchField.value.options = []
    }
    currentSearchField.value.options.push({ label: '选项', value: '' })
  }
}

function removeSearchOption(index: number) {
  currentSearchField.value?.options?.splice(index, 1)
}

function getSearchTypeLabel(type: string): string {
  const labels: Record<string, string> = {
    input: '输入框',
    select: '选择器',
    date: '日期',
    daterange: '日期范围',
    number: '数字'
  }
  return labels[type] || type
}

// ============ 列操作 ============
function handleAddColumnType(type: string) {
  addColumn(type)
}

function addColumn(type: string = 'text') {
  const col: TableColumnConfig = {
    prop: `col_${designerConfig.tableColumns.length + 1}`,
    label: `列${designerConfig.tableColumns.length + 1}`,
    type: type as any,
    align: 'left'
  }

  if (type === 'text') {
    col.minWidth = 120
  } else if (type === 'tag') {
    col.width = 120
    col.tagConfig = { mapping: { '1': { text: '启用', type: 'success' }, '0': { text: '禁用', type: 'danger' } } }
  } else if (type === 'date') {
    col.width = 180
  } else if (type === 'index') {
    col.label = '序号'
    col.width = 60
    col.align = 'center'
  } else if (type === 'selection') {
    col.label = '选择'
    col.width = 55
    col.align = 'center'
  } else if (type === 'action') {
    col.label = '操作'
    col.width = 200
    col.align = 'center'
    col.fixed = 'right'
    col.actionConfig = {
      buttons: [
        { label: '编辑', btnType: 'primary', action: 'edit' },
        { label: '删除', btnType: 'danger', action: 'delete' }
      ]
    }
  }

  designerConfig.tableColumns.push(col)
  selectElement('column', designerConfig.tableColumns.length - 1)
}

function removeColumn(index: number) {
  designerConfig.tableColumns.splice(index, 1)
  if (selectedElement.value?.type === 'column') {
    if (designerConfig.tableColumns.length === 0) {
      selectedElement.value = null
    } else if (selectedElement.value.index >= designerConfig.tableColumns.length) {
      selectedElement.value.index = designerConfig.tableColumns.length - 1
    }
  }
}

function moveColumn(index: number, direction: number) {
  const newIndex = index + direction
  const list = designerConfig.tableColumns
  const item = list.splice(index, 1)[0]
  list.splice(newIndex, 0, item)
  if (selectedElement.value?.type === 'column' && selectedElement.value.index === index) {
    selectedElement.value.index = newIndex
  }
}

function handleColumnChange() {
  // 响应式已自动处理
}

function getColumnTypeLabel(type?: string): string {
  const labels: Record<string, string> = {
    text: '文本',
    tag: '标签',
    date: '日期',
    index: '序号',
    selection: '多选',
    action: '操作'
  }
  return labels[type || 'text'] || type || '文本'
}

function getColumnTagType(type?: string): string {
  const map: Record<string, string> = {
    text: '',
    tag: 'success',
    date: 'warning',
    index: 'info',
    selection: 'info',
    action: 'danger'
  }
  return map[type || 'text'] || ''
}

// Tag 映射操作
function addTagMapping() {
  if (!currentColumn.value || !newTagKey.value) return
  if (!currentColumn.value.tagConfig) {
    currentColumn.value.tagConfig = { mapping: {} }
  }
  currentColumn.value.tagConfig.mapping[newTagKey.value] = { text: '标签文本', type: 'info' }
  newTagKey.value = ''
}

function removeTagMapping(key: string) {
  if (currentColumn.value?.tagConfig?.mapping) {
    delete currentColumn.value.tagConfig.mapping[key]
  }
}

// 操作列按钮操作
function addActionBtn() {
  if (!currentColumn.value?.actionConfig) return
  currentColumn.value.actionConfig.buttons.push({
    label: '新按钮',
    btnType: 'primary',
    action: 'custom'
  })
}

function removeActionBtn(index: number) {
  currentColumn.value?.actionConfig?.buttons.splice(index, 1)
}

// ============ 保存 ============
const handleSave = async () => {
  if (!designerConfig.tableName) {
    ElMessage.warning('请输入表格名称')
    return
  }
  if (!designerConfig.tableCode) {
    ElMessage.warning('请输入表格编码')
    return
  }
  if (designerConfig.tableColumns.length === 0) {
    ElMessage.warning('请添加至少一个列')
    return
  }

  loading.value = true
  try {
    const configJson = JSON.stringify({
      pageCode: designerConfig.tableCode,
      pageName: designerConfig.tableName,
      toolbar: designerConfig.toolbar,
      searchFields: designerConfig.searchFields,
      tableColumns: designerConfig.tableColumns,
      tableConfig: designerConfig.tableConfig
    })

    const saveData = {
      tableName: designerConfig.tableName,
      tableCode: designerConfig.tableCode,
      componentCategory: designerConfig.componentCategory,
      componentTags: designerConfig.componentTags,
      apiUrl: designerConfig.apiUrl,
      apiMethod: designerConfig.apiMethod,
      pagination: designerConfig.tableConfig.showPagination,
      pageSize: designerConfig.tableConfig.pageSize,
      showIndex: designerConfig.tableConfig.showIndex,
      selection: designerConfig.tableConfig.showSelection,
      border: designerConfig.tableConfig.border,
      stripe: designerConfig.tableConfig.stripe,
      status: true,
      configJson,
      columns: designerConfig.tableColumns
        .filter(c => c.prop && c.type !== 'index' && c.type !== 'selection')
        .map((col, index) => ({
          columnName: col.label,
          columnCode: col.prop || '',
          label: col.label,
          width: col.width,
          minWidth: col.minWidth,
          sortable: false,
          fixed: col.fixed as string || undefined,
          align: col.align || 'left',
          visible: true,
          sortOrder: index
        }))
    }

    if (tableId.value) {
      await updateTable(tableId.value, saveData)
      ElMessage.success('更新表格配置成功')
    } else {
      const id = await createTable(saveData)
      tableId.value = id
      ElMessage.success('创建表格配置成功')
      router.replace({ query: { id: id.toString() } })
    }
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// ============ 加载 ============
const loadTableConfig = async () => {
  if (!tableId.value) return

  loading.value = true
  try {
    const data = await getTableConfig(tableId.value)

    // 加载顶层字段
    designerConfig.tableName = data.tableName
    designerConfig.tableCode = data.tableCode
    designerConfig.componentCategory = (data.componentCategory as any) || 'business'
    designerConfig.componentTags = (data.componentTags as any) || ''
    designerConfig.apiUrl = data.apiUrl || ''
    designerConfig.apiMethod = data.apiMethod || 'GET'

    // 解析 configJson（标准页格式）
    if (data.configJson) {
      try {
        const configObj = JSON.parse(data.configJson)
        designerConfig.toolbar = configObj.toolbar || { buttons: [] }
        designerConfig.searchFields = configObj.searchFields || []
        designerConfig.tableColumns = configObj.tableColumns || []
        designerConfig.tableConfig = {
          ...designerConfig.tableConfig,
          ...(configObj.tableConfig || {})
        }
      } catch (e) {
        console.error('解析 configJson 失败', e)
      }
    } else {
      // 无 configJson 时，从顶层字段填充
      designerConfig.tableConfig.border = data.border ?? true
      designerConfig.tableConfig.stripe = data.stripe ?? true
      designerConfig.tableConfig.showPagination = data.pagination ?? true
      designerConfig.tableConfig.pageSize = data.pageSize ?? 10
      designerConfig.tableConfig.showIndex = data.showIndex ?? false
      designerConfig.tableConfig.showSelection = data.selection ?? false

      // 从 columns 数组构建 tableColumns
      if (data.columns && data.columns.length > 0) {
        designerConfig.tableColumns = data.columns.map(col => ({
          prop: col.columnCode,
          label: col.label,
          width: col.width,
          minWidth: col.minWidth,
          align: (col.align as any) || 'left',
          fixed: col.fixed as any,
          showOverflowTooltip: col.showOverflowTooltip,
          type: 'text'
        }))
      }
    }

    ElMessage.success('加载表格配置成功')
  } catch (error) {
    ElMessage.error('加载表格配置失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// ============ 其他操作 ============
const handlePreview = () => {
  ElMessage.info('预览功能开发中...')
}

const handleBack = () => {
  router.back()
}

// ============ 初始化 ============
onMounted(() => {
  if (tableId.value) {
    loadTableConfig()
  } else {
    // 新建模式：初始化默认列
    designerConfig.tableColumns = [
      { prop: 'name', label: '名称', minWidth: 150, type: 'text' },
      { prop: 'createTime', label: '创建时间', width: 180, type: 'date' },
      { prop: 'status', label: '状态', width: 100, align: 'center', type: 'tag', tagConfig: { mapping: { '1': { text: '启用', type: 'success' }, '0': { text: '禁用', type: 'danger' } } } },
      { label: '操作', width: 180, align: 'center', fixed: 'right', type: 'action', actionConfig: { buttons: [{ label: '编辑', btnType: 'primary', action: 'edit' }, { label: '删除', btnType: 'danger', action: 'delete' }] } }
    ]
    designerConfig.toolbar.buttons = [
      { label: '新增', btnType: 'primary', icon: 'Plus', action: 'add' }
    ]
    designerConfig.searchFields = [
      { field: 'name', label: '名称', type: 'input', placeholder: '请输入名称', clearable: true }
    ]
  }
})
</script>

<style lang="scss" scoped>
.table-designer {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;

  .designer-header {
    height: 50px;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;

    .header-left {
      display: flex;
      align-items: center;

      .title {
        margin-left: 16px;
        font-size: 16px;
        font-weight: bold;
      }
    }
  }

  .designer-body {
    flex: 1;
    display: flex;
    overflow: hidden;

    .panel-title {
      padding: 12px 16px;
      font-weight: bold;
      border-bottom: 1px solid #e6e6e6;
    }

    // ========== 左侧配置面板 ==========
    .config-panel {
      width: 320px;
      min-width: 320px;
      flex-shrink: 0;
      background-color: #fff;
      border-right: 1px solid #e6e6e6;
      display: flex;
      flex-direction: column;

      :deep(.el-tabs) {
        display: flex;
        flex-direction: column;
        flex: 1;
        overflow: hidden;
      }

      :deep(.el-tabs__header) {
        margin: 0;
        padding: 0 16px;
        flex-shrink: 0;
      }

      :deep(.el-tabs__content) {
        padding: 16px;
        flex: 1;
        overflow-y: auto;
      }

      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        font-weight: 500;
      }

      .item-list {
        .item-card {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 12px;
          border: 1px solid #e6e6e6;
          border-radius: 4px;
          margin-bottom: 8px;
          cursor: pointer;

          &:hover {
            border-color: #409eff;
          }

          &.active {
            border-color: #409eff;
            background-color: #ecf5ff;
          }

          .item-info {
            display: flex;
            align-items: center;
            gap: 8px;
            flex: 1;
            overflow: hidden;

            .item-detail {
              font-size: 12px;
              color: #909399;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          .item-actions {
            display: flex;
            gap: 4px;
            flex-shrink: 0;

            .el-icon {
              cursor: pointer;
              color: #909399;

              &:hover {
                color: #409eff;
              }
            }
          }
        }
      }
    }

    // ========== 中间预览面板 ==========
    .preview-panel {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;
      background-color: #fff;
      margin: 0 1px;
      overflow: hidden;

      .panel-title {
        flex-shrink: 0;
      }

      .preview-area {
        flex: 1;
        padding: 20px;
        overflow-x: auto;
        overflow-y: auto;
      }
    }

    // ========== 右侧属性面板 ==========
    .property-panel {
      width: 340px;
      min-width: 340px;
      flex-shrink: 0;
      background-color: #fff;
      border-left: 1px solid #e6e6e6;
      display: flex;
      flex-direction: column;

      .panel-title {
        flex-shrink: 0;
      }

      .property-content {
        padding: 16px;
        flex: 1;
        overflow-y: auto;
      }

      .empty-tip {
        text-align: center;
        color: #909399;
        padding: 40px 0;
      }

      .tag-config-item {
        padding: 8px;
        background: #f5f7fa;
        border-radius: 4px;
        margin-bottom: 8px;
      }

      .tag-config-row,
      .option-row,
      .action-btn-row {
        display: flex;
        gap: 6px;
        align-items: center;
        margin-bottom: 6px;
      }

      .add-tag-mapping {
        display: flex;
        gap: 8px;
        margin-top: 8px;
      }

      .action-buttons-config {
        .action-btn-item {
          padding: 8px;
          background: #f5f7fa;
          border-radius: 4px;
          margin-bottom: 8px;

          .action-config-section {
            margin-top: 8px;
            padding-left: 8px;
            border-left: 2px solid #409eff;
          }
        }
      }

      .option-item {
        margin-bottom: 6px;
      }
    }
  }
}
</style>
