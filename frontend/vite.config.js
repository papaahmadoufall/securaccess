import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    host: '0.0.0.0',
    strictPort: false,
    watch: {
      usePolling: true
    }
  },
  build: {
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'index.html')
      }
    },
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    minify: 'terser'
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      'vue': 'vue/dist/vue.esm-bundler.js'
    }
  }
})