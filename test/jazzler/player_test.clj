(ns jazzler.player-test
  (:use midje.sweet)
  (:require [jazzler.player :refer :all]
            [jazzler.helper :as h]))

(facts "about play-song"
  (fact "it plays a figure"
    (play-song (h/song)) => ..blerg..
    (provided (play-progression anything anything anything) 
              => ..blerg..)))
