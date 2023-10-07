(ns mr-who.dom)

(defn replace-node [n old]
  (let [parent old.parentNode]
    (.replaceChild parent n old)))

(defn append-child [parent child]
  (.parent.appendChild child))

(defn remove-node [node]
  (let [parent (.. old parentNode)
        rc (.. parent removeChild)]
    (rc node)))
