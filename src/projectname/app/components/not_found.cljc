(ns projectname.app.components.not-found
  "404 component structure."
  (:require
   [rum.core :as rum]))

(def page-style
  "Application root styling."
  {:flex 1
   :display "flex"
   :justifyContent "center"
   :alignItems "center"
   :flexDirection "column"})

;; --------- Template

(rum/defc template
  "404 template."
  []
  [:div {:style page-style}
   "404" [:a {:href "/"} "Home"]])

;; --------- Component

(def component
  "404 component."
  template)
