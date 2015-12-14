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

(def help-s
  {:general "You are in standard mode. You can use the following commands:

new: Transition to song mode with an empty song.
load <filepath>: Load a file and go into song mode."
   
   })

(defn help
  [ctx [cmd-str & [detail]]]
  (if-let [helptext (help-s (keyword detail))]
    (r/result ctx helptext)
    (r/result ctx (:general help-s))))

(def commands
  {:help help
   :open open
   :close close
   :exit exit
   :quit exit})

(defn command 
  "Returns a tuple with a fn and a seq of arguments."
  [s]
  (let [words (s/split s #"\s+")
        comm ((keyword (first words)) commands unknown-command)]
    [comm words]))
