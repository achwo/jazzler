(ns jazzler.player.overtone-format
  (:require [clojure.string :as s]
            [jazzler.song :as song]
            [overtone.music.pitch :refer [CHORD degrees->pitches note ] :as pitch]))

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
   :dominant7 10
   :maj7 11
   :b9 14
   :9 15
   :sharp9 16
   :11 17
   :sharp11 18})

(def major [2 2 1 2 2 2 1])

(def scales 
  {:major      [2 2 1 2 2 2 1]
   :minor      [2 1 2 2 1 2 2]
   :ionian     [2 2 1 2 2 2 1]
   :dorian     [2 1 2 2 2 1 2]
   :phrygian   [1 2 2 2 1 2 2]
   :lydian     [2 2 2 1 2 2 1]
   :mixolydian [2 2 1 2 2 1 2]
   :aeolian    [2 1 2 2 1 2 2]
   :locrian    [1 2 2 1 2 2 2]})

(defn- degree->int [degree]
  (get {:i 1
        :ii 2
        :iii 3
        :iv 4
        :v 5
        :vi 6
        :vii 7} degree))

(defn degree->pitch [mode degree]
  (apply + (take (dec (degree->int degree)) (get scales mode))))

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

;; (defn- degree->pitch [degree root triad]
  ;; (first (pitch/degrees->pitches [degree] triad root)))

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

(defn- kw->root [kw]
  (if-not (.endsWith (str (name kw)) "3")
    (keyword (str (name kw) 3))
    kw))

(defn- convert-key [{root :root mode :mode}]
  {:root (kw->root root) :mode mode})

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

(defn- chord->relative-notes 
  "{:root :C3 :quality :major :interval :7 :fifth :5}
=> [0 4 7 11]"
  [{:keys [root quality interval fifth] :as chord}]
  (let [qu (get qualities quality 4)
        in (get intervals interval 0)
        fi (get intervals fifth 7)]
    (distinct (concat qu [0 in fi]))))

(defn- relative-notes->notes 
  [rel_notes {:keys [root mode] :as key} chordroot]
  (let [rootnote (note root)]
    (map (partial + rootnote (degree->pitch mode chordroot)) rel_notes)))

(defn chord->notes [chord key]
  (->> chord
       (chord->relative-notes)
       (#(relative-notes->notes % key (:root chord)))))

(defn add-notes
  "Adds :notes to every chord"
  ([{:keys [key figures] :as song}]
   {:pre [(every? map? [key figures])]}
     (each-chord (comp 
                  (partial add-notes (convert-key key))
                  (partial merge (song/chord))) song))
  ([key chord]
   (assoc chord :notes (chord->notes chord key))))

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
    (take 4 elems)
    ;; (throw (Exception. "Bar is too long for quarters strategy."))
    ))

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
