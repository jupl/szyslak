(set-env!
 :source-paths #{"src"}
 :resource-paths #{"resources"}
 :dependencies '[[adzerk/boot-cljs              "2.0.0"     :scope "test"]
                 [adzerk/boot-reload            "0.5.1"     :scope "test"]
                 [binaryage/devtools            "0.9.3"     :scope "test"]
                 [binaryage/dirac               "1.2.4"     :scope "test"]
                 [crisptrutski/boot-cljs-test   "0.3.0"     :scope "test"]
                 [datascript                    "0.16.0"    :scope "test"]
                 [devcards                      "0.2.3"     :scope "test" :exclusions [cljsjs/react cljsjs/react-dom]]
                 [org.clojure/clojure           "1.8.0"     :scope "test"]
                 [org.clojure/clojurescript     "1.9.521"   :scope "test"]
                 [org.clojure/core.async        "0.3.442"   :scope "test"]
                 [pandeiro/boot-http            "0.7.6"     :scope "test"]
                 [powerlaces/boot-cljs-devtools "0.2.0"     :scope "test"]
                 [rum                           "0.10.8"    :scope "test"]
                 [tolitius/boot-check           "0.1.4"     :scope "test"]])

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
  (let [{:keys [closure-defines]} closure-opts
        new-defines (merge closure-defines
                           {'projectname.common.config/production true})
        prod-closure-opts (merge closure-opts {:closure-defines new-defines})]
    (comp
     (sift :include #{#"^devcards"} :invert true)
     (cljs :optimizations :advanced
           :compiler-options prod-closure-opts)
     (sift :include #{#"\.out" #"\.cljs\.edn$" #"^\." #"/\."} :invert true)
     (target))))

(deftask dev
  "Produce a development build."
  [d devcards  bool "Include devcards in build."
   s server    bool "Start a local server with dev tools and live updates."
   p port PORT int  "The port number to start the server in."]
  (let [{:keys [closure-defines]} closure-opts
        new-defines (merge closure-defines
                           {'projectname.common.config/devcards devcards
                            'projectname.common.config/hot-reload server})
        dev-closure-opts (merge closure-opts {:closure-defines new-defines})
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
               (target)]]
    (apply comp (remove nil? tasks))))

(deftask devcards
  "Produce a build containing devcards only with optimizations."
  []
  (let [{:keys [closure-defines]} closure-opts
        new-defines (merge closure-defines
                           {'projectname.common.config/devcards true})
        cards-closure-opts (merge closure-opts {:closure-defines new-defines})]
    (comp
     (sift :include #{#"^(?!devcards).*\.cljs\.edn$"} :invert true)
     (cljs :optimizations :advanced
           :compiler-options cards-closure-opts)
     (sift :include #{#"^assets/" #"^devcards(?!\.(cljs\.edn|out))"})
     (target))))

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
