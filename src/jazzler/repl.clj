(ns jazzler.repl
  (:gen-class)
  (:require [clojure.string :as s]
            [jazzler.parsing :as parser]
            ;; [jazzler.player :as player]
            [jazzler.overtone-format :as o]
            [jazzler.repl.io :as io]
            [jazzler.repl.commands :as c]
            [jazzler.repl.system :as sys]
            [jazzler.repl.state-machine :as state]
            [clojure.pprint :as p]))

(def demosong
  "Song: This is the Song Title
  [I [ii IV] V I]")

;; (->> song (o/add-notes) (o/apply-rhythm o/quarters))
;; (player/play-song song)


(defn run []
  (-> (sys/system)
      (state/init)
      (state/ready)))

(defn -main [& args]
  (run))
