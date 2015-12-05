(ns jazzler.repl.states
  (:require [clojure.pprint :refer [pprint]]
   [jazzler.repl.runtime :as r]
            [jazzler.repl.commands :as c]
            [jazzler.repl.song-commands :as sc]
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
    (io/print-error new-system)
    (r/clear-result-and-error new-system)))

;; song state is, where you can define a song
;; accepted commands will be:
;; - setters for keys existing in the language
;; - inspector functions for looking at the current state
;; - rhythm-setter (even though not yet existing in language)
;; - play and stop for playing back
;; - close for leaving songmode
;; - exit
;; - undo and redo
(defn song 
  [system]
  (print "INFO: ")
  (pprint system) ;; TODO proper logging
  (io/write-prompt system)
  (io/flush-buffer)
  (let [in (-> (io/readln) (r/sanitize))
        [command args] (sc/command in)
        new-system (command system args)]
    (io/print-result new-system)
    (io/print-error new-system)
    (r/clear-result-and-error new-system)))
