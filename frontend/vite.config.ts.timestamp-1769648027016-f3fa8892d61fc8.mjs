// vite.config.ts
import { defineConfig } from "file:///D:/E_Resource/IDEA_SPACE/self/lowcode-platform/frontend/node_modules/vite/dist/node/index.js";
import vue from "file:///D:/E_Resource/IDEA_SPACE/self/lowcode-platform/frontend/node_modules/@vitejs/plugin-vue/dist/index.mjs";
import vueJsx from "file:///D:/E_Resource/IDEA_SPACE/self/lowcode-platform/frontend/node_modules/@vitejs/plugin-vue-jsx/dist/index.mjs";
import AutoImport from "file:///D:/E_Resource/IDEA_SPACE/self/lowcode-platform/frontend/node_modules/unplugin-auto-import/dist/vite.js";
import Components from "file:///D:/E_Resource/IDEA_SPACE/self/lowcode-platform/frontend/node_modules/unplugin-vue-components/dist/vite.js";
import { ElementPlusResolver } from "file:///D:/E_Resource/IDEA_SPACE/self/lowcode-platform/frontend/node_modules/unplugin-vue-components/dist/resolvers.js";
import path from "path";
var __vite_injected_original_dirname = "D:\\E_Resource\\IDEA_SPACE\\self\\lowcode-platform\\frontend";
var vite_config_default = defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    // 自动导入 Vue API
    AutoImport({
      imports: ["vue", "vue-router", "pinia", "@vueuse/core"],
      resolvers: [ElementPlusResolver()],
      dts: "src/types/auto-imports.d.ts",
      eslintrc: {
        enabled: true
      }
    }),
    // 自动导入组件
    Components({
      resolvers: [ElementPlusResolver()],
      dts: "src/types/components.d.ts",
      dirs: ["src/components"]
    })
  ],
  resolve: {
    alias: {
      "@": path.resolve(__vite_injected_original_dirname, "src")
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/styles/variables.scss" as *;`
      }
    }
  },
  server: {
    port: 3e3,
    host: true,
    open: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true
      }
    }
  },
  build: {
    outDir: "dist",
    sourcemap: false,
    chunkSizeWarningLimit: 2e3,
    rollupOptions: {
      output: {
        manualChunks: {
          "vue-vendor": ["vue", "vue-router", "pinia"],
          "element-plus": ["element-plus"],
          "echarts": ["echarts", "vue-echarts"],
          "monaco": ["monaco-editor"]
        }
      }
    }
  }
});
export {
  vite_config_default as default
};
//# sourceMappingURL=data:application/json;base64,ewogICJ2ZXJzaW9uIjogMywKICAic291cmNlcyI6IFsidml0ZS5jb25maWcudHMiXSwKICAic291cmNlc0NvbnRlbnQiOiBbImNvbnN0IF9fdml0ZV9pbmplY3RlZF9vcmlnaW5hbF9kaXJuYW1lID0gXCJEOlxcXFxFX1Jlc291cmNlXFxcXElERUFfU1BBQ0VcXFxcc2VsZlxcXFxsb3djb2RlLXBsYXRmb3JtXFxcXGZyb250ZW5kXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ZpbGVuYW1lID0gXCJEOlxcXFxFX1Jlc291cmNlXFxcXElERUFfU1BBQ0VcXFxcc2VsZlxcXFxsb3djb2RlLXBsYXRmb3JtXFxcXGZyb250ZW5kXFxcXHZpdGUuY29uZmlnLnRzXCI7Y29uc3QgX192aXRlX2luamVjdGVkX29yaWdpbmFsX2ltcG9ydF9tZXRhX3VybCA9IFwiZmlsZTovLy9EOi9FX1Jlc291cmNlL0lERUFfU1BBQ0Uvc2VsZi9sb3djb2RlLXBsYXRmb3JtL2Zyb250ZW5kL3ZpdGUuY29uZmlnLnRzXCI7aW1wb3J0IHsgZGVmaW5lQ29uZmlnIH0gZnJvbSAndml0ZSdcclxuaW1wb3J0IHZ1ZSBmcm9tICdAdml0ZWpzL3BsdWdpbi12dWUnXHJcbmltcG9ydCB2dWVKc3ggZnJvbSAnQHZpdGVqcy9wbHVnaW4tdnVlLWpzeCdcclxuaW1wb3J0IEF1dG9JbXBvcnQgZnJvbSAndW5wbHVnaW4tYXV0by1pbXBvcnQvdml0ZSdcclxuaW1wb3J0IENvbXBvbmVudHMgZnJvbSAndW5wbHVnaW4tdnVlLWNvbXBvbmVudHMvdml0ZSdcclxuaW1wb3J0IHsgRWxlbWVudFBsdXNSZXNvbHZlciB9IGZyb20gJ3VucGx1Z2luLXZ1ZS1jb21wb25lbnRzL3Jlc29sdmVycydcclxuaW1wb3J0IHBhdGggZnJvbSAncGF0aCdcclxuXHJcbmV4cG9ydCBkZWZhdWx0IGRlZmluZUNvbmZpZyh7XHJcbiAgcGx1Z2luczogW1xyXG4gICAgdnVlKCksXHJcbiAgICB2dWVKc3goKSxcclxuICAgIC8vIFx1ODFFQVx1NTJBOFx1NUJGQ1x1NTE2NSBWdWUgQVBJXHJcbiAgICBBdXRvSW1wb3J0KHtcclxuICAgICAgaW1wb3J0czogWyd2dWUnLCAndnVlLXJvdXRlcicsICdwaW5pYScsICdAdnVldXNlL2NvcmUnXSxcclxuICAgICAgcmVzb2x2ZXJzOiBbRWxlbWVudFBsdXNSZXNvbHZlcigpXSxcclxuICAgICAgZHRzOiAnc3JjL3R5cGVzL2F1dG8taW1wb3J0cy5kLnRzJyxcclxuICAgICAgZXNsaW50cmM6IHtcclxuICAgICAgICBlbmFibGVkOiB0cnVlXHJcbiAgICAgIH1cclxuICAgIH0pLFxyXG4gICAgLy8gXHU4MUVBXHU1MkE4XHU1QkZDXHU1MTY1XHU3RUM0XHU0RUY2XHJcbiAgICBDb21wb25lbnRzKHtcclxuICAgICAgcmVzb2x2ZXJzOiBbRWxlbWVudFBsdXNSZXNvbHZlcigpXSxcclxuICAgICAgZHRzOiAnc3JjL3R5cGVzL2NvbXBvbmVudHMuZC50cycsXHJcbiAgICAgIGRpcnM6IFsnc3JjL2NvbXBvbmVudHMnXVxyXG4gICAgfSlcclxuICBdLFxyXG4gIHJlc29sdmU6IHtcclxuICAgIGFsaWFzOiB7XHJcbiAgICAgICdAJzogcGF0aC5yZXNvbHZlKF9fZGlybmFtZSwgJ3NyYycpXHJcbiAgICB9XHJcbiAgfSxcclxuICBjc3M6IHtcclxuICAgIHByZXByb2Nlc3Nvck9wdGlvbnM6IHtcclxuICAgICAgc2Nzczoge1xyXG4gICAgICAgIGFkZGl0aW9uYWxEYXRhOiBgQHVzZSBcIkAvc3R5bGVzL3ZhcmlhYmxlcy5zY3NzXCIgYXMgKjtgXHJcbiAgICAgIH1cclxuICAgIH1cclxuICB9LFxyXG4gIHNlcnZlcjoge1xyXG4gICAgcG9ydDogMzAwMCxcclxuICAgIGhvc3Q6IHRydWUsXHJcbiAgICBvcGVuOiB0cnVlLFxyXG4gICAgcHJveHk6IHtcclxuICAgICAgJy9hcGknOiB7XHJcbiAgICAgICAgdGFyZ2V0OiAnaHR0cDovL2xvY2FsaG9zdDo4MDgwJyxcclxuICAgICAgICBjaGFuZ2VPcmlnaW46IHRydWVcclxuICAgICAgfVxyXG4gICAgfVxyXG4gIH0sXHJcbiAgYnVpbGQ6IHtcclxuICAgIG91dERpcjogJ2Rpc3QnLFxyXG4gICAgc291cmNlbWFwOiBmYWxzZSxcclxuICAgIGNodW5rU2l6ZVdhcm5pbmdMaW1pdDogMjAwMCxcclxuICAgIHJvbGx1cE9wdGlvbnM6IHtcclxuICAgICAgb3V0cHV0OiB7XHJcbiAgICAgICAgbWFudWFsQ2h1bmtzOiB7XHJcbiAgICAgICAgICAndnVlLXZlbmRvcic6IFsndnVlJywgJ3Z1ZS1yb3V0ZXInLCAncGluaWEnXSxcclxuICAgICAgICAgICdlbGVtZW50LXBsdXMnOiBbJ2VsZW1lbnQtcGx1cyddLFxyXG4gICAgICAgICAgJ2VjaGFydHMnOiBbJ2VjaGFydHMnLCAndnVlLWVjaGFydHMnXSxcclxuICAgICAgICAgICdtb25hY28nOiBbJ21vbmFjby1lZGl0b3InXVxyXG4gICAgICAgIH1cclxuICAgICAgfVxyXG4gICAgfVxyXG4gIH1cclxufSlcclxuIl0sCiAgIm1hcHBpbmdzIjogIjtBQUFtVyxTQUFTLG9CQUFvQjtBQUNoWSxPQUFPLFNBQVM7QUFDaEIsT0FBTyxZQUFZO0FBQ25CLE9BQU8sZ0JBQWdCO0FBQ3ZCLE9BQU8sZ0JBQWdCO0FBQ3ZCLFNBQVMsMkJBQTJCO0FBQ3BDLE9BQU8sVUFBVTtBQU5qQixJQUFNLG1DQUFtQztBQVF6QyxJQUFPLHNCQUFRLGFBQWE7QUFBQSxFQUMxQixTQUFTO0FBQUEsSUFDUCxJQUFJO0FBQUEsSUFDSixPQUFPO0FBQUE7QUFBQSxJQUVQLFdBQVc7QUFBQSxNQUNULFNBQVMsQ0FBQyxPQUFPLGNBQWMsU0FBUyxjQUFjO0FBQUEsTUFDdEQsV0FBVyxDQUFDLG9CQUFvQixDQUFDO0FBQUEsTUFDakMsS0FBSztBQUFBLE1BQ0wsVUFBVTtBQUFBLFFBQ1IsU0FBUztBQUFBLE1BQ1g7QUFBQSxJQUNGLENBQUM7QUFBQTtBQUFBLElBRUQsV0FBVztBQUFBLE1BQ1QsV0FBVyxDQUFDLG9CQUFvQixDQUFDO0FBQUEsTUFDakMsS0FBSztBQUFBLE1BQ0wsTUFBTSxDQUFDLGdCQUFnQjtBQUFBLElBQ3pCLENBQUM7QUFBQSxFQUNIO0FBQUEsRUFDQSxTQUFTO0FBQUEsSUFDUCxPQUFPO0FBQUEsTUFDTCxLQUFLLEtBQUssUUFBUSxrQ0FBVyxLQUFLO0FBQUEsSUFDcEM7QUFBQSxFQUNGO0FBQUEsRUFDQSxLQUFLO0FBQUEsSUFDSCxxQkFBcUI7QUFBQSxNQUNuQixNQUFNO0FBQUEsUUFDSixnQkFBZ0I7QUFBQSxNQUNsQjtBQUFBLElBQ0Y7QUFBQSxFQUNGO0FBQUEsRUFDQSxRQUFRO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixNQUFNO0FBQUEsSUFDTixPQUFPO0FBQUEsTUFDTCxRQUFRO0FBQUEsUUFDTixRQUFRO0FBQUEsUUFDUixjQUFjO0FBQUEsTUFDaEI7QUFBQSxJQUNGO0FBQUEsRUFDRjtBQUFBLEVBQ0EsT0FBTztBQUFBLElBQ0wsUUFBUTtBQUFBLElBQ1IsV0FBVztBQUFBLElBQ1gsdUJBQXVCO0FBQUEsSUFDdkIsZUFBZTtBQUFBLE1BQ2IsUUFBUTtBQUFBLFFBQ04sY0FBYztBQUFBLFVBQ1osY0FBYyxDQUFDLE9BQU8sY0FBYyxPQUFPO0FBQUEsVUFDM0MsZ0JBQWdCLENBQUMsY0FBYztBQUFBLFVBQy9CLFdBQVcsQ0FBQyxXQUFXLGFBQWE7QUFBQSxVQUNwQyxVQUFVLENBQUMsZUFBZTtBQUFBLFFBQzVCO0FBQUEsTUFDRjtBQUFBLElBQ0Y7QUFBQSxFQUNGO0FBQ0YsQ0FBQzsiLAogICJuYW1lcyI6IFtdCn0K
