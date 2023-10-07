(ns mr-who.dom)

(defn div [& rest]
  (into [:div] rest))

(defn button [& rest]
  (into [:button] rest))

(defn span [& rest]
  (into [:span] rest))

(defn a [& rest]
  (into [:a] rest))

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


#_(for [tag [:div :button :li :span]]
    (eval
     '(defn ~(symbol tag) [& rest]
        (into [tag] rest))))

(defn replace-node [n old]
  (let [parent old.parentNode]
    (.replaceChild parent n old)))

(defn append-child [parent child]
  (.parent.appendChild child))

(defn remove-node [node]
  (let [parent (.. old parentNode)
        rc (.. parent removeChild)]
    (rc node)))
