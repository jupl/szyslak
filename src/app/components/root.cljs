(ns app.components.root
  "Application root component structure."
  (:require
   [color.components.page :as color-page]
   [rum.core :as rum]))

(def page-style
  "Application root styling."
  {:flex 1})

;; --------- Template

(rum/defc template
  "Application root template."
  []
  (color-page/component {:style page-style}))

;; --------- Component

(def component
  "Application root component."
  template)
