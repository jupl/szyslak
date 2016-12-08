(ns projectname.electron.main
  "Entry point for the main process."
  (:require
   [projectname.common.config :refer-macros [when-production]]
   [projectname.electron.menu :refer [init-menu]]
   [projectname.electron.window :refer [init-window]]))

(defn- -main
  "Configure and bootstrap Electron application."
  []
  (set! *main-cli-fn* identity) ;; Required for a Node application
  (when-production false
    (enable-console-print!))
  (let [app (-> "electron" js/require .-app)
        exit-app #(.quit app)
        main-window (atom nil)
        open-main-window #(init-window main-window "app.html")
        osx (= js/process.platform "darwin")]
    (.on app "ready" init-menu)
    (.on app "ready" open-main-window)
    (.on app "activate" open-main-window)
    (when-not osx
      (.on app "window-all-closed" exit-app))))
