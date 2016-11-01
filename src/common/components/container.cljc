(ns common.components.container
  "Container component structure."
  (:require
   [rum.core :as rum]))

(def ^:dynamic *context*
  "Represents context for server-side rendering."
  nil)

(def context-types
  "Types used for context."
  #?(:cljs {:connection js/React.PropTypes.object
            :dispatch js/React.PropTypes.func}))

(def parent-mixin
  "Parent container mixin."
  #?(:cljs {:child-context #(-> % :rum/args first)
            :class-properties {:childContextTypes context-types}}))

(def mixin
  "Child item mixin."
  {:class-properties {:contextTypes context-types}})

(defn get-context
  "Extract context from component."
  [comp]
  #?(:cljs (-> comp .-context (js->clj :keywordize-keys true))
     :clj *context*))

;; --------- Template

(rum/defc template
  "Container template."
  < parent-mixin
  [_ child]
  child)

;; --------- Component

(def component
  "Container component."
  template)
