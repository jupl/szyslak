(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources"}
 :dependencies '[[adzerk/boot-cljs              "1.7.228-1" :scope "test"]
                 [adzerk/boot-reload            "0.4.13"    :scope "test"]
                 [adzerk/boot-test              "1.1.2"     :scope "test"]
                 [binaryage/devtools            "0.8.2"     :scope "test"]
                 [binaryage/dirac               "0.7.4"     :scope "test"]
                 [crisptrutski/boot-cljs-test   "0.2.1"     :scope "test"]
                 [devcards                      "0.2.2"     :scope "test" :exclusions [cljsjs/react cljsjs/react-dom]]
                 [kibu/pushy                    "0.3.6"     :scope "test"]
                 [org.clojure/clojurescript     "1.9.293"   :scope "test"]
                 [org.clojure/core.async        "0.2.395"   :scope "test"]
                 [pandeiro/boot-http            "0.7.3"     :scope "test"]
                 [powerlaces/boot-cljs-devtools "0.1.2"     :scope "test"]
                 [tolitius/boot-check           "0.1.3"     :scope "test"]
                 [rum                           "0.10.7"    :only [server-render]]
                 [bidi                          "2.0.13"]
                 [datascript                    "0.15.4"]
                 [datascript-transit            "0.2.2"]
                 [hiccup                        "1.0.5"]
                 [org.clojure/clojure           "1.8.0"]
                 [org.clojure/data.json         "0.2.6"]
                 [org.immutant/immutant         "2.1.5"]
                 [ring/ring-core                "1.5.0"]
                 [ring/ring-defaults            "0.2.1"]])

(require
 '[adzerk.boot-cljs              :refer [cljs]]
 '[adzerk.boot-reload            :refer [reload]]
 '[adzerk.boot-test              :refer [test] :rename {test test-clj}]
 '[crisptrutski.boot-cljs-test   :refer [test-cljs]]
 '[powerlaces.boot-cljs-devtools :refer [cljs-devtools]]
 '[pandeiro.boot-http            :refer [serve]]
 '[tolitius.boot-check           :as    check])

;; Required to define custom test task.
(ns-unmap 'boot.user 'test)

(def closure-opts
  "Common Closure Compiler options for each build."
  {:output-wrapper :true})

(def target-path
  "Default directory for build output."
  "target")

;; Define default task options used across the board.
(task-options! aot {:namespace '#{org.project.app.main}}
               jar {:main 'org.project.app.main}
               pom {:project 'app
                    :version "0.1.0"}
               reload {:on-jsload 'org.project.common.reload/handle
                       :asset-path "public"}
               serve {:handler 'org.project.app.handler/dev-handler}
               target {:dir #{target-path}}
               test-cljs {:exit? true :js-env :phantom})

(deftask build
  "Produce a production build with optimizations."
  []
  (let [prod-closure-opts
        (assoc-in
         closure-opts
         [:closure-defines 'org.project.common.config/production]
         true)]
    (comp
     (sift :include #{#"^public/devcards"} :invert true)
     (cljs :optimizations :advanced
           :compiler-options prod-closure-opts)
     (sift :include #{#"\.out" #"\.cljs\.edn$" #"^\." #"/\."} :invert true)
     (aot)
     (pom)
     (uber)
     (sift :include #{#"\.clj$"} :invert true)
     (jar)
     (sift :include #{#"^project.jar$"})
     (target))))

(deftask dev
  "Start server locally with dev tools and live updates."
  [d devcards  bool "Include devcards in build."
   p port PORT int  "The port number to start the server in."]
  (let [dev-closure-opts
        (assoc-in
         closure-opts
         [:closure-defines 'org.project.common.config/hot-reload]
         true)
        tasks [(serve :port port)
               (sift :include #{#"\.clj$"} :invert true)
               (watch)
               (speak)
               (if-not devcards
                 (sift :include #{#"^public/devcards"} :invert true))
               (reload)
               (cljs-devtools)
               (cljs :source-map true
                     :optimizations :none
                     :compiler-options dev-closure-opts)
               (sift :include #{#"\.cljs\.edn$" #"^\." #"/\."} :invert true)
               (target)]]
    (apply comp (remove nil? tasks))))

(deftask devcards
  "Produce a build containing devcards only with optimizations."
  []
  (comp
   (sift :include #{#"^public/(?!devcards).*\.cljs\.edn$"} :invert true)
   (cljs :optimizations :advanced
         :compiler-options closure-opts)
   (sift :include #{#"^public/assets/"
                    #"^public/devcards(?!\.(cljs\.edn|out))"})
   (target)))

(deftask lint
  "Check and analyze source code."
  []
  (comp
   (sift :include #{#"\.clj[cs]?$"})
   (check/with-eastwood)
   (check/with-kibit)
   (check/with-bikeshed)))

(deftask test
  "Run all tests."
  []
  (comp
   (test-clj)
   (test-cljs)))
