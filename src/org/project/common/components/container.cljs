(ns org.project.common.components.container
  "Container component structure."
  (:require
   [rum.core :as rum]))

(def context-types
  "Types used for context."
  {:connection js/React.PropTypes.object
   :dispatch js/React.PropTypes.func})

(def parent-mixin
  "Parent container mixin."
  {:child-context #(-> % :rum/args first (dissoc :component))
   :class-properties {:childContextTypes context-types}})

(def mixin
  "Child item mixin."
  {:class-properties {:contextTypes context-types}})

(defn get-context
  "Extract context from component."
  [component]
  (-> component .-context (js->clj :keywordize-keys true)))

;; --------- Template

(rum/defc template
  "Container template."
  < parent-mixin
  [{:keys [component]}]
  (component))

;; --------- Component

(def component
  "Container component."
  template)
