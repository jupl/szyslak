(ns projectname.common.config
  "Configuration macros used across the project.")

;; If true then application is a production environment
#?(:cljs (goog-define production false))

;; If true then hot reloading is available
#?(:cljs (goog-define hot-reload false))

(def base-url
  "Base URL where assets are located."
  #?(:cljs (if (identical? hot-reload true)
             "http://localhost:3000/"
             (str "file://" (js/process.cwd) "/"))))

(defmacro when-production
  "Helper for evaluating code if (not) in production for tree shaking."
  [flag & body]
  `(when (identical? production ~flag)
     ~@body))

(defmacro when-hot-reload
  "Helper for evaluating code if there is (not) hot reload for tree shaking."
  [flag & body]
  `(when (identical? hot-reload ~flag)
     ~@body))
