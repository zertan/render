(ns hello-world.test
  (:require [hello-world.ikota :as ik]
            [goog.dom :as gdom]))

#_(defn modify-dom [normalized-component]
  (let [[{:keys [reagent-render]} & params] normalized-component
        {:keys [hiccup dom container]} (@mounted-components normalized-component)
        new-hiccup (ik/component->hiccup normalized-component)
        new-dom (patch hiccup new-hiccup dom)]
    (println new-hiccup)
    (swap! mounted-components assoc normalized-component {:hiccup new-hiccup
                                                          :dom new-dom
                                                          :container container})
    (when (not= dom new-dom)
      (gdom/removeChildren container)
      (.. container (appendChild new-dom)))))



(comment
  (-> js/document
      (.getElementById "app")
      (.. (appendChild (js/document.createElement "h1"))))

  (-> js/document
      (.createElement "h1")
      (.. (appendChild "Hello"))
      )
  
  (let [doc js/document
        new-dom (ik/hiccup->dom
                 [:div {:class "abc"}
                  [:div "asd"]])
        container (doc.getElementById "app")
        h1 (doc.createElement "h1")]
    (.. h1 (appendChild  "Hello"))
    (.. container (appendChild h1))
    
    #_(when (not= dom new-dom)
        (gdom/removeChildren container)
        (.. container (gdom/appendChild new-dom)))))
