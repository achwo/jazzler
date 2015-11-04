(ns jazzler.overtone-format
  (:require [clojure.string :as s]
            [overtone.music.pitch :refer [CHORD degrees->pitches]]))

(defn set-bpm 
  "Adds a bpm field to the given map."  
  [bpm m]
  (assoc m :bpm bpm))

(defn set-offset 
  "Adds an offset field to the given map."
  [offset chord]
  (assoc chord :offset offset))

(defn- degree->pitch [degree root triad]
  (first (degrees->pitches [degree] triad root)))

(defn degree->midi-chord 
  "Returns a list of midi notes for the given degree in the given key.
  Example: (:i [:C3 :major]) => (48 55 52)"
  [degree [root triad]]
  {:pre [(keyword? degree)]}
  (map + 
       (triad CHORD)
       (repeat (degree->pitch degree root triad))))

(defn string->root 
  "Converts a string root into a keyword in octave 3
  Example: \"c\" => :C3"
  [s]
  {:pre [(string? s)]}
  (keyword (str (s/upper-case s) 3)))

(defn convert-progression [{[root triad] :key}]
  {:key [(string->root root) triad]})
