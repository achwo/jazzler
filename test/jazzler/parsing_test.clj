(ns jazzler.parsing-test
  (:use midje.sweet)
  (:require [jazzler.parsing :refer :all]
            [instaparse.core :as i]))

(def progression-parser
  (i/parser
"progression = <'['>bar?<']'>
bar = <'['>chord<']'>
chord = #'[A-G]'
whitespace = #'\\s+'"))

(defn parse-progression [string]
  (->> (progression-parser string)
       (i/transform {:chordbar :bar})
       ))

(facts "about parse-progression"
  (fact "[] returns empty progression"
    (parse-progression "[]") => [:progression])
  (fact "[[C]] is one bar with one chord"
    (parse-progression "[[C]]") => [:progression [:bar [:chord "C"]]]))

;; FIXME more accurat chord definition
