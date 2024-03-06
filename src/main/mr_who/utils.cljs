(ns mr-who.utils
  (:require [clojure.string :as string]))

#_(defn keyword->str [kw]
  (if (keyword? kw)
    (name kw)
    kw))

(defn primitive? [hiccup]
  (or (string? hiccup)
      (number? hiccup)
      (boolean? hiccup)
      (nil? hiccup)))

(defn comp-ident [comp-data]
  (println comp-data)
  (let [k (keys comp-data)]
    (filterv #(= "id" (second (string/split % #"/"))) k)))

#_(defn to-map-path [path value]
  (loop [p path]
    (if (empty? (rest p))
      {p value}
      (let [k (first p)
            r (rest p)
            a (recur r)]
        {k a}))))


(defn db-value-at [m path]
  (if (list? path)
    (let [value (get-in m path)]
      (cond
        (map? value) (let [k (keys value)
                           v (vals value)]
                       (zipmap k (mapv #(db-value-at m %) v)))
        (list? value) (if (ident? value)
                        (db-value-at m value)
                        (mapv #(db-value-at m %) value))
        :else value))
    path))
