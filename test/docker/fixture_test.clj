(ns docker.fixture-test
  (:require [docker.fixture :as docker]
            [clj-http.lite.client :as http]
            [clojure.test :refer :all]))

;; get a random port in the ephemeral range
(def port (docker/rand-port))

;; get the docker host we should connect to
(def host (docker/host))

;; prove interaction with fixture by init-fn
(def fixture-response (atom nil))

;; easy http GET
(defn component-http-get
  ([host port]
   (http/get (str "http://" host ":" port "/knock-knock"))))

;; start and HTTP server that echos responses
;; request it in init-fn and put the response in the fixture-response atom...
(use-fixtures :once
  (docker/new-fixture {:cmd ["docker" "run" "-d" "-p" (str port ":80") "keisato/http-echo"]
                       :sleep 500
                       :init-fn (fn [component]
                                  (reset! fixture-response
                                          (component-http-get (:host component) port)))}))

;; did the init-fn interact with the fixture?
(deftest test-fixture-init
  (is (= 200 (:status @fixture-response))))

;; can we interact with the fixture now?
(deftest test-fixture-interact
  (is (= 200 (:status (component-http-get host port)))))
