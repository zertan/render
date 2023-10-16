// vite.config.js
import { defineConfig } from 'vite'

export default defineConfig({
    publicDir: false,
    build: {
        minify: true,
        outDir: "out",
        lib: {
          entry: { render: "/out/js/src/main/mr_who/render.mjs",
                   dom: "/out/js/src/main/mr_who/dom.mjs",
                   mutations: "/out/js/src/main/mr_who/mutations.mjs",
                   utils: "/out/js/src/main/mr_who/utils.mjs",},
          name: { render: "mr-who/render",
                  dom: "mr-who/dom",
                  mutations: "mr-who/mutations",
                  utils: "mr-who/utils"
                },
          formats: ['esm']
          // the proper extensions will be added
        },
      rollupOptions: {
        // make sure to externalize deps that shouldn't be bundled
        // into your library
        external: ["cherry-cljs", "@cljs-oss/module-deps", "squint-cljs"]
      }    
    }
})
