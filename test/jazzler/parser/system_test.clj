(ns jazzler.parser.system-test
  (:use midje.sweet)
  (:require [jazzler.parser.system :refer :all]
            
(facts "about parse progression"
  (let [chord-I (chord :i :major)]
    (fact "it can be empty"
      (parse-progression "[]") => {:figures {:progression '()}})
    (fact "it can have bars with one chord"
      (parse-progression "[[I]]") 
      => {:figures {:progression [{:elements [chord-I]}]}})
    (fact "it can have aliases of chordname for a whole bar"
      (parse-progression "[I]") 
      => {:figures {:progression [{:elements [chord-I]}]}})
    (fact "a bar can contain more than one chord"
      (parse-progression "[[I IV]]") 
      => {:figures {:progression [{:elements [chord-I chord-IV]}]}})
    (fact "it can contain more than one bar"
      (parse-progression "[[I] [IV]]") 
      => {:figures {:progression [{:elements [chord-I]}
                                  {:elements [chord-IV]}]}}
      (parse-progression "[I IV]") 
      => {:figures {:progression [{:elements [chord-I]}
                                  {:elements [chord-IV]}]}})))
