(ns projectname.app.main
  "Entry point for main server-side application."
  (:require
   [immutant.web :as web]
   [projectname.app.handler :refer [handler]]
   [ring.middleware.defaults :refer [api-defaults wrap-defaults]])
  (:gen-class))

(def defaults
  "Default server options that can be overridden."
  {:port 3000})

(defn- -main
  "Start the web server, leveraging command line arguments."
  [& {:as args}]
  (web/run (wrap-defaults handler api-defaults) (merge defaults args)))
