(ns org.project.app.components.root
  "Application root component structure."
  (:require
   [rum.core :as rum]))

(def page-style
  "Application root styling."
  {:flex 1
   :display "flex"
   :justifyContent "center"
   :alignItems "center"
   :background "linear-gradient(transparent, gainsboro)"})

;; --------- Template

(rum/defc template
  "Application root template."
  []
  [:div {:style page-style} "Hello, World"])

;; --------- Component

(def component
  "Application root component."
  template)
