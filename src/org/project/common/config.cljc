(ns org.project.common.config
  "Configuration macros used across the project.")

;; If true then application is a production environment
#?(:cljs (goog-define production false))

;; If true then hot reloading is available
#?(:cljs (goog-define hot-reload false))

(def in-server
  "If true then code is in a server environment."
  #?(:cljs false :clj true))

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

(defmacro when-in-server
  "Helper for evaluating code if (not) in the server for tree shaking."
  [flag & body]
  `(when (identical? in-server ~flag)
     ~@body))
