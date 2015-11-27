(ns jazzler.repl.system
  (:require [jazzler.repl.io :as io]))

(defn system [] {:error nil
               :path nil
               :song nil
               :quit false})

(defn shutdown [state])

(defn clear-result [state] (assoc state :result nil))

(defn clear-transition [state] (assoc state :transition nil))

(defn output-result [{result :result :as state}])

(defn greet []
  (io/writeln "Welcome to Jazzler 0.1")
  (io/writeln "======================"))
