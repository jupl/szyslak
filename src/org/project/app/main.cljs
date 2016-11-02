(ns org.project.app.main
  "Entry point for application."
  (:require
   [bidi.bidi :refer [match-route]]
   [datascript.core :refer [conn-from-db transact!]]
   [datascript.transit :refer [read-transit-str]]
   [org.project.app.components.root :as root]
   [org.project.app.config :refer [routes]]
   [org.project.common.components.container :as container]
   [org.project.common.config :refer-macros [when-production]]
   [org.project.common.db :refer [update-in-server! update-route!]]
   [org.project.common.messenger :refer [create-messenger dispatch]]
   [org.project.common.reload :as reload]
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
  (js/setTimeout render)
  (js/setTimeout #(update-in-server! connection)))
