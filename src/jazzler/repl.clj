(ns jazzler.repl
  (:gen-class)
  (:require [jazzler.parsing :as parser]
            [jazzler.player :as player]
            [jazzler.overtone-format :as o]
            [clojure.pprint :as p]))

;; currently unused
(defn repl []
  (do 
    (print "Jazzler> ")
    (flush))
  (let [input (read-line)]
    (p/pprint (parser/parse-progression input))
    (recur)))

(def demosong
  "Song: This is the Song Title
[I [ii IV] V I]")

(defn -main [& args]
  (println "Welcome to Jazzler 0.1")
  (println "======================")
  (let [song (->> (assoc (parser/parse-song demosong)
                        :key {:root :C3 :triad :major}
                        :bpm 80
                        :structure [:progression])
                 (o/add-notes)
                 (o/apply-rhythm o/quarters))]
      (println "\nData Structure: ")
      (p/pprint song)

    (println "\n Now trying to playback...")
    (player/play-song song)
  
  ;; (flush)
  ;; (repl)
))
