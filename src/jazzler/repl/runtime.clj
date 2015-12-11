(ns jazzler.repl.runtime
  (:require [jazzler.repl.io :as io]
            [clojure.string :as string]))

(defn sanitize [s]
  (string/trim s))

(defn shutdown [system]
  ;; TODO: is this the right place? changes transition
  (assoc system :transition :exit))

(defn result 
  ([system] (:result system))
  ([system s & options] 
   (if options 
     (assoc system :result s :print-options options)
     (assoc system :result s))))

(defn error [system s]
  (assoc system :error s))

(defn clear-print-options [system]
  (dissoc system :print-options))

(defn clear-result-and-error [system] 
  (-> system
      (result nil)
      (error nil)
      (clear-print-options)))

(defn output-result [system] (result system))

(defn file [system file]
  (assoc system :file file))

(defn song 
  ([system] (:song system))
  ([system song] (assoc system :song song)))

(defn runtime-error [system]
  (io/writeln "Something went horribly wrong!") 
  (io/writeln (:error system))
  (shutdown system))

(defn greet []
  (io/writeln "Welcome to Jazzler 0.1")
  (io/writeln "======================"))
