(ns jazzler.overtone-format
  (:require [clojure.string :as s]
            [overtone.music.pitch :refer [CHORD degrees->pitches] :as pitch]))

(defn- seq->map [[key value]]
  {key value})

(defn- seqs->map 
  "Turns a seq of k-v-seqs into a map
  Example: ([[:key :val][:k :v]]) => {:kel :val, :k :v}"
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
  (first (pitch/degrees->pitches [degree] triad root)))

(defn- degree->midi-chord 
  "Returns a list of midi notes for the given degree in the given key.
  Example: (:i {:root :C3 :mode :major}) => (48 55 52)"
  [degree {:keys [root mode]}]
  {:pre [(keyword? degree)]}
  (map + 
       (mode CHORD)
       (repeat (degree->pitch degree root mode))))

(defn- string->root 
  "Converts a string root into a keyword in octave 3
  Example: \"c\" => :C3"
  [s]
  {:pre [(string? s)]}
  (keyword (str (s/upper-case s) 3)))

(defn- each-chord- [f s]
  (for [[figname bars] s]
    [figname (map #(assoc % :elements (map f (:elements %))) bars)]))

(defn- each-chord
  "Apply function f to each chord in song."
  [f {:keys [figures] :as song}]
  (let [result (seqs->map (each-chord- f (seq figures)))]
    (assoc song :figures result)))

(defn- each-bar [f prog]
  (map f prog))

(defn- each-figure [f {figs :figures :as song}]
  (assoc song :figures (seqs->map (for [[figname prog] (seq figs)]
                                    [figname (f prog)]))))

(defn- add-key 
  "Add a field to a chord."
[chord key])

(defn- convert-key [{[root triad] :key}]
  {:key {:root (string->root root) :mode triad}})

(defn add-notes
  "Adds :notes to every chord"
  ([{:keys [key figures] :as song}]
   {:pre [(every? map? [key figures])]}
   (each-chord (partial add-notes key) song))
  ([key chord]
   (assoc chord :notes (degree->midi-chord (:chord chord) key))))

(defn- count-up-bars [seq]
  (map #(assoc-in %1 [:bar] %2) seq (iterate inc 1)))

(defn song->seq 
  "Transforms a song to a sequence of bars."
  [{:keys [figures structure]}]
  (let [sequence (apply concat (map figures structure))]
    (count-up-bars sequence)))

(defn- quarters-seq [elems]
  (case (count elems)
    1 (repeat 4 (first elems))
    2 (concat (repeat 2 (first elems)) (repeat 2 (second elems)))
    3 (concat (repeat 2 (first elems)) (rest elems))
    4 elems
    (throw (Exception. "Bar is too long for quarters strategy."))))

(defn quarters 
  "A strategy for rhythm. It transforms the given rhythmless bars
  to bars of quarter notes. Only works for bars with 4 our less elements.
  Usage: (bar) => bar"
[{elems :elements :as bar}]
{:pre (seq? elems)}
  (letfn 
      [(assoc-chord [chord beat] (assoc chord :duration 1/4 :beat beat))
       (map-chords [seq] (map assoc-chord seq (iterate inc 1)))]
    (assoc bar :elements (map-chords (quarters-seq elems)))))

(defn apply-rhythm 
  "Applies rhythmic strategy function f to the song."
  [f song]
  (each-figure #(each-bar f %) song))
