(ns jazzler.repl.state-machine
  (:require [clojure.string :as string]
            [jazzler.repl.system :as s]
            [jazzler.repl.commands :as c]
            [jazzler.repl.io :as io]))

(declare transition)

;; misc

(defn sanitize [s]
  (string/trim s))

(defn shutdown [system]
  (assoc system :state :shutdown))

(defn error [system]
  (io/writeln "Something went horribly wrong!") 
  (io/writeln (:error system))
  (shutdown system))


(defn shutting-down? [system]
  (not (:state system)))


;; states

(defn exit [system]
  (io/writeln "Shutting down"))

(defn init [system]
  (s/greet)
  system)

(defn ready [system]
  (io/write-prompt system)
  (io/flush-buffer)
  (let [in (-> (io/readln) (sanitize))
        [command args] (c/comm in)
        new-system (apply command system args)]
    (io/print-result new-system)
    (if (:transition new-system)
      (transition new-system)
      (recur (s/clear-result new-system)))))

(defn song [system]
  (io/writeln "song")
  system)


;; state machine

(defn transition-old [{t :transition :as system}]
  (let [new-system (assoc system :transition nil)]
    (case t
      :exit (s/shutdown (s/clear-transition new-system))
      :song (song (s/clear-transition new-system))
      (error (assoc system :error (str "Unknown transition state: " t)))
      )))

(def states 
  {:init init
   :ready ready
   :song song
   :exit exit
   :shutdown shutdown})

(def transitions
  {:init {:else :ready}
   :ready {:open :song
           :new :song}
   
   :all {:error :exit
         :exit :shutdown}


})

(defn transition-target [state trans]
  {:post [(not (nil? %))]}
    (or (trans (state transitions))
        (trans (:all transitions))))

(defn transition [{s :state t :transition :as system}]
    (assoc system :state (transition-target s t) :transition nil))

(defn run [{st :state :as system}]
  (when-not (= st :shutdown)
    (recur (-> system 
               ((st states)) 
               (transition)))))
