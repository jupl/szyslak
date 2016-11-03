(ns projectname.app.main
  "Entry point for application."
  (:require
   [bidi.bidi :refer [match-route]]
   [cljs.reader :refer [read-string]]
   [datascript.core :refer [create-conn reset-conn! transact!]]
   [projectname.app.components.root :as root]
   [projectname.app.config :refer [routes]]
   [projectname.common.components.container :as container]
   [projectname.common.config :refer-macros [when-production]]
   [projectname.common.db :refer [update-in-server! update-route!]]
   [projectname.common.messenger :refer [create-messenger dispatch]]
   [projectname.common.reload :as reload]
   [pushy.core :as pushy]
   [rum.core :as rum]))

;; DataScript instance
(defonce connection (create-conn))

;; Messenger instance
(defonce messenger (create-messenger))

(defn- render
  "Render the application."
  []
  (let [container (js/document.getElementById "container")
        props {:connection connection
               :dispatch (partial dispatch messenger)
               :component root/component}]
    (rum/mount (container/component props) container)))

(defn- -main
  "Application entry point."
  []
  ;; Initialize DB with data from server
  (reset-conn! connection (read-string js/__DB__))

  ;; Setup development tools
  (when-production false
    (enable-console-print!)
    (reload/add-handler render))

  ;; Set up routing
  (let [on-route-change (partial update-route! connection)
        matcher (partial match-route routes)
        history (pushy/pushy on-route-change matcher)]
    (pushy/start! history))

  ;; Register handlers to messenger

  ;; Start application
  (dispatch messenger :initialize)
  (js/setTimeout render)
  (js/setTimeout #(update-in-server! connection)))
