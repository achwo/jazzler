(ns jazzler.parser.system
  (:require [instaparse.core :as i]
            [jazzler.parser.parser :as p] 
            [jazzler.parser.transformer :as t]))

(def transformations
  {:bchord t/barchord
   :majorchord t/majorchord
   :minorchord t/minorchord
   :diminished t/diminished
   :augmented t/augmented
   :progression t/progression
   :figdef t/figdef
   :figsym t/figsym
   :title t/title
   :structure t/structure
   :tempo-value t/tempo-value
   :bar t/bar})

(defn parse 
  "Parses the string s using startrule. Use m for custom transformations."
  ([startrule s] (parse transformations startrule s))
  ([m startrule s]
   (->> s
        (#(p/song-parser % :start startrule))
        (t/transform m))))

(def parse-progression (partial parse :progression))
(def parse-title (partial parse :title))
(def parse-structure (partial parse :structureContent))
(def parse-figdef (partial parse (dissoc transformations :figdef) :figdef))
(def parse-figsym (partial parse :figsym))

(def parse-title-val #(p/song-parser % :start :title-value))
(def parse-tempo-val #(p/song-parser % :start :tempo-value))

(defn parse-song [s]
  (->> s
       (p/song-parser)
       (t/transform transformations)
       (apply merge)))


(defn failure? 
  "Tests whether a parse result is a failure."
  [result]
  (i/failure? result))

(def valid? (complement failure?))
