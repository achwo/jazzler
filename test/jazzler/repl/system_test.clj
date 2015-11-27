(ns jazzler.repl.system-test
  (:use midje.sweet)
  (:require [jazzler.repl.system :refer :all]))

(facts "about system"
  (fact "it has the necessary fields"
    (keys (system)) => (contains #{:error :result})))
