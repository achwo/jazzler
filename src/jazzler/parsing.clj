(ns jazzler.parsing
  (:gen-class)
  (:require [instaparse.core :as i]))

(defn progression 
  [& content]
  (cond
    (seq? content) [:progression content]
    (nil? content) [:progression '()]
    :else [:progression (list content)];unused
))

(defn bar [& content]
  [:bar content])

(defn barchord [content]
  [:bar (list content)])

(def progression-parser
  (i/parser 
   (str
    "progression = <'['>barOrChord? (<ws> barOrChord)* <']'> "
    "<barOrChord> = bar | bchord "
    "bar = <'['> chord (<ws> chord)* <']'> "
    "bchord = chord "
    "chord = 'I' | 'II' | 'III' | 'IV' | 'V' |'VI' | 'VII' "
    "ws = #'\\s+'")))

(def transformations
  {:progression progression
   :bchord barchord
   :chord str
   :bar bar})

(defn parse-progression [string]
  (->> string
       (progression-parser)
       (i/transform transformations)))

