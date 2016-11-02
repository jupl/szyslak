(ns org.project.app.handler
  "Application ring handler."
  (:require
   [bidi.ring :refer [make-handler]]
   [clojure.data.json :as json]
   [datascript.core :refer [create-conn transact!]]
   [datascript.transit :refer [write-transit-str]]
   [hiccup.page :refer [html5 include-css include-js]]
   [org.project.app.config :refer [routes]]
   [org.project.app.components.root :as root]
   [org.project.common.components.container :as container]
   [org.project.common.db :refer [update-in-server! update-route!]]
   [ring.middleware.resource :refer [wrap-resource]]
   [rum.core :refer [render-html]]))

(defn- home-page
  "Home page."
  [{:keys [connection] :as props}]
  (binding [container/*context* props]
    (html5 {:lang "en"}
     [:head
      [:meta {:charset "UTF-8"}]
      [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
      [:meta {:name "viewport" :content "initial-scale=1.0"}]
      [:title "App"]
      (include-css "/assets/normalize.css")]
     [:body
      [:div#container {:style "min-height:100vh;display:flex"}
       (render-html (container/component props))]
      [:script {:type "text/javascript"}
       "window.__DB__=" (json/write-str (write-transit-str @connection)) ";"]
      (include-js "/app.js")])))

(defn- bidi-handler
  "Default response to show."
  [handler]
  (fn request-handler
    [{:keys [route-params]}]
    (let [connection (create-conn)
          route {:handler handler :route-params route-params}
          props {:connection connection :component root/component}]
      (update-route! connection route)
      (update-in-server! connection)
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (home-page props)})))

(def handler
  "Finalized application Ring handler."
  (-> routes
      (make-handler bidi-handler)
      (wrap-resource "public")))

(defn- dev-handler
  "Application Ring handler with wrappers for development server."
  [& args]
  (require '[ring.middleware.reload])
  (require '[ring.middleware.stacktrace])
  (defonce -wrap-reload (resolve 'ring.middleware.reload/wrap-reload))
  (defonce -wrap-stacktrace (resolve 'ring.middleware.stacktrace/wrap-stacktrace))
  (defonce -dev-handler (-> handler -wrap-stacktrace -wrap-reload))
  (apply -dev-handler args))
