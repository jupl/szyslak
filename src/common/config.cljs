(ns common.config
  "Configuration information used across the project.")

;; If true then application is a production environment
(goog-define production false)

;; If true then hot reloading is available
(goog-define hot-reload false)

(def base-url
  "Base URL for application assets."
  (cond
    (identical? hot-reload true) "http://localhost:3000/"
    (exists? js/__dirname) (str "file://" js/__dirname "/")))
