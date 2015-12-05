(ns jazzler.repl.runtime-test
  (:use midje.sweet)
  (:require [jazzler.repl.runtime :refer :all]))

(facts "about sanitize"
  (fact "it strips a string from whitespace"
    (sanitize "   hello   ") => "hello"))

(fact "about song"
  (fact "it gets the value of the song field"
    (song {:song :bla}) => :bla)
  (fact "it sets the song field"
    (song {} :bla) => {:song :bla}))
