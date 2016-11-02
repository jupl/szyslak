(ns org.project.common.db
  "DataScript functionality relative to project."
  (:require
   [org.project.common.config :as config]
   [datascript.core :refer [transact!]]))

(def in-server-query
  "DataScript query to check if in server environment."
  '[:find ?in-server .
    :where
    [0 ::in-server ?in-server]])

(def route-query
  "DataScript query to get handler."
  '[:find [?handler ?route-params]
    :where
    [0 ::handler ?handler]
    [0 ::route-params ?route-params]])

(defn update-in-server!
  "Update routing information."
  [connection]
  (transact! connection [{:db/id 0 ::in-server config/in-server}]))

(defn update-route!
  "Update routing information."
  [connection {:keys [handler route-params]}]
  (transact! connection
             [{:db/id 0 ::handler handler}
              {:db/id 0 ::route-params (or route-params {})}]))
