(ns color.messenger
  "Message handler for color specifics."
  (:require
   [cljs.core.async :refer [<! alts! timeout]]
   [color.db :as db]
   [common.messenger :refer [subscribe]])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]))

(defn register-previous
  "Hook up to messenger to handle color change to previous."
  [messenger connection]
  (let [[handler] (subscribe messenger ::previous)]
    (go-loop []
      (<! handler)
      (db/previous-color connection)
      (recur))))

(defn register-next
  "Hook up to messenger to handle color change to next."
  [messenger connection]
  (let [[handler] (subscribe messenger ::next)]
    (go-loop []
      (<! handler)
      (db/next-color connection)
      (recur))))

(defn register-auto
  "Hook up to messenger to auto change color until manual change."
  [messenger connection]
  (let [[previous unsubscribe-previous] (subscribe messenger ::previous)
        [next unsubscribe-next] (subscribe messenger ::next)]
    (go-loop []
      (if (nil? (-> [previous next (timeout 5000)] alts! (get 0)))
        (do
          (db/next-color connection)
          (recur))
        (do
          (unsubscribe-previous)
          (unsubscribe-next))))))

(defn register
  "Hook up to messenger for color events."
  [messenger connection]
  (let [[handler unsubscribe] (subscribe messenger :initialize)]
    (db/initialize connection)
    (go
      (<! handler)
      (doseq [func [register-previous register-next register-auto]]
        (func messenger connection))
      (unsubscribe))))
