(ns mr-who.macros)

(defmacro defc [a]
  '(println 1 ~a))

#_(defn counter-list-comp
  #_([ident & children] (into (counter-list-comp) children))
  ([app {:counter-list/keys [id]}]
   [:div {}
    (for [c [:app-cursor [:counter-list/id id :counters]]]
      #_(counter-comp app {:counter/id c}))]))

