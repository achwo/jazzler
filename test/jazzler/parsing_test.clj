(ns jazzler.parsing-test
  (:use midje.sweet)
  (:require [jazzler.parsing :refer :all]
            [instaparse.core :as i]))

(facts "about parse progression"
  (fact "[] returns empty progression"
    (parse-progression "[]") => [:progression '()])
  (fact "[[I]] is one bar with one chord"
    (parse-progression "[[I]]") 
    => [:progression (list [:bar (list "I")])])
  (fact "[I] is one bar of I"
    (parse-progression "[I]") 
    => [:progression (list [:bar (list "I")])])
  (fact "[[I IV]] is one bar of two chords"
    (parse-progression "[[I IV]]") 
    => [:progression (list [:bar (list "I" "IV")])])
  (fact "[[I] [IV]] is one bar of each chord"
    (parse-progression "[[I] [IV]]") 
    => [:progression (list [:bar (list "I")]
                           [:bar (list "IV")])])
  (fact "[I IV] is one bar of each chord"
               (parse-progression "[I IV]") 
               => [:progression (list [:bar (list "I")]
                                      [:bar (list "IV")])])
  
)
