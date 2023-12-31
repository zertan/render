(ns mr-who.render
  (:require [pyramid.core :as p]
            [clojure.string :as string]
            #_[goog.dom :as gdom]))

(defn keyword->str [kw]
  (if (keyword? kw)
    (name kw)
    kw))

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
  ([id e type attr children] {:mr-who/id id :element e :type type :attr attr :children children}))

(defn extract-tag-id-css-classes [tag-maybe-id-css-classes]
  (let [as-str (keyword->str tag-maybe-id-css-classes)
        chunks (string/split as-str #"\.")
        tag-with-maybe-id (first chunks)
        [tag id] (string/split tag-with-maybe-id #"#")
        css-classes (rest chunks)]
    [tag id css-classes]))

(defn style-map->css-str [m]
  (->> m
       (map (fn [[k v]]
              (let [css-key (keyword->str k)
                    css-val (cond
                              (number? v) (str v "px")
                              :else (keyword->str v))]
                (str css-key ": " css-val))))
       (string/join "; ")))

#?(:cljs
   (defn add-css-to-element [element css-classes]
     (let [css-classes (remove #(or (nil? %) (string/blank? %)) css-classes)]
       (when-not (empty? css-classes)
         (doseq [c css-classes]
           (.. element -classList (add c)))))))

#?(:cljs
   (defn create-element [tag-maybe-id-css-classes attr-map]
     (let [[tag-name id css-classes] (extract-tag-id-css-classes tag-maybe-id-css-classes)
           element (js/document.createElement tag-name)]
       
       (when id
         (set! (.-id element) id))
       
       (add-css-to-element element css-classes)
       (when-not (empty? attr-map)
         (doseq [[k v] attr-map
                 :let [k-str (keyword->str k)]]
           (cond
             (= k :style) (.. element (setAttribute "style" (style-map->css-str v)))
             (= k :class) (let [css-classes (if (string? v)
                                              (string/split v #"\s+")
                                              (map name v))]
                            (add-css-to-element element css-classes))
             (re-find #"on-\w+-*\w+" k-str) (let [event (string/replace k-str "on-" "")
                                                  event (string/replace event "-" "")]
                                              (.. element (addEventListener event v)))
             :else (let [v-str (keyword->str v)]
                     (.. element (setAttribute k-str v-str))))))
       
       element)))

(defn render-and-meta-things
  #_([node things] (render-and-meta-things node things (gdom/appendChild)))
  [node things & {:keys [fun app]}]
  (if (primitive? things) (let [e (.. node (appendChild (js/document.createTextNode things)))
                                id (random-uuid)]
                            ;; here we are using fun passed below as a pointer in app state
                            (if fun (fun [:mr-who/id id]))
                            (create-vdom-element id e :text {}))
      (let [f (first things)
            m (second things)
            r (vec (rest (rest things)))]
        (cond
          (and (keyword? f)
               (map? m)) (let [e (.. node (appendChild (create-element f m)))]
               (create-vdom-element (random-uuid) e f m (if r (render-and-meta-things e r {:app app}) [])))
          (= :app-cursor f) (let [cursor (get-in @app m)
                                  ident [(first m) (second m)]
                                  lastv (last m)]
                              (render-and-meta-things node cursor
                                                      {:app app
                                                       :fun #(swap! app update-in (conj ident :mr-who/mounted-elements) conj [lastv %])}))
          (keyword? f) (let [e (.. node (appendChild (js/document.createElement (name f))))]
                         (let [r (rest things)]
                           (create-vdom-element (random-uuid) e f {} (if r (render-and-meta-things e r {:app app}) []))))
          (and (list? f) (empty? r)) (render-and-meta-things node f {:app app})
          :else (mapv #(render-and-meta-things node % {:app app}) things)))))

(defn add-new-elements [vdom app to-node hic]
  (swap! vdom p/add (render-and-meta-things to-node hic {:app app})))
