(ns mr-who.micro-ns
  (:require ["./dom.mjs" :as dom]))

(defn click-factory [app id]
  (fn [e]
    (swap! app update-in [:counter/id id :value] inc)
    
    (let [v (get-in @app [:counter/id id :value])
          elements (get-in @app [:counter/id id :mr-who/mounted-elements])
          mounted-ids (filterv #(not (nil? %)) (mapv #(if (= (first %) :value) (second %)) elements))]
      (doall
       (for [id mounted-ids]
        #_(println "id: " mounted-ids)
        (let [new-node (js/document.createTextNode v)
              node (get-in @app (conj id :element))]
          #_(println "new: " new-node)
          #_(println "old: " (conj id :element))
          (dom/replace-node new-node node)
          #_(println "aasdd")
          (swap! app assoc-in (conj id :element) new-node)))))))

(defn counter-comp
  #_([app vdom {:keys [id]} & children] (into (counter-comp app vdom id) children))
  ([app {:counter/keys [id]}]
   [:div {}
    [:button {:on-click (click-factory app id)} "click"]
    [:div {} "Counter " [:app-cursor [:counter/id id :name]] ": " [:app-cursor [:counter/id id :value]]]]))

(defn counter-list-comp
  #_([ident & children] (into (counter-list-comp) children))
  ([app {:counter-list/keys [id]}]
   [:div{}
    (for [c [1 2] #_[:app-cursor [:counter-list/id id :counters]]]
      (counter-comp app {:counter/id c}))]))
