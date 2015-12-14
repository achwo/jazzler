(ns jazzler.repl.io
  (require [clojure.pprint :refer [pprint]]))

(defn write [s] (print s))
(defn writeln [s] (println s))

(defn readln [] 
  (read-line))

(defn- print-with-options [result option]
  (case option
    :pprint (pprint result)
    (writeln result)))

(defn print-result [{result :result options :print-options :as state}]
  (when result
    (print-with-options result (first options)))
  state)

(defn print-error [{error :error :as state}]
  (when error
    (writeln (str "Error: " error)))
  state)

(defn write-prompt [ctx s]
  ;; TODO: current directory
  (write (str s "> ")))

(defn flush-buffer []
  (flush))
