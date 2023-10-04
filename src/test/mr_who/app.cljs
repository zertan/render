(ns mr-who.app
  (:require [mr-who.render :as r]
            [pyramid.core :as p]
            [goog.dom :as gdom]))

(defonce vdom (atom nil))

(defonce app (atom nil))

(defn init []

  (println "init")

  (if (.-firstChild (js/document.getElementById "app"))
    (gdom/removeChildren (js/document.getElementById "app")))

  (reset! app (p/db [{:id 1
                      :value 1
                      :mounted-elements []}
                     {:id 2
                      :value 2
                      :mounted-elements []}]))
  
  (reset! vdom (p/db [(r/render-and-meta-things (js/document.getElementById "app") [:div {}
                                                                                    [:div {} "aba"]
                                                                                    [:app-cursor [:id 1]]]
                                                {:app app})])))
  

(comment

  (let [to-node (get-in @vdom [:id (second (keys (:id @vdom))) :element])]
    (r/add-new-elements vdom app to-node [:div {}
                                          [:div {} "aba"]
                                          [:app-cursor [:id 1]]]))
  
  (do
    (swap! app update-in [:id 1 :value] inc)
    
    (let [v (get-in @app [:id 1 :value])
          mounted-ids (get-in @app [:id 1 :mounted-elements])
                                        ;new-nodes (take (count (get-in @app [:id 1 :mounted-elements])) (repeat (js/document.createTextNode 1)))
          ]
      (for [id mounted-ids]
        (let [new-node (js/document.createTextNode v)
              node (get-in @vdom (conj id :element))]
          (println node)
          (gdom/replaceNode new-node node)
          (swap! vdom assoc-in (conj id :element) new-node))
        )))

  (get-in @vdom (first (get-in @app [:id 1 :mounted-elements])))
  )
