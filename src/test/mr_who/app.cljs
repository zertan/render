(ns mr-who.app
  (:require [mr-who.render :as r]
            #_[mr-who.resolve :as resolve]
            [pyramid.core :as p]
            [goog.dom :as gdom]
            [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.operation :as pco]))

#_(def indexes
  (pci/register [temperature-from-city]))

#_(pco/defresolver  [{:keys [city]}]
  {:temperature (get temperatures city)})

(defonce vdom (atom nil))

(defonce app (atom nil))

(defn click-factory [app vdom id]
  (fn [e]
    (swap! app update-in [:counter/id id :value] inc)
    
    (let [v (get-in @app [:counter/id id :value])
          mounted-ids (filterv #(not (nil? %)) (mapv #(if (= (first %) :value) (second %)) (get-in @app [:counter/id id :mr-who/mounted-elements])))]
      (doall
       (for [id mounted-ids]
         (let [new-node (js/document.createTextNode v)
               node (get-in @vdom (conj id :element))]
           (gdom/replaceNode new-node node)
           (swap! vdom assoc-in (conj id :element) new-node)))))))

(defn init []

  (println "init")

  (try (gdom/removeChildren (js/document.getElementById "app"))
       (catch js/Error e))

  (reset! app (p/db [{:root {:counter-list/id 1
                             :counters [{:counter/id 1
                                         :value 1
                                         :name "a"
                                         :mr-who/mounted-elements []}
                                        {:counter/id 2
                                         :value 2
                                         :name "b"
                                         :mr-who/mounted-elements []}]}}]))
  
  (def query [{:counters [:id :value]}])
  
  (defn counter-comp
    #_([app vdom {:keys [id]} & children] (into (counter-comp app vdom id) children))
    ([app vdom {:counter/keys [id]}]
     [:div
      [:button {:on-click (click-factory app vdom id)} "click"]
      [:div {} "Counter " [:app-cursor [:counter/id id :name]] ": " [:app-cursor [:counter/id id :value]]]]))
  
  (defn counter-list-comp
    #_([ident & children] (into (counter-list-comp) children))
    ([app vdom {:counter-list/keys [id]}]
     [:div {}
      #_(for [c [:app-cursor [:counter-list/id id :counter-list/counters]]]
        (counter-comp app vdom {:counter/id c}))]))
  
  (defn root-comp [app vdom ident]
    [:div {}
     #_(counter-list-comp app vdom {:counter-list/id 1})
     (counter-comp app vdom {:counter/id 1})])

  (reset! vdom (p/db [(r/render-and-meta-things (js/document.getElementById "app")
                                                (root-comp app vdom [:root])
                                                {:app app})])))



(comment
  
  (let [to-node (get-in @vdom [:id (second (keys (:id @vdom))) :element])]
    (r/add-new-elements vdom app to-node (my-cool-comp
                                          (my-cool-comp))))
  
  
  
  (get-in @vdom (first (get-in @app [:id 1 :mounted-elements])))

  (get-in @app [:id 1])
  )
