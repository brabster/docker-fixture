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
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[clj-http-lite "0.3.0"]]}
             :provided {:dependencies [[org.clojure/clojure "1.8.0"]]}})
