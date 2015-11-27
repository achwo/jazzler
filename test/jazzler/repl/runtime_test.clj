(ns jazzler.repl.runtime-test
  (:use midje.sweet)
  (:require [jazzler.repl.runtime :refer :all]))

(facts "about sanitize"
  (fact "it strips a string from whitespace"
    (sanitize "   hello   ") => "hello"))
