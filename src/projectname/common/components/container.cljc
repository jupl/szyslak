(ns projectname.common.components.container
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
  #?(:cljs {:child-context #(-> % :rum/args first (dissoc :component))
            :class-properties {:childContextTypes context-types}}))

(def mixin
  "Child item mixin."
  #?(:cljs {:class-properties {:contextTypes context-types}}))

(defn get-context
  "Extract context from component."
  [component]
  #?(:cljs (-> component .-context (js->clj :keywordize-keys true))
     :clj *context*))

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