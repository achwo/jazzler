(ns jazzler.core
  (:gen-class)
  (:require 
   [jazzler.repl.system :as repl]
            [jazzler.parser.system :as p]
            [jazzler.player :as pl]))

(def demosong
  "Song: This is the Song Title
  [I [ii IV] V I]")

;; (->> song (o/add-notes) (o/apply-rhythm o/quarters))
;; (player/play-song song)

(defn -main [& args]
  (if args
    ;; (print (slurp (first args)))
    (repl/start (p/parse-song (slurp (first args))))
    (repl/start)))
