(ns app.components.root
  "Application root component structure."
  (:require
   [app.components.home :as home]
   [app.components.not-found :as not-found]
   [common.db :refer [route-query]]
   [common.components.container :as container]
   [datascript.core :refer [q]]
   [rum.core :as rum]))

;; --------- Template

(rum/defc template
  "Application root template."
  [{:keys [handler]}]
  (case handler
    :home (home/component)
    :not-found (not-found/component)))

;; --------- Component

(rum/defcc component
  "Application root component."
  < rum/reactive container/mixin
  [comp]
  ;; TODO Fix server side rendering issue
  (let [db (-> comp container/get-context :connection rum/react)
        [handler] #?(:cljs (q route-query db)
                     :clj [:home])]
    (template {:handler handler})))
