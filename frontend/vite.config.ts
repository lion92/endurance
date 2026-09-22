import react from '@vitejs/plugin-react'
import { defineConfig } from 'vitest/config'

export default defineConfig({
  plugins: [react()],
  server: {
    // En développement, le site (5173) et l'API (8080) semblent ne faire qu'UNE origine :
    // pas de CORS à configurer, et le cookie SameSite=Strict voyage normalement.
    proxy: { '/api': 'http://localhost:8080' },
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    css: false,
  },
})
