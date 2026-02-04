<template>
  <div
    v-if="componentVisible"
    class="form-render-free-canvas"
    :style="componentStyle"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :label-width="config.labelWidth ? config.labelWidth + 'px' : undefined"
      :label-position="config.labelPosition || 'left'"
    >
      <el-row :gutter="16">
        <el-col
          v-for="field in config.fields"
          :key="field.id"
          :span="getFieldWidth(field)"
        >
          <el-form-item
            :label="field.label"
            :prop="field.fieldCode"
            :required="field.required"
          >
            <!-- 输入框 -->
            <el-input
              v-if="field.fieldType === 'input'"
              v-model="formData[field.fieldCode]"
              :placeholder="field.placeholder"
              clearable
            />

            <!-- 文本域 -->
            <el-input
              v-else-if="field.fieldType === 'textarea'"
              v-model="formData[field.fieldCode]"
              type="textarea"
              :placeholder="field.placeholder"
              :rows="3"
            />

            <!-- 下拉框 -->
            <el-select
              v-else-if="field.fieldType === 'select'"
              v-model="formData[field.fieldCode]"
              :placeholder="field.placeholder"
              clearable
              style="width: 100%"
            >
              <el-option
                v-for="opt in field.options"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </el-select>

            <!-- 日期选择器 -->
            <el-date-picker
              v-else-if="field.fieldType === 'date' || field.fieldType === 'datetime'"
              v-model="formData[field.fieldCode]"
              :type="field.fieldType === 'date' ? 'date' : 'datetime'"
              :placeholder="field.placeholder"
              style="width: 100%"
            />

            <!-- 数字输入框 -->
            <el-input-number
              v-else-if="field.fieldType === 'number'"
              v-model="formData[field.fieldCode]"
              :placeholder="field.placeholder"
              style="width: 100%"
            />

            <!-- 开关 -->
            <el-switch
              v-else-if="field.fieldType === 'switch'"
              v-model="formData[field.fieldCode]"
            />

            <!-- 单选框 -->
            <el-radio-group
              v-else-if="field.fieldType === 'radio'"
              v-model="formData[field.fieldCode]"
            >
              <el-radio
                v-for="opt in field.options"
                :key="opt.value"
                :label="opt.value"
              >
                {{ opt.label }}
              </el-radio>
            </el-radio-group>

            <!-- 复选框 -->
            <el-checkbox-group
              v-else-if="field.fieldType === 'checkbox'"
              v-model="formData[field.fieldCode]"
            >
              <el-checkbox
                v-for="opt in field.options"
                :key="opt.value"
                :label="opt.value"
              >
                {{ opt.label }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import type { ComponentPosition, ComponentStyle, FormComponentConfig, FormFieldConfig, FieldType } from '@/types/page-free-canvas'

interface Props {
  config: FormComponentConfig
  position: ComponentPosition
  style?: ComponentStyle
}

const props = defineProps<Props>()

// 表单数据
const formRef = ref()
const formData = reactive<Record<string, any>>({})

// 组件可见性
const componentVisible = computed(() => true)

/**
 * 计算组件样式
 */
const componentStyle = computed(() => {
  const baseStyle: Record<string, any> = {
    left: props.position.x + 'px',
    top: props.position.y + 'px',
    width: props.position.width + 'px',
    height: props.position.height + 'px',
    zIndex: props.position.zIndex || 1
  }

  if (props.style) {
    Object.assign(baseStyle, props.style.customStyles)
    if (props.style.backgroundColor) baseStyle.backgroundColor = props.style.backgroundColor
    if (props.style.borderRadius) baseStyle.borderRadius = props.style.borderRadius
    if (props.style.border) baseStyle.border = props.style.border
    if (props.style.padding) baseStyle.padding = props.style.padding
    if (props.style.margin) baseStyle.margin = props.style.margin
    if (props.style.boxShadow) baseStyle.boxShadow = props.style.boxShadow
    if (props.style.opacity !== undefined) baseStyle.opacity = props.style.opacity
  }

  return baseStyle
})

/**
 * 获取字段宽度（栅格占比）
 */
function getFieldWidth(field: FormFieldConfig): number {
  return field.width || 12
}

/**
 * 初始化表单数据
 */
function initFormData() {
  props.config.fields.forEach(field => {
    if (field.fieldType === 'checkbox') {
      formData[field.fieldCode] = field.defaultValue || []
    } else {
      formData[field.fieldCode] = field.defaultValue ?? ''
    }
  })
}

onMounted(() => {
  initFormData()
})
</script>

<style scoped lang="scss">
.form-render-free-canvas {
  position: absolute;
  overflow: auto;
  background: #fff;
  border-radius: 4px;
  padding: 20px;

  :deep(.el-form-item__label) {
    font-weight: 500;
  }
}
</style>
