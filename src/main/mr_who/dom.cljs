(ns mr-who.dom
  (:require [clojure.string :as string]
            ["./utils.mjs" :as u])
  #_(:require [mr-who.macros]))

(defn replace-node [n old]
  (let [parent old.parentNode]
    (.replaceChild parent n old)))

(defn append-child [parent child]
  (.. parent (appendChild child))
  #_(.parent.appendChild child))

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
        (u/re-find #"on-\w+-*\w+" k) (let [event (replace k "on-" "")
                                           event (replace event "-" "")]
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
  (let [node (f tag)]
    (attr-helper node attr-map)
    (if (> (count children) 1)
      (let [a (filterv #(u/primitive? %) children)
            b (filterv #(not (u/primitive? %)) children)
            c (mapv #(js/document.createTextNode %) a)]
        (mapv #(append-helper node %) (.flat (into b c))))
      (if (u/primitive? (first children))
        (if-not (u/undefined? (first children))
          (append-helper node (js/document.createTextNode (first children))))
        (if (or (> (count (first children)) 1) (u/list? (first children)))
          (mapv #(append-helper node %) (.flat (first children)))
          (append-helper node (first children)))))
    node))

(defn div [attr-map & children] (re {:f #(js/document.createElement %)} :div attr-map children))
(defn img [attr-map & children] (re {} :img attr-map children))
(defn button [attr-map & children] (re {} :button attr-map children))
(defn span [attr-map & children] (re {} :span attr-map children))
(defn a [attr-map & children] (re {} :a attr-map children))
(defn nav [attr-map & children] (re {} :nav attr-map children))
(defn header [attr-map & children] (re {} :header attr-map children))
(defn svg [attr-map & children] (if-let [xmlns (:xmlns attr-map)]
                                  (re {:f #(js/document.createElementNS xmlns %)} :svg attr-map children)
                                  (re {:f #(js/document.createElement %)} :svg attr-map children)))
(defn path [attr-map & children] (re {:f #(js/document.createElementNS "http://www.w3.org/2000/svg" %)} :path attr-map children))
(defn ul [attr-map & children] (re {} :ul attr-map children))
(defn li [attr-map & children] (re {} :li attr-map children))
(defn label [attr-map & children] (re {} :label attr-map children))
(defn input [attr-map & children] (re {} :input attr-map children))
(defn form [attr-map & children] (re {} :form attr-map children))
(defn p [attr-map & children] (re {} :p attr-map children))
(defn time [attr-map & children] (re {} :time attr-map children))
(defn ol [attr-map & children] (re {} :ol attr-map children))
(defn footer [attr-map & children] (re {} :footer attr-map children))
(defn main [attr-map & children] (re {} :main attr-map) children)
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
