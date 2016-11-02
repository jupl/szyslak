(ns org.project.common.devcards
  "Entry point for devcards, referencing all devcards."
  (:require
   ;; Require any devcards you want to show
   [devcards.core :refer-macros [start-devcard-ui!]]))

(defn- -main
  "Configure and bootstrap devcards."
  []
  (enable-console-print!)
  (start-devcard-ui!))
