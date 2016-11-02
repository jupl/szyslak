(ns org.project.common.config
  "Configuration macros used across the project.")

;; If true then application is a production environment
#?(:cljs (goog-define production false))

;; If true then hot reloading is available
#?(:cljs (goog-define hot-reload false))

(def base-url
  "Base URL where assets are located."
  #?(:cljs (when (exists? js/require)
             (if (identical? hot-reload true)
               (let [os (js/require "os")]
                 (str "http://" (.hostname os) ":3000/"))
               (str "file://" js/__dirname "/")))))

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
