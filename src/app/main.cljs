(ns app.main
  "Entry point for application."
  (:require
   [app.components.root :as root]
   [common.components.container :as container]
   [common.config :refer-macros [when-production]]
   [common.messenger :refer [create-messenger dispatch]]
   [common.reload :as reload]
   [datascript.core :refer [conn-from-db]]
   [datascript.transit :refer [read-transit-str]]
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
               :dispatch (partial dispatch messenger)}]
    (rum/mount (container/component props (root/component)) container)))

(defn- -main
  "Application entry point."
  []
  ;; Setup development tools
  (when-production false
    (enable-console-print!)
    (reload/add-handler render))

  ;; Register handlers to messenger

  ;; Start application
  (dispatch messenger :initialize)
  (js/setTimeout render))
