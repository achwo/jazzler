(ns jazzler.repl
  (:gen-class)
  (:require [jazzler.parsing :as parser]
            [clojure.pprint :as p]))

(defn repl []
  (do 
    (print "Jazzler> ")
    (flush))
  (let [input (read-line)]
    (p/pprint (parser/parse-progression input))
    (recur)))

(defn -main [& args]
  (println "Welcome to Jazzler 0.1")
  (println "======================")
  (flush)
  (repl))
