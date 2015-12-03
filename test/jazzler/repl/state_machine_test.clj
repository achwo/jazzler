(ns jazzler.repl.state-machine-test
  (:use midje.sweet)
  (:require [jazzler.repl.state-machine :refer :all]))

(def transitions
  {:init {:done :ready}
   :ready {:else :ready}
   :all {:error :exit}})

(facts "about transition-target"
  (fact "it returns the target of the transition on the given state"
    (transition-target :init :done transitions) => :ready)
  (fact "if the rule is not in the state, look in :all"
    (transition-target :init :error transitions) => :exit)
  (fact "the default rule is :else"
    (transition-target :ready :else transitions) => :ready))

(facts "about shutting-down?"
  (fact "it's true, when system state is :shutdown"
    (shutting-down? {:state :shutdown}) => true)
  (fact "or false else"
    (shutting-down? {:state :ready}) => false))

(fact "state-machine contains the necessary fields"
  (keys (state-machine)) => (contains #{:state :transition}))

(fact "transition updates :state and :transition"
  (let [a-sys {:state :init :transition :done}]
    (transition a-sys transitions) => {:state :ready :transition :else}))

(declare init)

(fact "run exits on shutdown"
  (let [a-sys {:state :init :transition :quit}
        trans {:init {:quit :shutdown}}
        state-fns {:init #(init %)}]
    (run a-sys state-fns trans) => nil
    (provided (init a-sys) => a-sys)))


;; TODO: for a run test, i want to have injected io functions 
