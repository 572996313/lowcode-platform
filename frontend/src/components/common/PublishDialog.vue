<template>
  <el-dialog
    :model-value="visible"
    title="发布页面"
    width="500px"
    :close-on-click-modal="false"
    @update:model-value="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="路由路径" prop="routePath">
        <el-input
          v-model="form.routePath"
          placeholder="请输入路由路径，如：/page/example"
          clearable
        />
        <div class="form-tip">
          路由路径必须以 / 开头，且在系统中必须唯一
        </div>
      </el-form-item>

      <el-form-item label="访问地址" v-if="form.routePath">
        <el-text type="info" size="small">
          {{ accessUrl }}
        </el-text>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">确定发布</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

interface Props {
  visible: boolean
  pageId?: number
  initialRoutePath?: string
}

const props = withDefaults(defineProps<Props>(), {
  initialRoutePath: ''
})

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'confirm', routePath: string): void
}>()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  routePath: props.initialRoutePath || ''
})

const rules = reactive<FormRules>({
  routePath: [
    { required: true, message: '请输入路由路径', trigger: 'blur' },
    {
      pattern: /^\/.*/,
      message: '路由路径必须以 / 开头',
      trigger: 'blur'
    },
    {
      pattern: /^[a-zA-Z0-9\/\-_]+$/,
      message: '路由路径只能包含字母、数字、/、-、_',
      trigger: 'blur'
    }
  ]
})

const accessUrl = computed(() => {
  if (!form.routePath) return ''
  return `${window.location.origin}${window.location.pathname}#${form.routePath}`
})

watch(() => props.initialRoutePath, (val) => {
  form.routePath = val || ''
})

watch(() => props.visible, (val) => {
  if (val) {
    form.routePath = props.initialRoutePath || ''
    formRef.value?.clearValidate()
  }
})

function handleClose() {
  emit('update:visible', false)
  form.routePath = ''
  formRef.value?.clearValidate()
}

async function handleConfirm() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true
    emit('confirm', form.routePath)
  } catch (error) {
    ElMessage.error('请检查表单输入')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
