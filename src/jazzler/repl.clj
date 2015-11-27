(ns jazzler.repl
  (:gen-class)
  (:require [jazzler.repl.system :as sys]))

(def demosong
  "Song: This is the Song Title
  [I [ii IV] V I]")

;; (->> song (o/add-notes) (o/apply-rhythm o/quarters))
;; (player/play-song song)

(defn -main [& args]
  (sys/start))
