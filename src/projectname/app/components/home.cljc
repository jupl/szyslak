(ns projectname.app.components.home
  "Home page component structure."
  (:require
   [rum.core :as rum]))

(def page-style
  "Home page styling."
  {:flex 1
   :display "flex"
   :justifyContent "center"
   :alignItems "center"
   :background "linear-gradient(transparent, gainsboro)"})

;; --------- Template

(rum/defc template
  "Home page template."
  []
  [:div {:style page-style} "Hello, World"])

;; --------- Component

(def component
  "Home page component."
  template)
