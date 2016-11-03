(ns projectname.app.components.root
  "Application root component structure."
  (:require
   [projectname.app.components.home :as home]
   [projectname.app.components.not-found :as not-found]
   [projectname.common.db :refer [query-route]]
   [projectname.common.components.container :as container]
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
  (template {:handler (-> comp
                          container/get-context
                          :connection
                          rum/react
                          query-route
                          first)}))
