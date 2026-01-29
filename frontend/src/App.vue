<template>
  <el-config-provider :locale="zhCn">
    <router-view />

    <!-- 全局弹窗容器 -->
    <DialogContainer />

    <!-- 全局抽屉容器 -->
    <DrawerContainer />
  </el-config-provider>
</template>

<script setup lang="ts">
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { DialogContainer } from '@/utils/dialogManager'
import { DrawerContainer } from '@/utils/drawerManager'
</script>

<style>
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
