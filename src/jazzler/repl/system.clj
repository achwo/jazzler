(ns jazzler.repl.system
  (:require [jazzler.repl.states :as state]
            [jazzler.repl.state-machine :as sm]))

(defn system [] 
  (merge (sm/state-machine)
         {:error nil
          :result nil}))
;; cwd filepath song

(def transitions
  {:init  {:else  :song}
   :ready {:open  :song
           :new   :song
           :else  :ready}
   :song  {;:close :ready
           :else  :song}
   :all   {:error :exit
           :exit  :shutdown}})

(def states 
  {:init  state/init
   :ready state/ready
   :song  state/song
   :exit  state/exit})

(defn start 
  ([]
   (sm/run (system) states transitions))
  ([song]
   (sm/run (assoc (system) :song song) states transitions)))
