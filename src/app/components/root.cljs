(ns app.components.root
  "Application root component structure."
  (:require
   [rum.core :as rum]))

(def root-style
  "Application page styling."
  {:flex 1
   :display "flex"
   :justifyContent "center"
   :alignItems "center"
   :background "linear-gradient(transparent, gainsboro)"})

;; --------- Template

(rum/defc template
  "Application root template."
  []
  [:div {:style root-style} "Hello, World"])

;; --------- Component

(def component
  "Application root component."
  template)
