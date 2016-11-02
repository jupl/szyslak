(ns org.project.color.components.page
  "Color page component structure."
  (:require
   [datascript.core :refer [q]]
   [org.project.color.db :refer [color-query]]
   [org.project.common.components.container :as container]
   [rum.core :as rum]))

(def background-style
  "Background styling."
  {:transition "background 0.8s ease-out"
   :display "flex"})

(def button-style
  "Button styling."
  {:borderRadius "50%"
   :background "radial-gradient(white, gainsboro)"
   :borderColor "gainsboro"
   :outline "none"})

(def text-style
  "Text styling."
  {:marginLeft 4
   :marginRight 4
   :marginTop 4})

(def gradient-style
  "Gradient styling."
  {:flex 1
   :display "flex"
   :justifyContent "center"
   :alignItems "center"
   :background "linear-gradient(rgba(255, 255, 255, 0.4), transparent)"})

;; --------- Template

(rum/defc template
  "Application root template."
  [{:keys [color style on-next-color on-previous-color]}]
  (let [top-style (merge background-style style {:backgroundColor color})]
    [:div {:style top-style}
     [:div {:style gradient-style}
      [:button {:style button-style :on-click on-previous-color} "<"]
      [:span {:style text-style} "Hello, World"]
      [:button {:style button-style :on-click on-next-color} ">"]]]))

;; --------- Component

(rum/defcc component
  "Application root component."
  < rum/reactive container/mixin
  [comp {:keys [style]}]
  (let [{:keys [connection dispatch]} (container/get-context comp)
        db (rum/react connection)]
    (template {:style style
               :color (q color-query db)
               :on-next-color #(dispatch :color.messenger/next)
               :on-previous-color #(dispatch :color.messenger/previous)})))
