(ns jazzler.repl.states
  (:require [jazzler.repl.runtime :as r]
            [jazzler.repl.commands :as c]
            [jazzler.repl.io :as io]))

(defn- transition [system trans]
  (assoc system :transition trans))

(defn exit [system]
  (io/writeln "Shutting down")
  ;; TODO: maybe close files, etc.
  system)

(defn init [system]
  (r/greet)
  system)

(defn ready [system]
  (println system) ;; TODO proper logging
  (io/write-prompt system)
  (io/flush-buffer)
  (let [in (-> (io/readln) (r/sanitize))
        [command args] (c/command in)
        new-system (command system args)]
    (io/print-result new-system)
    (r/clear-result new-system)))

(defn song [system]
  (io/writeln "song")
  system)
