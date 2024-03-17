(ns mr-who.app
  (:require [mr-who.micro-ns :as m]
            [mr-who.render :as r]
            [mr-who.dom :as dom]
            [clojure.spec.alpha :as s]
            [gadget.inspector :as inspector]))

(defonce app (atom nil))

(inspector/inspect "App state" app)

(defn init []
  (set! (.-app js/window) app)
  (reset! app {:counter-list/id {1 {:counters [[:counter/id 1] [:counter/id 2]]}}
               :counter/id {1 {:counter/id 1
                               :value 1
                               :name "a"
                               :mr-who/mounted-elements []}
                            2 {:counter/id 2
                               :value 2
                               :name "b"
                               :mr-who/mounted-elements []}}})

  (println (dom/div {:id :root}
                    (dom/div {:id :sub-node} "asd")))
  (dom/append-helper (js/document.getElementById "app")
                     (:node (:root (dom/div {:id :root}
                                            (dom/p {:id :c} "Coola")
                                            (dom/span {} "11asaada")
                                            (dom/div {:id :sub-node} "asd"))))
                     #_(m/counter-list-comp app {:counter-list/id 1})
                     ))

#_(reset! vdom (n/db :mr-who/id (r/render-and-meta-things (js/document.getElementById "app")
                                                        (m/counter-comp app vdom {:counter/id "1"})
                                                        {:app app})))
