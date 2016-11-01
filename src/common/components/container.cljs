(ns common.components.container
  "Container component structure."
  (:require
   [rum.core :as rum]))

(def context-types
  "Types used for context."
  {:connection js/React.PropTypes.object
   :dispatch js/React.PropTypes.func})

(def parent-mixin
  "Parent container mixin."
  {:child-context #(-> % :rum/args first)
   :class-properties {:childContextTypes context-types}})

(def mixin
  "Child item mixin."
  {:class-properties {:contextTypes context-types}})

(def get-context
  "Extract context from component."
  #(-> % .-context js->clj))

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
