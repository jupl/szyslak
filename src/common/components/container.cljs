(ns common.components.container
  "Container component structure."
  (:require
   [rum.core :as rum]))

(def context-types
  "Types used for context."
  {:connection js/React.PropTypes.object
   :dispatch js/React.PropTypes.func})

(def context
  "Parent container mixin."
  {:child-context #(-> % :rum/args first)
   :class-properties {:childContextTypes context-types}})

;; --------- Template

(rum/defc template
  "Container template."
  < context
  [_ child]
  child)

;; --------- Component

(def component
  "Container component."
  template)
