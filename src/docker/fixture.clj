(ns docker.fixture
  "Docker-based clojure.test fixture"
  (:require [com.stuartsierra.component :as component]
            [clojure.string :refer [trim-newline split]]
            [clojure.java.shell :refer [sh]]
            [taoensso.timbre :as log]))

(defn rand-port
  "Get a string representation of a random TCP port in the ephemeral range"
  []
  (str (+ 0x400 (rand-int (- 0x10000 0x400)))))

(def host
  "Get the container host; try docker-machine, default to localhost"
  (memoize #(try
              (trim-newline (:out (sh "docker-machine" "ip")))
              (catch Exception e (prn 'Error 'getting 'host e) "localhost"))))

(defrecord Container [cmd sleep]
  component/Lifecycle
  (start [component]
    (log/debug "starting container" cmd)
    (log/debug "note, pulling the container if needed may take some time")
    (let [shell-result (apply sh cmd)
          container-id (if (not= 0 (:exit shell-result))
                         (throw (ex-info "Unable to start docker container" shell-result))
                         (trim-newline (:out shell-result)))
          container-host (host)]
      (log/debug "waiting for container" container-id)
      (Thread/sleep sleep)
      (log/debug "sleep complete")
      (merge component {:container-id container-id
                        :host container-host})))

  (stop [component]
    (log/debug "killing container" (:container-id component))
    (sh "docker" "kill" (:container-id component))
    (sh "docker" "rm" (:container-id component))
    (log/debug "Stopped and removed container" (:container-id component))))

(defn new-container
  "Create a new docker container using cmd, waiting for sleep for the container to become ready"
  [{:keys [cmd sleep]}]
  (map->Container {:cmd cmd :sleep sleep}))

(defn new-fixture
  "A new test fixture using the docker cmd, sleeping for sleep ms
  before allowing test to continue, then running init-fn for side
  effects, a fn taking an argument of component."
  [{:keys [cmd sleep init-fn]
    :or {init-fn identity
         sleep 1000}}]
  (let [component (atom nil)]
    (fn [f]
      (reset! component
              (try (component/start (new-container {:cmd cmd :sleep sleep}))
                   (catch Exception e
                     (log/error "Unable to start container" (ex-data e))
                     (throw e))))
      (init-fn component)
      (try
        (f)
        (finally
          (component/stop @component))))))
