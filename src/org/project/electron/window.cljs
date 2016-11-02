(ns org.project.electron.window
  "ClojureScript API for Electron browser windows."
  (:require
   [org.project.common.config :as config]))

(defn init-window
  "Create a new window (if it doesn't exist already) and focus."
  [window url & args]
  (when (nil? @window)
    (let [browser-window (-> "electron" js/require .-BrowserWindow)]
      (reset! window (->> args
                          (apply hash-map)
                          clj->js
                          browser-window.))
      (.loadURL @window (str config/base-url url))
      (.on @window "closed" #(reset! window nil))))
  (.focus @window))
