(ns mr-who.dom
  (:require [clojure.string :as string]
            [mr-who.utils :as u]))

(defn replace-node [old n]
  (let [parent old.parentNode]
    (.. parent (replaceChild n old))))

#_(defn replace-child [parent n]
  (let [first-child (first parent.children)]
    (n.replaceChild n old)))

(defn append-child [parent child]
  (.. parent (appendChild child)))

(defn remove-node [node]
  (let [parent (.. node parentNode)
        rc (.. parent removeChild)]
    (rc node)))

(defn style-map->css-str [m]
  (->> m
       (map (fn [[k v]]
              (let [css-key k
                    css-val (cond
                              (number? v) (str v "px")
                              :else v)]
                (str (name css-key) ": " css-val))))
       (string/join "; ")))

(defn add-css-to-element [element css-classes]
  (let [css-classes (remove #(or (nil? %) (string/blank? %)) css-classes)]
    (when-not (empty? css-classes)
      (doseq [c css-classes]
        (.. element -classList (add c))))))

(defn remove-css-from-element [element css-classes]
  (let [css-classes (remove #(or (nil? %) (string/blank? %)) css-classes)]
    (when-not (empty? css-classes)
      (doseq [c css-classes]
        (.. element -classList (remove c))))))

(defn attr-helper [element attr-map]
  (when-not (empty? attr-map)
    (doseq [[k v] attr-map]
      (cond
        (= k :id) (set! (.-id element) (name v))
        (= k :style) (.. element (setAttribute "style" (style-map->css-str v)))
        (= k :class) (let [css-classes (if (string? v)
                                         (string/split v #"\s+"))]
                       (add-css-to-element element css-classes))
        (or (string? k) (keyword? k)) (let [k (name k)] (if (re-find #"on-\w+-*\w+" k)
                                                          (let [event (string/replace k "on-" "")
                                                                event (string/replace event "-" "")]
                                                            (.. element (addEventListener event v)))))
        (= k :viewBox) (.. element (setAttributeNS nil k v))
        (= k :d) (.. element (setAttributeNS nil k v))
        :else (if (= v (or nil "null")) (.. element (setAttribute k "")) (.. element (setAttribute k v)))))))

#_(defn div [& rest]
    (into [:div] rest))

(defn element-helper [parent c]
 (let [f (first c)
       attr (second c)
       r (rest (rest c))] (f parent attr r))) 

(defn append-helper [node child & {:keys [action] :as opts :or {action append-child}}]
  (try (action node child)
       (catch js/Error e
         (if (fn? child)
           (child node)
           (println "Error: could not append to following parent: " node ", child:" child)))))

(defn re [{:keys [f] :or {f #(js/document.createElement %)}} tag attr-map children]
  (let [node (f (name tag))
        ;a (println "asds" children)
        ;b (println "a" (first children))

        children (if (and (= (type children) cljs.core/IndexedSeq) (= (count children) 1) (not (= children (list {:nil nil}))))
                   (first children)
                   children)]
    #_(println "after: " children " " (type children))
    (attr-helper node attr-map)
    #_(println "node: " node)
    #_(println "attr-map " attr-map)
    #_(println "c: " children)
    (cond
      (and (or (= (type children) cljs.core/IndexedSeq) (= (type children) cljs.core/LazySeq) (= (type children) cljs.core/PersistentArrayMap))
           (> (count children) 1)) (let [children (flatten children)]
                                     #_(println  "c. "children)
                                     (mapv #(append-helper node (:mr-who/node (first (vals %)))) children)
                                     (merge {:mr-who/node node}
                                            (let [c (filterv #(not (= :nil (first (keys %))))
                                                             (flatten children))]
                                              (zipmap (mapv #(first (keys %)) c) (mapv #(first (vals %)) c)))))
      
      (u/primitive? (first children)) (do
                                #_(println "add")
                                #_(println node)
                                #_(println children)

                                (append-helper node (js/document.createTextNode children))
                                (merge {:mr-who/node node} {:primitive children}))
      :else (if (and (not (undefined? children))  (not (= children (list {:nil nil}))))

              (do
                (if-let [c (:mr-who/node (first (vals children)))]
                  (append-helper node c))
                (if-not (= :nil (first (keys children)))
                  (merge {:mr-who/node node} children)
                  {:mr-who/node node}))))))

(defn re-helper [m]
  (let [k (first m)
        v (second m)]
    {k m}))

(defn mv-id [attr f]
  (if-let [id (:id attr)]
    (assoc {} id f)
    (assoc {} :nil f)))

(defn div [attr-map & children]
  (mv-id attr-map (re {} :div attr-map children)))
(defn img [attr-map & children]
  (mv-id attr-map (re {} :img attr-map children)))
(defn button [attr-map & children]
  (mv-id attr-map (re {} :button attr-map children)))
(defn span [attr-map & children]
  (mv-id attr-map (re {} :span attr-map children)))
(defn a [attr-map & children]
  (mv-id attr-map (re {} :a attr-map children)))
(defn nav [attr-map & children]
  (mv-id attr-map (re {} :nav attr-map children)))
(defn header [attr-map & children]
  (mv-id attr-map (re {} :header attr-map children)))
(defn svg [attr-map & children]
  (mv-id attr-map (re {:f (if-let [xmlns (:xmlns attr-map)]
                         #(js/document.createElementNS xmlns %)
                         #(js/document.createElement %))} :svg attr-map children)))
(defn path [attr-map & children]
  (mv-id attr-map (re {:f #(js/document.createElementNS "http://www.w3.org/2000/svg" %)} :path attr-map children)))
(defn g [attr-map & children]
  (mv-id attr-map (re {} :g attr-map children)))
(defn clipPath [attr-map & children]
  (mv-id attr-map (re {} :clipPath attr-map children)))
(defn defs [attr-map & children]
  (mv-id attr-map (re {} :defs attr-map children)))
(defn ul [attr-map & children]
  (mv-id attr-map (re {} :ul attr-map children)))
(defn text [attr-map & children]
  (mv-id attr-map (re {} :text attr-map children)))
(defn li [attr-map & children]
  (mv-id attr-map (re {} :li attr-map children)))
(defn label [attr-map & children]
  (mv-id attr-map (re {} :label attr-map children)))
(defn input [attr-map & children]
  (mv-id attr-map (re {} :input attr-map children)))
(defn select [attr-map & children]
  (mv-id attr-map (re {} :select attr-map children)))
(defn option [attr-map & children]
  (mv-id attr-map (re {} :option attr-map children)))
(defn form [attr-map & children]
  (mv-id attr-map (re {} :form attr-map children)))
(defn p [attr-map & children]
  (mv-id attr-map (re {} :p attr-map  children)))
(defn timea [attr-map & children]
  (mv-id attr-map (re {} :time attr-map children)))
(defn ol [attr-map & children] (mv-id attr-map (re {} :ol attr-map children)))
(defn h1 [attr-map & children] (mv-id attr-map (re {} :h1 attr-map children)))
(defn h2 [attr-map & children] (mv-id attr-map (re {} :h2 attr-map children)))
(defn h3 [attr-map & children] (mv-id attr-map (re {} :h3 attr-map children)))
(defn h4 [attr-map & children] (mv-id attr-map (re {} :h4 attr-map children)))
(defn h5 [attr-map & children] (mv-id attr-map (re {} :h5 attr-map children)))
(defn footer [attr-map & children] (mv-id attr-map (re {} :footer attr-map children)))
(defn main [attr-map & children] (mv-id attr-map (re {} :main attr-map children)))
(defn render [attr-map f] (mv-id {} (re {:f (fn [tag] (f))} :render {} nil)))

;; (defn button [& rest]) 

;; (defn span [& rest]
;;   (into [:span] rest))

;; (defn a [& rest]
;;   (into [:a] rest))

;; #_(defn a [attr-map & rest]
;;   ())

;; (defn nav [& rest]
;;   (into [:nav] rest))

;; (defn header [& rest]
;;   (into [:header] rest))

;; (defn svg [& rest]
;;   (into [:svg] rest))

;; (defn path [& rest]
;;   (into [:path] rest))

;; (defn ul [& rest]
;;   (into [:ul] rest))

;; (defn li [& rest]
;;   (into [:li] rest))

;; #_(defn img [& rest]
;;   (into [:img] rest))

;; (defn label [& rest]
;;   (into [:label] rest))

;; (defn input [& rest]
;;   (into [:input] rest))

;; (defn form [& rest]
;;   (into [:form] rest))

#_(map m/def-dom-fn [:div :button :li :span])
