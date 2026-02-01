<template>
  <div class="monaco-editor-container" ref="editorContainer"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onBeforeUnmount } from 'vue'

interface Props {
  modelValue: string
  language?: string
  height?: string
  theme?: string
  options?: Record<string, any>
  readonly?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  language: 'javascript',
  height: '300px',
  theme: 'vs',
  options: () => ({}),
  readonly: false
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const editorContainer = ref<HTMLElement>()
let editorInstance: any = null

onMounted(() => {
  initEditor()
})

onBeforeUnmount(() => {
  if (editorInstance) {
    editorInstance.dispose()
  }
})

const initEditor = async () => {
  // 动态导入 Monaco Editor
  import('monaco-editor').then((monaco) => {
    if (!editorContainer.value) return

    editorInstance = monaco.editor.create(editorContainer.value, {
      value: props.modelValue,
      language: props.language,
      theme: props.theme,
      readOnly: props.readonly,
      minimap: { enabled: false },
      fontSize: 14,
      lineNumbers: 'on',
      scrollBeyondLastLine: false,
      automaticLayout: true,
      ...props.options
    })

    // 监听内容变化
    editorInstance.onDidChangeModelContent(() => {
      const value = editorInstance.getValue()
      emit('update:modelValue', value)
    })
  })
}

watch(() => props.modelValue, (newValue) => {
  if (editorInstance && newValue !== editorInstance.getValue()) {
    editorInstance.setValue(newValue)
  }
})

watch(() => props.language, (newLanguage) => {
  if (editorInstance) {
    const model = editorInstance.getModel()
    if (model) {
      monaco.editor.setModelLanguage(model, newLanguage)
    }
  }
})

watch(() => props.theme, (newTheme) => {
  if (editorInstance) {
    import('monaco-editor').then((monaco) => {
      monaco.editor.setTheme(newTheme)
    })
  }
})

watch(() => props.readonly, (newReadonly) => {
  if (editorInstance) {
    editorInstance.updateOptions({ readOnly: newReadonly })
  }
})

defineExpose({
  editor: () => editorInstance
})
</script>

<style scoped>
.monaco-editor-container {
  width: 100%;
  height: v-bind(height);
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}
</style>
