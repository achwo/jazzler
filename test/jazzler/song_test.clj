(ns jazzler.song-test
  (:use midje.sweet)
  (:require [jazzler.song :refer :all]))

(facts "about figure"
  (fact "it returns the figure value"
    (figure {:figures {"fig" 1}} "fig") => 1)
  (fact "it returns nil, if figure does not exist"
    (figure {:figure {}} "fig") => nil)
  (fact "it returns nil, if no figure exists"
    (figure {} "fig") => nil))
