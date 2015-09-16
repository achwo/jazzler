(ns jazzler.core-test
  (:use midje.sweet)
  (:require [jazzler.core :refer :all]))

(fact "This test does not fail."
      (= 1 1) => true)
