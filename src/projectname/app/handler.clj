(ns projectname.app.handler
  "Application ring handler."
  (:require
   [bidi.ring :refer [make-handler]]
   [clojure.data.json :as json]
   [datascript.core :refer [create-conn transact!]]
   [hiccup.page :refer [html5 include-css include-js]]
   [projectname.app.config :refer [routes]]
   [projectname.app.components.root :as root]
   [projectname.common.components.container :as container]
   [projectname.common.db :refer [update-in-server! update-route!]]
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
       "window.__DB__=" (json/write-str (pr-str @connection)) ";"]
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

(def create-dev-handler
  "Create a development handler using above handler."
  (memoize #(do (require '[ring.middleware.reload]
                         '[ring.middleware.stacktrace])
                (-> #'handler
                    ((resolve 'ring.middleware.reload/wrap-reload))
                    ((resolve 'ring.middleware.stacktrace/wrap-stacktrace))))))

(defn- dev-handler
  "Application Ring handler with wrappers for development server."
  [& args]
  (apply (create-dev-handler) args))