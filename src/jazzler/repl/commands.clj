(ns jazzler.repl.commands
  (:require [clojure.string :as s]
            [jazzler.repl.io :as io]))

;; TODO: io handler in state? -> keine dependency

(defn- error [state s]
  (assoc state :error s))

(defn- file [state file]
  (assoc state :file file))

(defn unknown-command [state & s]
  (error state (str "Unknown command: " s)))

(defn open [state & s]
  (io/writeln "open")
  ;; TODO actually open and load file
  (file state :dummy))

(defn close [state & s]
  (io/writeln "close")
  (file state nil))

(defn exit [state & s]
  (assoc state :transition :exit))

;; (p/pprint (parser/parse-progression input))

(defn comm [s]
  (let [words (s/split s #"\s+")]
    (case (first words)
      "open" [open (rest words)]
      "close" [close (rest words)]
      "exit" [exit (rest words)]
      "quit" [exit (rest words)]
      [unknown-command words])))
