(ns jazzler.repl.runtime
  (:require [jazzler.repl.io :as io]
            [clojure.string :as string]))

(defn sanitize [s]
  (string/trim s))

(defn shutdown [system]
  ;; TODO: is this the right place? changes transition
  (assoc system :transition :exit))

(defn clear-result [system] (assoc system :result nil))

(defn output-result [{result :result :as system}])

(defn error [system s]
  (assoc system :error s))

(defn file [system file]
  (assoc system :file file))

(defn error [system]
  (io/writeln "Something went horribly wrong!") 
  (io/writeln (:error system))
  (shutdown system))

(defn greet []
  (io/writeln "Welcome to Jazzler 0.1")
  (io/writeln "======================"))
