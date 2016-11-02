(ns org.project.color.db
  "DataScript functionality relative to colors."
  (:require
   [datascript.core :refer [q transact!]]))

;; --------- Queries

(def color-query
  "DataScript query to get selected color."
  '[:find ?color .
    :where
    [0 ::selected ?id]
    [?id ::value ?color]])

(def color-ids-query
  "DataScript query to get all IDs for colors."
  '[:find [?id ...]
    :where [?id ::value]])

(def previous-color-ids-query
  "DataScript query to get all IDs that precede selected color."
  '[:find [?id ...]
    :where
    [0 ::selected ?selected]
    [?id ::value]
    [(< ?id ?selected)]])

(def next-color-ids-query
  "DataScript query to get all IDs that follow selected color."
  '[:find [?id ...]
    :where
    [0 ::selected ?selected]
    [?id ::value]
    [(> ?id ?selected)]])

;; --------- Transactions

(defn previous-color
  "Cycle to the previous color from selected color."
  [connection]
  (let [id (or (->> @connection (q previous-color-ids-query) last)
               (->> @connection (q color-ids-query) last))]
    (transact! connection [{:db/id 0 ::selected id}] ::tx-previous)))

(defn next-color
  "Cycle to the next color from selected color."
  [connection]
  (let [id (or (->> @connection (q next-color-ids-query) first)
               (->> @connection (q color-ids-query) first))]
    (transact! connection [{:db/id 0 ::selected id}] ::tx-next)))

(defn initialize
  "Set up colors and selected color."
  [connection]
  (let [colors ["#39cccc" "#2ecc40" "#ffdc00" "#ff851b"]
        transactions (map #(hash-map ::value %) colors)]
    (transact! connection transactions ::tx-initialize))
  (next-color connection))
