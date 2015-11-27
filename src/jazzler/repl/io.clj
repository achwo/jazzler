(ns jazzler.repl.io)

(defn write [s] (print s))
(defn writeln [s] (println s))

(defn readln [] 
  (read-line))

(defn print-result [{result :result :as state}]
  (writeln result)
  state)

(defn write-prompt [state]
  ;; TODO: current directory
  (write "Jazzler> "))

(defn flush-buffer []
  (flush))
