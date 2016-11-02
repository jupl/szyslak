(ns org.project.app.components.root
  "Application root component structure."
  (:require
   [datascript.core :refer [q]]
   [org.project.app.components.home :as home]
   [org.project.app.components.not-found :as not-found]
   [org.project.common.db :refer [route-query]]
   [org.project.common.components.container :as container]
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
  (let [db (-> comp container/get-context :connection rum/react)
        [handler] (q route-query db)]
    (template {:handler handler})))
