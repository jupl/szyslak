(ns common.config
  "Configuration information used across the project.")

;; If true then application is a production environment
(goog-define production false)

;; If true then hot reloading is available
(goog-define hot-reload false)

;; When stringifying keywords, include namespaces
(extend-type Keyword IEncodeJS (-clj->js [s] (subs (str s) 1)))

(def base-url
  "Base URL for application assets."
  (cond
    (identical? hot-reload true) "http://localhost:3000/"
    (exists? js/__dirname) (str "file://" js/__dirname "/")))
