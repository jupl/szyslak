(ns org.project.color.db
  "DataScript functionality relative to colors."
  (:require
   [datascript.core :refer [q transact!]]))

;; --------- Queries

(def query-color
  "DataScript query to get selected color."
  (partial q '[:find ?color .
               :where
               [0 ::selected ?id]
               [?id ::value ?color]]))

(def query-color-ids
  "DataScript query to get all IDs for colors."
  (partial q '[:find [?id ...]
               :where [?id ::value]]))

(def query-previous-color-ids
  "DataScript query to get all IDs that precede selected color."
  (partial q '[:find [?id ...]
               :where
               [0 ::selected ?selected]
               [?id ::value]
               [(< ?id ?selected)]]))

(def query-next-color-ids
  "DataScript query to get all IDs that follow selected color."
  (partial q '[:find [?id ...]
               :where
               [0 ::selected ?selected]
               [?id ::value]
               [(> ?id ?selected)]]))

;; --------- Transactions

(defn previous-color!
  "Cycle to the previous color from selected color."
  [connection]
  (let [id (or (-> @connection query-previous-color-ids last)
               (-> @connection query-color-ids last))]
    (transact! connection [{:db/id 0 ::selected id}] ::tx-previous)))

(defn next-color!
  "Cycle to the next color from selected color."
  [connection]
  (let [id (or (-> @connection query-next-color-ids first)
               (-> @connection query-color-ids first))]
    (transact! connection [{:db/id 0 ::selected id}] ::tx-next)))

(defn initialize!
  "Set up colors and selected color."
  [connection]
  (let [colors ["#39cccc" "#2ecc40" "#ffdc00" "#ff851b"]
        transactions (map #(hash-map ::value %) colors)]
    (transact! connection transactions ::tx-initialize))
  (next-color! connection))
