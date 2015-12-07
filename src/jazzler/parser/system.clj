(ns jazzler.parser.system
  (:require [instaparse.core :as i]
            [jazzler.parser.parser :as p] 
            [jazzler.parser.transformer :as t]))

(def transformations
  {:progression t/progression
   :bchord t/barchord
   :majorchord t/majorchord
   :minorchord t/minorchord
   :diminished t/diminished
   :augmented t/augmented
   :title t/title
   :bar t/bar})

(defn parse [startrule s]
  (->> s
       (#(p/song-parser % :start startrule))
       (t/transform transformations)))

(def parse-progression (partial parse :progression))
(def parse-title (partial parse :title))

(defn parse-song [s]
  (->> s
       (p/song-parser)
       (t/transform transformations)
       (apply merge)))

;; TODO: it would be really nice to have an exception if this fails on use
(def parse-title-val #(p/song-parser % :start :title-value))

(defn failure? 
  "Tests whether a parse result is a failure."
  [result]
  (i/failure? result))
