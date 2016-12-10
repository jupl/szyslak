(ns projectname.electron.menu
  "ClojureScript API for Electron menu."
  (:require
   [projectname.common.config :refer-macros [devcards? production?]]
   [projectname.electron.window :refer [init-window]]))

(def menu-template
  "Template to render menu in Electron."
  (atom []))

(def develop-template
  "Template to render Develop menu item."
  {:label "Develop"
   :submenu [{:role "toggledevtools"
              :accelerator "F12"}]})

(defn- osx-menu-item
  "Create additional menu item for OS X."
  []
  (let [name (-> "electron" js/require .-app .getName)]
    {:label name
     :submenu [{:role "about"}
               {:type "separator"}
               {:role "services" :submenu []}
               {:type "separator"}
               {:role "hide"}
               {:role "hideothers"}
               {:role "unhide"}
               {:type "separator"}
               {:role "quit"}]}))

(defn- dev-menu-item
  "Create additional menu item for development."
  []
  (when-not (production?)
    (if (devcards?)
      (let [devcards-window (atom nil)
            open-devcards #(init-window devcards-window "devcards.html")]
        (update-in develop-template [:submenu] conj {:label "Open Devcards"
                                                     :accelerator "Shift+F12"
                                                     :click open-devcards}))
      develop-template)))

(defn- update-menu!
  "Handler for when menu template is updated, which rebuilds and updates menu."
  [_ _ _ template]
  (let [menu (-> "electron" js/require .-Menu)
        osx (= js/process.platform "darwin")
        pre-menu (if osx [(osx-menu-item)] [])
        post-menu (if (production?) [] [(dev-menu-item)])]
    (->> (concat pre-menu template post-menu)
         clj->js
         (.buildFromTemplate menu)
         (.setApplicationMenu menu))))

(defn init-menu
  "Intialize Electron menu and monitor for changes. Call this only once."
  [name]
  (add-watch menu-template :watcher update-menu!)
  (reset! menu-template @menu-template))
