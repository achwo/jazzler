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

;; TODO: I can't think of a test for run right now...
