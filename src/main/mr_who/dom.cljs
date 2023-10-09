(ns mr-who.dom
  #_(:require [mr-who.macros]))

(defn div [& rest]
  (into [:div] rest))

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

(defn img [& rest]
  (into [:img] rest))

(defn label [& rest]
  (into [:label] rest))

(defn input [& rest]
  (into [:input] rest))

(defn form [& rest]
  (into [:form] rest))



#_(map m/def-dom-fn [:div :button :li :span])

(defn replace-node [n old]
  (let [parent old.parentNode]
    (.replaceChild parent n old)))

(defn append-child [parent child]
  (.parent.appendChild child))

(defn remove-node [node]
  (let [parent (.. old parentNode)
        rc (.. parent removeChild)]
    (rc node)))
