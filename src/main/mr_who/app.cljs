(ns mr-who.app
  (:require []))

(declare app)



(defonce vdom (clojure.core/atom (p/db [(do (gdom/removeChildren (js/document.getElementById "app"))
                                            (render-and-meta-things (js/document.getElementById "app") [:div {} "no-el"]))])))

(defonce app (clojure.core/atom (p/db [{:id 1
                                        :value 1
                                        :mounted-elements []}
                                       {:id 2
                                        :value 2
                                        :mounted-elements []}])))
