(ns jazzler.parser.transformer-test
  (:use midje.sweet)
  (:require [jazzler.parser.transformer :refer :all]))

;; TODO: is it even used?
(comment facts "about add-bar-numbers" 
  (fact "the first bar is bar 1"
    (add-bar-numbers [{}]) => [{:bar 1}])
  (fact "the second bar is bar 2"
    (add-bar-numbers [{} {}]) => [{:bar 1} {:bar 2}])
  (fact "works for more bars"
    (add-bar-numbers [{} {} {}]) => [{:bar 1} {:bar 2} {:bar 3}]))

(facts "about string->degree"
  (fact "it makes a lower-case roman numeral keyword out of an upper-case roman numeral string"
    (string->degree "I") => :i)
  (fact "it does only work with strings"
    (string->degree :I)) => (throws AssertionError))
