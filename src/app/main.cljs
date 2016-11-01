(ns app.main
  "Entry point for application."
  (:require
   [app.components.root :as root]
   [color.messenger :as color]
   [common.components.container :as container]
   [common.config :refer-macros [when-production]]
   [common.messenger :refer [create-messenger dispatch]]
   [common.reload :as reload]
   [datascript.core :refer [create-conn]]
   [rum.core :as rum]))

(def schema
  "DataScript DB schema for application DB."
  nil)

;; DataScript instance
(defonce connection (create-conn schema))

;; Messenger instance
(defonce messenger (create-messenger))

(def container-style
  "Style attributes applied to the CLJS application container."
  {:position "fixed"
   :top 0
   :bottom 0
   :left 0
   :right 0
   :overflow "auto"
   :display "flex"
   :backgroundColor "white"})

(defn- render
  "Render the application."
  []
  (let [container (js/document.getElementById "container")
        style (.-style container)
        props {:connection connection
               :dispatch (partial dispatch messenger)
               :component root/component}]
    (set! (.-cssText container) "")
    (doseq [[key val] container-style]
      (aset style (clj->js key) (clj->js val)))
    (rum/mount (container/component props) container)))

(defn- -main
  "Application entry point."
  []
  ;; Setup development tools
  (when-production false
    (enable-console-print!)
    (reload/add-handler render))

  ;; Register handlers to messenger
  (color/register messenger connection)

  ;; Start application
  (dispatch messenger :initialize)
  (js/setTimeout render))
