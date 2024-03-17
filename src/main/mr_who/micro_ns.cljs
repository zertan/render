(ns mr-who.micro-ns
  (:require [mr-who.dom :as d :refer [div button]]))

(defn click-factory [app id]
  (fn [e]
    (swap! app update-in [:counter/id id :value] inc)
    
    (let [v (get-in @app [:counter/id id :value])
          elements (get-in @app [:counter/id id :mr-who/mounted-elements])
          mounted-ids (filterv #(not (nil? %)) (mapv #(if (= (first %) :value) (second %)) elements))]
      (doall
       (for [id mounted-ids]
         (let [new-node (js/document.createTextNode v)
               node (get-in @app (conj id :element))]
           (d/replace-node new-node node)
           (swap! app assoc-in (conj id :element) new-node)))))))

(defn counter-comp [app {:counter/keys [id]}]
  (let [cf (click-factory app id)]
    (d/div {}
           (d/button {:on-click cf} "click")
           (d/div {} "Counter " [:app-cursor [:counter/id id :name]] ": " [:app-cursor [:counter/id id :value]]))))

(defn counter-list-comp [app {:counter-list/keys [id]}]
  (d/div {}
    (for [c (get-in @app [:counter-list/id id :counters]) #_[:app-cursor [:counter-list/id id :counters]]]
          (counter-comp app {:counter/id (second c)}))))
