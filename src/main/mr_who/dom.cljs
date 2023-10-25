(ns mr-who.dom
  (:require [clojure.string :as string]
            ["./utils.mjs" :as u])
  #_(:require [mr-who.macros]))

(defn replace-node [old n]
  (let [parent old.parentNode]
    (.. parent (replaceChild n old))))

(defn append-child [parent child]
  (.. parent (appendChild child)))

(defn remove-node [node]
  (let [parent (.. old parentNode)
        rc (.. parent removeChild)]
    (rc node)))

(defn style-map->css-str [m]
  (->> m
       (map (fn [[k v]]
              (let [css-key k
                    css-val (cond
                              (u/number? v) (str v "px")
                              :else v)]
                (str css-key ": " css-val))))
       (string/join "; ")))

(defn add-css-to-element [element css-classes]
  (let [css-classes (remove #(or (u/nil? %) (string/blank? %)) css-classes)]
    (when-not (empty? css-classes)
      (doseq [c css-classes]
        (.. element -classList (add c))))))

(defn attr-helper [element attr-map]
  (when-not (empty? attr-map)
    (doseq [[k v] (js/Object.entries attr-map)]
      (cond
        (= k :style) (.. element (setAttribute "style" (style-map->css-str v)))
        (= k :class) (let [css-classes (if (u/string? v)
                                         (string/split v #"\s+"))]
                       (add-css-to-element element css-classes))
        (u/re-find #"on-\w+-*\w+" k) (let [event (u/replace k "on-" "")
                                           event (u/replace event "-" "")]
                                       (.. element (addEventListener event v)))
        (= k :viewBox) (.. element (setAttributeNS nil k v))
        (= k :d) (.. element (setAttributeNS nil k v))
        :else (if (= v (or nil "null")) (.. element (setAttribute k "")) (.. element (setAttribute k v)))))))

#_(defn div [& rest]
    (into [:div] rest))

(defn element-helper [parent c]
 (let [f (first c)
       attr (second c)
       r (rest (rest c))] (f parent attr r))) 

(defn append-helper [node child]
  (try (append-child node child)
       (catch js/Error e
         (if (u/function? child)
           (child node)
           (println "Error: could not append to following parent: " node ", child:" child)))))

(defn re [{:keys [f] :or {f #(js/document.createElement %)}} tag attr-map children]
  (let [node (f tag)
        children (if (and (u/list? children) (= (count children) 1)) (first children) children)]
    (attr-helper node attr-map)
    (println "node: " node)
    (println "c: " children)
    (cond
      (and (u/list? children)
           (> (count children) 1)) (let [children (.flat children)]
                                     (println  "c. "children)
                                     (mapv #(append-helper node (:node (first (u/vals %)))) children)
                                     (merge {:node node}
                                            (let [c (filterv #(not (= :nil (first (u/keys %))))
                                                             (.flat children))]
                                              (u/zipmap (mapv #(first (u/keys %)) c) (mapv #(first (u/vals %)) c)))))
      
      (and (u/primitive? children)
           (not (u/undefined? children))) (do
                                            (println "add")
                                            (println node)
                                            (println children)
                                            (append-helper node (js/document.createTextNode children))
                                            (merge {:node node} children))
      :else (if-not (u/undefined? children)
              (do
                (if-let [c (:node (first (u/vals children)))]
                  (append-helper node c))
                (if-not (= :nil (first (u/keys children)))
                  (merge {:node node} children)
                  ))
              {:node node}))))

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
(defn form [attr-map & children]
  (mv-id attr-map (re {} :form attr-map children)))
(defn p [attr-map & children]
  (mv-id attr-map (re {} :p attr-map children)))
(defn time [attr-map & children]
  (mv-id attr-map (re {} :time attr-map children)))
(defn ol [attr-map & children] (mv-id attr-map (re {} :ol attr-map children)))
(defn footer [attr-map & children] (mv-id attr-map (re {} :footer attr-map children)))
(defn main [attr-map & children] (mv-id attr-map (re {} :main attr-map) children))
#_(defn render [f] (re {} :main attr-map))

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
