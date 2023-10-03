(ns build
  (:require [cljs.build.api :as b]))

(b/build "src"
  {:output-dir "public/js"
   :output-to "public/js/main.js"
   :optimizations :advanced
   :main 'hello-world.core
   :install-deps true
   :npm-deps {"flowbite" "1.8.1"}
   })
