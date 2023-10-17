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
        :else (if (= v (or "true" nil "null")) (.. element (setAttribute k "")) (.. element (setAttribute k v)))))))

#_(defn div [& rest]
    (into [:div] rest))

(defn element-helper [parent c]
 (let [f (first c)
       attr (second c)
       r (rest (rest c))] (f parent attr r))) 

(defn re
  [tag attr-map & children]
  (let [node (js/document.createElement tag)]
    (attr-helper node attr-map)
    (if (> (count children) 1)
      (mapv #(append-child node %) children)
      (if (u/primitive? (first children))
        (if-not (u/undefined? (first children))
          (append-child node (js/document.createTextNode (first children))))
        (append-child node (first children))))
    node))

(defn div [attr-map children] (re :div attr-map children))
(defn img [attr-map children] (re :img attr-map children))

(defn button [& rest]
  (into [:button] rest))

(defn span [& rest]
  (into [:span] rest))

(defn a [& rest]
  (into [:a] rest))

#_(defn a [attr-map & rest]
  ())

(defn nav [& rest]
  (into [:nav] rest))

(defn header [& rest]
  (into [:header] rest))

(defn svg [& rest]
  (into [:svg] rest))

(defn path [& rest]
  (into [:path] rest))

(defn ul [& rest]
  (into [:ul] rest))

(defn li [& rest]
  (into [:li] rest))

#_(defn img [& rest]
  (into [:img] rest))

(defn label [& rest]
  (into [:label] rest))

(defn input [& rest]
  (into [:input] rest))

(defn form [& rest]
  (into [:form] rest))

#_(map m/def-dom-fn [:div :button :li :span])
