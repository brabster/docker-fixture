(defproject docker-fixture (or (System/getenv "PROJECT_VERSION") "0.0.0-SNAPSHOT")
  :description "Docker containers as clojure.test fixtures"
  :url "http://github.com/brabster/docker-fixture"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories [["snapshots" {:url "https://clojars.org/repo"
                               :username :env/clojars_username
                               :password :env/clojars_password}]
                 ["releases" {:url "https://clojars.org/repo"
                              :username :env/clojars_username
                              :password :env/clojars_password
                              :sign-releases false}]]
  :dependencies [[com.stuartsierra/component "0.3.1"]
                 [com.taoensso/timbre "4.7.4"]]
  :aliases {"qa" ["do"
                  ["clean"]
                  ["check"]
                  ["eastwood"]
                  ["bikeshed" "-m" "100"]
                  ["ancient"]
                  ["cloverage"]]}
  :eastwood {:include-linters [:keyword-typos
                               :non-clojure-file
                               :unused-fn-args
                               :unused-locals
                               :unused-namespaces
                               :unused-private-vars
                               :unused-private-vars]
             :exclude-linters [:suspicious-expression]}
  :plugins [[lein-ancient "0.6.10"]
            [lein-kibit "0.1.2" :exclusions [org.clojure/clojure
                                             org.clojure/tools.cli]]
            [jonase/eastwood "0.2.3"]
            [lein-bikeshed "0.3.0"]
            [lein-cloverage "1.0.6"]]
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[clj-http-lite "0.3.0"]]}
             :provided {:dependencies [[org.clojure/clojure "1.8.0"]]}})
