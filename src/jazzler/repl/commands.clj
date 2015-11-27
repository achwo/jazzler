(ns jazzler.repl.commands
  (:require [jazzler.repl.io :as io]
            [jazzler.repl.runtime :as rt]
            [clojure.string :as s]))


(defn unknown-command [system args]
  (rt/error system (str "Unknown command: " args)))

(defn open [system args]
  (io/writeln "open")
  ;; TODO actually open and load file
  (rt/file system (second args)))

(defn close [system args]
  (io/writeln "close")
  (rt/file system nil))

(defn exit [system args]
  (rt/shutdown system))

(def commands
  {:open open
   :close close
   :exit exit
   :quit exit})

(defn command 
  "Returns a tuple with a fn and a seq of arguments."
  [s]
  (let [words (s/split s #"\s+")
        comm ((keyword (first words)) commands)]
    [comm words]))
