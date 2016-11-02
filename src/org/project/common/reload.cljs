(ns org.project.common.reload
  "Logic for reload notification if hot reloading is available.")

;; Set of handlers called when a hot reload occurs in a dev environment.
(defonce handlers (atom #{}))

(defn add-handler
  "Add a callback function to reload handlers."
  [handler]
  (swap! handlers conj handler))

(defn- handle
  "Invoke all registered reload handlers."
  []
  (doseq [handler @handlers]
    (js/setTimeout handler 0)))
