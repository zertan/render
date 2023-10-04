(ns hello-world.core
  (:require ;["@orbisclub/orbis-sdk" :refer [Orbis]]
   [pyramid.core :as p]
   [hello-world.mr-clean :as r]
   [hello-world.ikota :as ik]
   ["flowbite" :as fb]
   ;[goog.object :as gobj]
   [goog.dom :as gdom]
   ))

;(def orbis (new Orbis))



(def init-state [{:person/id 0
                  :person/name "Jim Simmons"
                  :person/age 23}
                 {:root/person {:person/id 1
                               :person/name "Hilda Engelbrekt"
                               :person/age 36
                               :person/friends [{:person/id 3 :person/name "Crab Roibinson" :person/age 2 :person/friends [[:person/id 2]]}
                                                {:person/id 2 :person/name "Rab Crabinson" :person/age 5 :person/friends [[:person/id 0]]}]}}])
 
#_(def app (r/atom (p/db init-state)))

#_(def sub-person (r/atom (p/db [{:root/person [:person/id 8]}
                               {:person/id 8 :person/name "Daniel Hermansson" :person/age 34 :person/friends []}])))

(defn init []
  (js/console.log "init"))

;;;;;;;;;;;;

(defn ref-fn-factory [this id]
  (fn [r]
    (when [r]
      #_(gobj/set this (str id) r))))

(defprotocol Component
  (init-local-state [this props])
  (render [this props children]))

(defrecord DraggableArea [local-state]
  Component
  (init-local-state [this props]
    (reset! local-state {:ref-fn (ref-fn-factory this (:id props))
                         :on-mouse-move nil #_(fn [e] (println "dsa"))
                         :on-mouse-up nil #_(fn [e])
                         :item-dragging nil}))
  (render [this props children]
    (let [{:keys [id style class]} props
          {:keys [ref-fn on-mouse-move on-mouse-up]} @(r/cursor local-state [])]
      (let [stuff (merge {:ref ref-fn
                    :id id
                    :class class
                    :style (merge {} style)}
                   (if on-mouse-move {:on-mouse-move on-mouse-move})
                   (if on-mouse-up  {:on-mouse-up on-mouse-up}))]
        [:div stuff
         children]))))

(def ui-draggable-area (DraggableArea. (r/atom nil)))

(defn event->dom-coords
  "Translate a javascript evt to a clj [x y] within the given dom element."
  [evt dom-ele & {:keys [bounding-rect]}]
  (let [cx (.-clientX evt)
        cy (.-clientY evt)
        ;;parentOffsetX (-> evt .-target .-offsetLeft)
        ;;parentOffsetY (-> evt .-target .-offsetTop)
        BB (if bounding-rect (.getBoundingClientRect dom-ele))
        x  (- (+ (- cx (if bounding-rect (.-left BB) 0)) (.-scrollLeft dom-ele)) 0)
        y  (+ (- cy (if bounding-rect (.-top BB) 0) (.-scrollTop dom-ele)) 0)]
    [x y]))

(defn vector-minus [a b]
  [(- (nth a 0) (nth b 0))
   (- (nth a 1) (nth b 1))])

(defn vector-plus [a b]
  [(+ (nth a 0) (nth b 0))
   (+ (nth a 1) (nth b 1))])

(defn clip-to-bounding-rect [pos bounding-rect]
  (let [x (first pos)
        y (second pos)
        left (nth bounding-rect 0)
                                        ;right (nth bounding-rect 1)
        top (nth bounding-rect 1)
                                        ;bottom (nth bounding-rect 4)
        ]
    (if bounding-rect
      [(if (< x left) left x) (if (> y top) top y)]
      pos)))

(defn on-mouse-up-factory [this props]
  (fn [e]
    (let [plane ui-draggable-area]
      (println "u")
      (swap! (:local-state this) merge {:dragging? false
                                        :end-pos (-> this :local-state deref :pos)})
      (swap! (:local-state plane) merge {:on-mouse-move nil
                                         :item-dragging nil
                                         :on-mouse-up nil}))))

(defn on-mouse-down-factory [this props]
  (let [{:keys [id plane container-id draggable? bounding-rect keep-position?]} props
        plane ui-draggable-area
        plane-obj (js/document.getElementById container-id)
        this-obj (js/document.getElementById id)
        move-fn-factory (fn [displacement this props]
                          (fn [evt]
                            (let [plane ui-draggable-area
                                  plane-obj (js/document.getElementById container-id)
                                  end-pos (if keep-position? (-> this :local-state deref :end-pos))
                                  p (clip-to-bounding-rect (vector-minus (event->dom-coords evt plane-obj)
                                                                         displacement)
                                                           bounding-rect)
                                  p (if end-pos (vector-plus p end-pos) p)]
                              (swap! (:local-state plane) merge  {:was-dragged? true})
                              (swap! (:local-state this) merge {:pos p}))))]
    (fn [e]
      (let [plane ui-draggable-area
            plane-obj (js/document.getElementById container-id)
                                        ;item-dragging-chan (-> plane :local-state deref :item-dragging)
            this-obj (js/document.getElementById id)
            displacement (event->dom-coords e this-obj)
            end-pos (if keep-position? (-> this :local-state deref :end-pos))]
        #_(reset! item-dragging-chan {:id (:id props)
                                      :height (.-clientHeight this-obj)
                                      :width (.-clientWidth this-obj)})
        (swap! (:local-state this) merge {:dragging? true
                                     :pos (if end-pos end-pos (clip-to-bounding-rect (vector-minus (event->dom-coords e plane-obj)
                                                                                                   displacement)
                                                                                     bounding-rect))
                                     :height (.-clientHeight this-obj)
                                     :width (.-clientWidth this-obj)})
        (swap! (:local-state plane) merge {:on-mouse-move (move-fn-factory displacement this props)
                                           :was-dragged? false
                                           :item-dragging {:id (:id props)
                                                           :height (.-clientHeight this-obj)
                                                           :width (.-clientWidth this-obj)}
                                           :on-mouse-up (on-mouse-up-factory this props)})))))


(defrecord Draggable [local-state]
  Component
  (init-local-state [this props]
    (reset! local-state {:pos [0 0]
                         :dragging? false
                         :on-mouse-down (on-mouse-down-factory this props)}))
  (render [this props children]
    (let [{:keys [id class draggable? style keep-position? on-mouse-up]} props
          {:keys [ref-fn on-mouse-down dragging? pos height width drag-handle]} @(r/cursor local-state [])]
      [:div (merge {:id id
                    :style (merge {}
                                  style
                                  (if dragging?
                                    {:height height
                                     :width width
                                     :transform (str "translate(" (first pos)  "px," (second pos) "px)")}
                                    (if keep-position?
                                      {:transform (str "translate(" (first pos)  "px," (second pos) "px)")})))
                    :class (str (if dragging? "absolute z-50 w-fit h-fit ") class)
                    :ref ref-fn}
                   (if drag-handle {} {:on-mouse-down on-mouse-down}))
       children])))

(def ui-draggable (Draggable. (r/atom nil)))

;;;;;;;;;;;;

(comment
  @app
  @(r/cursor app [:person/id 1])
  (swap! app assoc-in [:person/id 0 :person/age] 28))

(defn ui-person [{:person/keys [id name age friends]}]
  [:div {:class "flex flex-col items-center space-x-4"}
   [:div {:class "inline-flex items-center text-base font-semibold text-gray-900 dark:text-white pl-2 border-l-2"}
    [:div {:class "mr-6"}
     [:p {:class "text-sm font-medium text-gray-900 truncate dark:text-white"} "Name: " name]
     [:p {:class "text-sm text-gray-500 truncate dark:text-gray-400"} "Age: " age]]
    [:button {:class "text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800"
              :on-click #(swap! app update-in [:person/id id :person/age] inc)} "Increase Age"]]
   (when (> (count friends) 0)
     [:div {}
      [:span {:class "font-bold"} "Friends: "]
      [:ul {:class ""}
       (for [f friends]
         [:li {:class "mb-2"}
          [ui-person @(r/cursor app f)]])]])])

(defn draggable-component []
  (let [state (r/atom {:move-fn nil
                       :pos [50 50]})]
    (fn []
      [:div {}
       [:div {:class "bg-black"
              :style {:position "absolute"
                      :transform (let [pos (:pos @state)]
                                   (str "translate(" (first pos) "px," (second pos) "px)"))
                      :width "50px"
                      :height "50px"}
              :on-mouse-down #(swap! state assoc :move-fn (fn [e]
                                                            (let [n-x (aget e "clientX")
                                                                  n-y (aget e "clientY")]
                                                              (swap! state assoc :pos [n-x n-y]))))
              }]
       [:div (merge {:style {:position "absolute"
                             :background-color "blue"
                             :width "500px"
                             :height "500px"}
                     :on-mouse-up #(swap! state assoc :move-fn nil)}
                    (if (:move-fn @state) {:on-mouse-move #((:move-fn @state) %)}))]])))

(defn ui-root [app person]
  [:div
   [:div "This should not be updated on clicks."
    [ui-person @(r/cursor app person)]]
   #_[:div {:id "person"}
    #_[ui-person sub-person @(r/cursor sub-person [:person/id 8])]]
   #_[:div
    [ui-person app @(r/cursor app person)]
    [:button {:class "text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-blue-600 dark:hover:bg-blue-700 focus:outline-none dark:focus:ring-blue-800"
              :on-click #(swap! sub-person update-in [:person/id 8 :person/age] inc)} "Increase D's Age"]
    ]]

  #_[:div {}
   (render ui-draggable-area {:id "draggablearea"
                              :class "w-screen h-screen dark:bg-black dark:text-white flex w-screen h-screen justify-center align-center"}
           [:div])
   (render ui-draggable {:id "draggable"
                         :keep-position? true
                         :container-id "draggablearea"}

           #_[:div {:class "bg-blue-700"
                    :style {:width "400px"
                            :height "100px"}}])])


#_[ui-person app @(r/cursor app person)]

;; (def person (r/cursor app [:person/id 1]))
(defn run []
  #_(init-local-state ui-draggable-area {:id "draggablearea"
                                       })
  #_(init-local-state ui-draggable {:id "draggable"
                                    :container-id "draggablearea"})
  
  #_(r/render [ui-person @(r/cursor app [:person/id 1])] #_[ui-root app @(r/cursor app [:root/person])] (js/document.getElementById "app"))
  
  #_(r/render [ui-person sub-person @(r/cursor sub-person [:person/id 8])] (js/document.getElementById "app"))
  
  #_(init-local-state ui-draggable {:id "draggable"
                                    :container-id "draggablearea"}))
#_(r/render [draggable-component] (js/document.getElementById "app"))
                                        ;(r/render [ui-friends @(r/cursor app [:person/id 1 :person/friends])] (js/document.getElementById "app"))



(comment
  (r/render [:div {:id 1}
             "hi 1"
             [:div {:id 2} "hi " 3]] (js/document.getElementById "app"))

  (-> js/document
      (.getElementById "app")
      (.. (appendChild (js/document.createTextNode "h1"))))

  (fn create-element [d]
    )

  
  
  (def things
    (render-and-meta-things
     (js/document.getElementById "app")
     [:div {}
      [:div {}
       "hej"]
      [:div {} "hej igen"]]))

  (p/add (p/db [{:id 1
                 :e "some-e-1"
                 :children [{:id 2
                             :e "some-e-2"
                             :children []}
                            {:id 3
                             :e "some-e-3"
                             :children []}]}]) {:id 1 :e "some-e-4" :children [[:id 2] {:id 4}]})
  
  (ik/hiccup->dom [:div {}
                   [:div {} "hej"]
                   [:div {} "hej igen"]]
                  )
      
  (let [e (get-in @vdom [:id (second (keys (:id @vdom))) :element])]
    (swap! vdom p/add 
           (render-and-meta-things
            e
            [:div {}
             [:div {} "aba"]
             [:app-cursor [:id 1]]])))

  (js/setTimeout
   (do
     (swap! app update-in [:id 1 :value] inc)
     
     (let [v (get-in @app [:id 1 :value])
           mounted-ids (get-in @app [:id 1 :mounted-elements])
                                        ;new-nodes (take (count (get-in @app [:id 1 :mounted-elements])) (repeat (js/document.createTextNode 1)))
           ]
       (for [id mounted-ids]
         (let [new-node (js/document.createTextNode v)
               node (get-in @vdom (conj id :element))]
           (println node)
           (gdom/replaceNode new-node node)
           (swap! vdom assoc-in (conj id :element) new-node))
         ))) 1000)

  (get-in @vdom (first (get-in @app [:id 1 :mounted-elements])))
  
  )
