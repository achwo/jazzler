(ns jazzler.repl.io)

(defn write [s] (print s))
(defn writeln [s] (println s))

(defn readln [] 
  (read-line))

(defn print-result [{result :result :as state}]
  (when result
    (writeln result))
  state)

(defn print-error [{error :error :as state}]
  (when error
    (writeln (str "Error: " error)))
  state)

(defn write-prompt [state]
  ;; TODO: current directory
  (write "Jazzler> "))

(defn flush-buffer []
  (flush))
