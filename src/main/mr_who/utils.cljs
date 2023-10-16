(ns mr-who.utils
  (:require [clojure.string :as string]))

(defn string? [thing]
  (= (js/typeof thing) "string"))

(defn nil? [thing]
  (= (js/typeof thing) "undefined"))

(defn boolean? [thing]
  (= (js/typeof thing) "boolean"))

(defn function? [thing]
  (= (js/typeof thing) "function"))

(defn random-uuid []
  (crypto.randomUUID))

(defn re-find [regex s]
  (s.match regex))

(defn replace [s match replacement]
  (s.replace match replacement))

#_(defn keyword->str [kw]
  (if (keyword? kw)
    (name kw)
    kw))

(defn map? [thing]
  (= (js/typeof thing) "object"))

(defn list? [thing]
  (js/Array.isArray thing))

(defn ident? [thing]
  (if (= list? thing)
    (let [f (first thing)
          s (second thing)]
      (and (string? f) (or (number? s) (string? s))))))

(defn number? [thing]
  (= (js/typeof thing) "number"))

(defn keyword? [f]
  (first (filterv #(= % true) (doall (map #(= f %) ["div" "p" "button" "span" "a" "nav" "svg" "path" "ul" "li" "img" "header" "form" "input" "label"]))))
  #_(= (first f) ":")
  #_(apply or (map #(= % f) ["div" "p" "button"])))

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

(defn keys [m]
  (js/Object.keys m))

(defn vals [m]
  (js/Object.values m))

(defn zipmap
  "Returns a map with the keys mapped to the corresponding vals."
  {:added "1.0"
   :static true}
  [keys vals]
    (loop [map {}
           ks (seq keys)
           vs (seq vals)]
      (if (and ks vs)
        (recur (assoc map (first ks) (first vs))
               (next ks)
               (next vs))
        map)))

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
