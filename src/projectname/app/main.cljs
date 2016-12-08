(ns projectname.app.main
  "Entry point for application."
  (:require
   [datascript.core :refer [create-conn]]
   [projectname.app.components.root :as root]
   [projectname.common.components.container :as container]
   [projectname.common.config :refer-macros [production?]]
   [projectname.common.messenger :refer [create-messenger dispatch]]
   [projectname.common.reload :as reload]
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
    (.removeAttribute container "style")
    (doseq [[key val] container-style]
      (aset style (clj->js key) (clj->js val)))
    (rum/mount (container/component props) container)))

(defn- -main
  "Application entry point."
  []
  ;; Setup development tools
  (when-not (production?)
    (enable-console-print!)
    (reload/add-handler render))

  ;; Register handlers to messenger

  ;; Start application
  (dispatch messenger :initialize)
  (js/setTimeout render))
