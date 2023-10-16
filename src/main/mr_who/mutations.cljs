(ns mr-who.mutations
  (:require ["./render.mjs" :as r]
            ["./dom.mjs" :as dom]
            ["./utils.mjs" :as u]))

(defn merge-comp [app comp comp-data {:keys [f path]}]
  (let [p (println "p:" path)
        el (println "ele: " (get-in @app (conj path :mr-who/mounted-elements)))
        ]
    ))

#_(defn merge-comp [app comp comp-data {:keys [f path]}]
  (let [el (println "ele: " (get-in @app (conj path :mr-who/mounted-elements)))
        comp-ident (u/comp-ident comp-data)
        f (first comp-ident)
        s (second comp-ident)
        elements-at-path (get-in @app (conj path :mr-who/mounted-elements))

        ;;thing (u/to-map-path path (f (get-in @app path) comp-ident))


        rendered-thing (merge {} (r/render-and-meta-things (first elements-at-path) (apply comp comp-data) {:app app}))]
    (swap! app merge (merge {f {s comp-data}} rendered-thing))
    rendered-thing))
