// vite.config.js
import { defineConfig } from 'vite'

export default defineConfig({
    publicDir: false,
    build: {
        minify: true,
        outDir: "out",
        lib: {
            entry: "src/js/index",
            name: 'mr-who',
            formats: ['esm'],
            // the proper extensions will be added
            fileName: 'mr_who'
        },
      rollupOptions: {
        // make sure to externalize deps that shouldn't be bundled
        // into your library
        external: ["cherry-cljs", "@cljs-oss/module-deps", "squint-cljs"]
      }    
    }
})
