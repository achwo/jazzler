(ns jazzler.overtone-format
  (:require [clojure.string :as s]
            [overtone.music.pitch :refer [CHORD degrees->pitches]]))

(defn- seq->map [[key value]]
  {key value})

(defn- seqs->map 
  [seq]
    (apply merge (map seq->map seq)))

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
  [degree {:keys [root triad]}]
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
  {:key {:root (string->root root) :triad triad}})

(defn add-chord-notes [key chord]
  (assoc chord :notes (degree->midi-chord (:chord chord) key)))

(defn bar-playback-information [key bar]
  (assoc bar :elements (map (partial add-chord-notes key) (:elements bar))))

(defn add-playback-information 
  "Adds playback information for overtone to the given song.
  (song) => song
  (key figures) => figures"
  ([{:keys [key figures] :as song}] 
   {:pre [(every? map? [key figures])]}
   (assoc song :figures (add-playback-information key figures)))
  ([key figures]
   (seqs->map
    (for [[figname bars] (seq figures)]
      [figname (map (partial bar-playback-information key) bars)]))))

