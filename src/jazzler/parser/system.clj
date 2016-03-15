(ns jazzler.parser.system
  (:require [instaparse.core :as i]
            [jazzler.parser.parser :as p] 
            [jazzler.parser.transformer :as t]))

(def transformations
  {
   :bchord t/barchord
   :majorchord t/majorchord
   :minorchord t/minorchord
   :diminished t/diminished
   :augmented t/augmented
   :progression t/progression
   :figdef t/figdef
   :figsym t/figsym
   :title t/title
   :tempo t/tempo
   :structure t/structure
   :structureLine t/structure
   ;; :tempo-value t/tempo-value
   :scale t/scale
   :root t/root
   :quality t/quality
   :intervalnum t/intervalnum
   :chord t/chord
   :scale-value t/scale-value
   :bar t/bar})

(defn failure? 
  "Tests whether a parse result is a failure."
  [result]
  (i/failure? result))

(def valid? (complement failure?))

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
(def parse-scale-val (partial parse :scale-value))
;; (def parse-scale-val #(p/song-parser % :start :scale-value))

;; (def parse-all #(p/song-parser % :start :all))
;; (def parse-all #(partial parse :all))

(defn parse-all [s]
  (let [parse (p/song-parser s :start :all)]
    (if (failure? parse)
      parse
      (apply merge (t/transform transformations parse)))))
  ;; (->> s
       ;; (#(p/song-parser % :start :all))
       ;; (t/transform transformations)
       ;; (apply merge)))

(defn parse-song [s]
  (->> s
       (p/song-parser)
       (t/transform transformations)
       (apply merge)))

