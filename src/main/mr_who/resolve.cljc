(ns mr-who.resolve
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.interface.eql :as p.eql]))

#_(pco/defresolver temperature-from-city [{:keys [city]}]
  {:temperature (get temperatures city)})
