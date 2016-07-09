(ns jazzler.parser.chord-parser
  (:require [instaparse.core :as i]
            [overtone.music.pitch :refer [CHORD degrees->pitches note]]
            [clojure.string :as s]))

;;;; PARSER 
(def chord-grammar
  (str 
   "chord = root quality? intervalnum? fifth? "
   "root = 'I' | 'II' | 'III' | 'IV' | 'V' |'VI' | 'VII' "
   "quality = '-' | '+' | 'o' "
   "intervalnum = six | seven | nine | eleven "
   "<six> = 'b'? '6' "
   "<seven> = '7' | 'maj7' "
   "<nine> = ('b' | '#')? '9' "
   "<eleven> = '#'? '11' "
   "fifth = '#5' | 'b5' | 'sus4' "
))


;;;; transformers

(defn root [number]
  {:root (keyword (s/lower-case number))})

(defn quality [str]
  {:quality (case str
              "-" :minor
              "+" :augmented
              "o" :diminished)})

(defn chord [& stuff] stuff)

(defn intervalnum [str]
  {:interval (case str
               "b6" :flat6
               "6" :6
               "7" :dominant7
               "maj7" :major7
               "b9" :flat9
               "9" :9
               "#9" :sharp9
               "11" :11
               "#11" :sharp11)})

(def chord-parser (i/parser chord-grammar))

(defn parse-chord [s] 
  (->> s
       (#(chord-parser %))
       (i/transform {:root root
                     :quality quality
                     :intervalnum intervalnum
                     :chord chord})
       (apply merge)))

(defn- get-root-notes [_]
  [0])

(defn- get-quality-notes [{quality :quality}]
  (case quality
    :minor [3 7]
    :major [4 7]
    :diminished [3 6]
    :augmented [4 8]))

(defn- get-interval-notes [{interval :interval q :quality}]
  (case interval
    :dominant7 [(- 10 (if (= q :diminished) 1 0))]
    :major7 [11]))

(defn- complete-chord [chord]
  (merge {:root nil :quality :major :interval nil :fifth nil} chord))

(defn get-relative-notes [chord]
  (let [complete-chord (complete-chord chord)]
    [(get-root-notes complete-chord)
     (get-quality-notes complete-chord)
     (get-interval-notes complete-chord)]))

(defn new-chord 
  []
  {:root :i 
   :quality :major
   :interval :7
   :fifth :5})

(def qualities
  {:minor [3 7]
   :major [4 7]
   :diminished [3 6]
   :augmented [4 8]})

(def intervals
  {:b5 6
   :5 7
   :sharp5 8
   :sus4 5
   :flat6 8
   :6 9
   :7 10
   :maj7 11
   :b9 14
   :9 15
   :sharp9 16
   :11 17
   :sharp11 18})

(defn root->note [root]
  (note root))

