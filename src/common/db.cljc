(ns common.db
  "DataScript functionality relative to project."
  (:require
   [datascript.core :refer [transact!]]))

(def route-query
  "DataScript query to get handler."
  '[:find [?handler ?route-params]
    :where
    [0 ::handler ?handler]
    [0 ::route-params ?route-params]])

(defn update-route!
  "Update routing information."
  [connection {:keys [handler route-params]}]
  (transact! connection
             [{:db/id 0 ::handler handler}
              {:db/id 0 ::route-params (or route-params {})}]))
