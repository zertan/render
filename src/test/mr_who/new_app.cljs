(ns test.mr-who.new-app)


(defn root-comp [{:keys [router header wizard-modal] :or {router ((first (rc/router-comp {})))
                                                              header ((first (h/header-comp {})))
                                                              wizard-modal ((first (wm/modal-comp {})))}}
                 & children]
  (list (fn [] {:router router
                :header header
                :wizard-modal wizard-modal})
        (fn [] (dom/div {:id :root
                         :class "bg-black w-screen h-screen text-white dark"}
                 ((second (h/header-comp header)))
                 ((second (rc/router-comp router
                                          children)))
                 ((second (wm/modal-comp wizard-modal)))))))
