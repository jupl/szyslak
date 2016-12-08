(ns projectname.electron.window
  "ClojureScript API for Electron browser windows."
  (:require
   [projectname.common.config :refer [base-url]]))

(defn init-window
  "Create a new window (if it doesn't exist already) and focus."
  [window url & args]
  (when (nil? @window)
    (let [browser-window (-> "electron" js/require .-BrowserWindow)]
      (reset! window (->> args
                          (apply hash-map)
                          clj->js
                          browser-window.))
      (.loadURL @window (str base-url url))
      (.on @window "closed" #(reset! window nil))))
  (.focus @window))
