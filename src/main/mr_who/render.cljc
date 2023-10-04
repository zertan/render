(ns mr-who.render
  (:require [pyramid.core :as p]
            [goog.dom :as gdom]))

(defn primitive? [hiccup]
  (or (string? hiccup)
      (number? hiccup)
      (keyword? hiccup)
      (boolean? hiccup)
      (inst? hiccup)
      (nil? hiccup)
      (char? hiccup)))

(defn create-vdom-element
  ([id e type attr] (create-vdom-element id e type attr []))
  ([id e type attr children] {:id id :element e :type type :attr attr :children children}))

(defn render-and-meta-things
  #_([node things] (render-and-meta-things node things (gdom/appendChild)))
  [node things & {:keys [fun app]}]
  (if (primitive? things) (let [e (.. node (appendChild (js/document.createTextNode things)))
                                id (random-uuid)]
                            ;; here we are using fun passed below as a pointer in app state
                            (if fun (fun [:id id]))
                            (create-vdom-element id e :text {}))
      (let [f (first things)
            m (second things)
            r (vec (rest (rest things)))]
        (cond
          (and (keyword? f)
               (map? m)) (let [e (.. node (appendChild (js/document.createElement (name f))))]
               (create-vdom-element (random-uuid) e f m (if r (render-and-meta-things e r {:app app}) [])))
          (= :app-cursor f) (let [cursor (get-in @app m)]
                              (render-and-meta-things node (:value cursor)
                                                      {:app app
                                                       :fun #(swap! app update-in (conj m :mounted-elements) conj %)}))
          (keyword? f) (let [e (.. node (appendChild (js/document.createElement (name f))))]
                         (let [r (rest things)]
                           (create-vdom-element (random-uuid) e f {} (if r (render-and-meta-things e r {:app app}) []))))
          (and (list? f) (empty? r)) (render-and-meta-things node f {:app app})
          :else (mapv #(render-and-meta-things node % {:app app}) things)))))

(defn add-new-elements [vdom app to-node hic]
  (swap! vdom p/add (render-and-meta-things to-node hic {:app app})))

(comment
  (create-vdom-element 1 2 3 4 nil))
