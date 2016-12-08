(ns projectname.common.config
  "Configuration macros used across the project.")

;; If true then application is a production environment
#?(:cljs (goog-define production false))

;; If true then hot reloading is available
#?(:cljs (goog-define hot-reload false))

(defmacro production?
  "Helper for evaluating code if in production for tree shaking."
  [] `(identical? production true))

(defmacro hot-reload?
  "Helper for evaluating code if in hot reload for tree shaking."
  [] `(identical? hot-reload true))

(def base-url
  "Base URL where assets are located."
  #?(:cljs (cond
             (hot-reload?) "http://localhost:3000/"
             (production?) (str "file://" js/__dirname "/")
             :else (str "file://" (js/process.cwd) "/"))))
