<template>
  <span>
    <template v-if="column.type === 'tag'">
      <el-tag :type="getTagType(value)">
        {{ getTagText(value) }}
      </el-tag>
    </template>
    <template v-else-if="column.type === 'image'">
      <el-image
        :src="value"
        :style="{ width: column.imageConfig?.width + 'px', height: column.imageConfig?.height + 'px' }"
        :fit="column.imageConfig?.fit"
        :preview-src-list="[value]"
      />
    </template>
    <template v-else-if="column.type === 'switch'">
      <el-switch :model-value="value" disabled />
    </template>
    <template v-else-if="column.type === 'progress'">
      <el-progress :percentage="value" :stroke-width="6" />
    </template>
    <template v-else-if="column.type === 'link'">
      <el-link
        :href="column.linkConfig?.href.replace('{value}', value)"
        :target="column.linkConfig?.target"
      >
        {{ value }}
      </el-link>
    </template>
    <template v-else>
      {{ value || '-' }}
    </template>
  </span>
</template>

<script setup lang="ts">
import type { TableColumn } from '@/types/page-v6'

// Props
interface Props {
  column: TableColumn
  value: any
}

defineProps<Props>()

/**
 * 获取标签类型
 */
function getTagType(value: any): string {
  return props.column.tagConfig?.[value]?.type || 'info'
}

/**
 * 获取标签文本
 */
function getTagText(value: any): string {
  return props.column.tagConfig?.[value]?.text || value
}
</script>
