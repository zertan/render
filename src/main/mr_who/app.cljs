(ns mr-who.app
  (:require ["./micro_ns.mjs" :as m]
            ["./render.mjs" :as r]
            ["./normalize.mjs" :as n]))

(defonce app (atom nil))

(reset! app {:counter/id {"1" {:counter/id "1"
                               :value 1
                               :name "a"
                               :mr-who/mounted-elements []}
                          "2" {:counter/id "2"
                               :value 2
                               :name "b"
                               :mr-who/mounted-elements []}}})

(r/render-and-meta-things (js/document.getElementById "app")
                          (m/counter-list-comp app {:counter-list/id "1"})
                          {:app app})

#_(reset! vdom (n/db :mr-who/id (r/render-and-meta-things (js/document.getElementById "app")
                                                        (m/counter-comp app vdom {:counter/id "1"})
                                                        {:app app})))