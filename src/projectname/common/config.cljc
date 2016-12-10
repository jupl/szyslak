(ns projectname.common.config
  "Configuration macros used across the project.")

;; If true then devcards is available
#?(:cljs (goog-define devcards false))

;; If true then hot reloading is available
#?(:cljs (goog-define hot-reload false))

;; If true then application is a production environment
#?(:cljs (goog-define production false))

(def in-server
  "If true then code is in a server environment."
  #?(:cljs false :clj true))

(defmacro devcards?
  "Helper for evaluating code if devcards is available for tree shaking."
  []
  `(identical? devcards true))

(defmacro hot-reload?
  "Helper for evaluating code if in hot reload for tree shaking."
  []
  `(identical? hot-reload true))

(defmacro in-server?
  "Helper for evaluating code if in the server for tree shaking."
  []
  `(identical? in-server true))

(defmacro production?
  "Helper for evaluating code if in production for tree shaking."
  []
  `(identical? production true))
