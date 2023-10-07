(ns mr-who.normalize)

(defn assign-by [k]
  (fn [data item]
    (get data (get item k))))

(defn db [k data]
  #_(js/console.log "d: " (js-iterable? data)#_(satisfies? IIterable data))
  (reduce (assign-by k) data)
  #_(.reduce data (assign-by k) {}))
