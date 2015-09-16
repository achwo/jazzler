(ns jazzler.core-test
  (:use midje.sweet)
  (:require [jazzler.core :refer :all]))

(fact "This test fails."
      (= 0 1) => true)
