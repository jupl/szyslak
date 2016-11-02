(ns app.main
  "Entry point for application."
  (:require
   [app.components.root :as root]
   [app.config :refer [routes]]
   [bidi.bidi :refer [match-route]]
   [common.components.container :as container]
   [common.config :refer-macros [when-production]]
   [common.db :refer [update-route!]]
   [common.messenger :refer [create-messenger dispatch]]
   [common.reload :as reload]
   [datascript.core :refer [conn-from-db transact!]]
   [datascript.transit :refer [read-transit-str]]
   [pushy.core :as pushy]
   [rum.core :as rum]))

;; DataScript instance
(defonce connection (conn-from-db (read-transit-str js/__DB__)))

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
  (js/setTimeout render))
