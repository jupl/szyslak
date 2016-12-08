(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources"}
 :dependencies '[[adzerk/boot-cljs              "1.7.228-2" :scope "test"]
                 [adzerk/boot-reload            "0.4.13"    :scope "test"]
                 [binaryage/devtools            "0.8.3"     :scope "test"]
                 [binaryage/dirac               "0.8.4"     :scope "test"]
                 [crisptrutski/boot-cljs-test   "0.3.0"     :scope "test"]
                 [datascript                    "0.15.5"    :scope "test"]
                 [devcards                      "0.2.2"     :scope "test" :exclusions [cljsjs/react cljsjs/react-dom]]
                 [org.clojure/clojure           "1.8.0"     :scope "test"]
                 [org.clojure/clojurescript     "1.9.293"   :scope "test"]
                 [org.clojure/core.async        "0.2.395"   :scope "test"]
                 [pandeiro/boot-http            "0.7.6"     :scope "test"]
                 [powerlaces/boot-cljs-devtools "0.1.2"     :scope "test"]
                 [rum                           "0.10.7"    :scope "test"]
                 [tolitius/boot-check           "0.1.3"     :scope "test"]])

(require
 '[adzerk.boot-cljs              :refer [cljs]]
 '[adzerk.boot-reload            :refer [reload]]
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
(task-options! reload {:on-jsload 'projectname.common.reload/handle}
               serve {:dir target-path}
               target {:dir #{target-path}}
               test-cljs {:exit? true :js-env :phantom})

(deftask build
  "Produce a production build with optimizations."
  []
  (let [prod-closure-opts
        (assoc-in
         closure-opts
         [:closure-defines 'projectname.common.config/production]
         true)]
    (comp
     (sift :include #{#"^devcards"} :invert true)
     (cljs :ids #{"index"}
           :optimizations :simple
           :compiler-options (merge prod-closure-opts {:optimize-constants true
                                                       :static-fns true}))
     (sift :include #{#"^index\.cljs\.edn$"} :invert true)
     (cljs :optimizations :advanced
           :compiler-options prod-closure-opts)
     (sift :include #{#"\.out" #"\.cljs\.edn$" #"^\." #"/\."} :invert true)
     (target))))

(deftask dev
  "Produce a development build."
  [d devcards  bool "Include devcards in build."
   s server    bool "Start a local server with dev tools and live updates."
   p port PORT int  "The port number to start the server in."]
  (let [dev-closure-opts
        (assoc-in
         closure-opts
         [:closure-defines 'projectname.common.config/hot-reload]
         server)
        tasks [(if server (serve :port port))
               (if server (watch))
               (if server (speak))
               (if-not devcards (sift :include #{#"^devcards"} :invert true))
               (if server (reload))
               (if server (cljs-devtools))
               (cljs :source-map true
                     :optimizations :none
                     :compiler-options dev-closure-opts)
               (sift :include #{#"\.cljs\.edn$" #"^\." #"/\."} :invert true)
               (if-not server (sift :include #{#"\.out"} :invert true))
               (target)]]
    (apply comp (remove nil? tasks))))

(deftask devcards
  "Produce a build containing devcards only with optimizations."
  []
  (comp
   (sift :include #{#"^(?!devcards).*\.cljs\.edn$"} :invert true)
   (cljs :optimizations :advanced
         :compiler-options closure-opts)
   (sift :include #{#"^assets/" #"^devcards(?!\.(cljs\.edn|out))"})
   (target)))

(deftask lint
  "Check and analyze source code."
  []
  (comp
   (sift :include #{#"\.clj[cs]?$"})
   (check/with-kibit)
   (check/with-bikeshed)))

(deftask test
  "Run all tests."
  []
  (test-cljs))
