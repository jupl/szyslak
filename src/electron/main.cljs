(ns electron.main
  "Entry point for the main process."
  (:require
   [common.config :refer-macros [when-production]]
   [electron.menu :refer [init-menu]]
   [electron.window :refer [init-window]]))

(defn- -main
  "Configure and bootstrap Electron application."
  []
  (set! *main-cli-fn* identity) ;; Required for a Node application
  (when-production false
    (enable-console-print!))
  (let [app (-> "electron" js/require .-app)
        exit-app #(.quit app)
        main-window (atom nil)
        osx (= js/process.platform "darwin")
        open-main-window #(init-window main-window "app.html")]
    (.on app "ready" init-menu)
    (.on app "ready" open-main-window)
    (.on app "activate" open-main-window)
    (when-not osx
      (.on app "window-all-closed" exit-app))))
