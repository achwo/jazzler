(ns jazzler.song
  (require [clojure.string :as str]
           [clojure.set :as set]))

;; TODO: put song constructers and changers here

;;;;; DEFAULTS

(def default-title "Unnamed song")
(def default-composer "Unknown")
(def default-tempo 120)
(def default-chord {:root :i
            :quality :major
            :interval nil
            :fifth :5})
(def default-key {:root :C3 :mode :major})

;;;; CONSTRUCTORS

(defn chord 
  "Usage: (chord :chord :i :beat 1) => chord"
  [& {:as keys}]
  (merge default-chord keys))

(defn mode [& {:keys [root mode]}]
  (merge default-key keys))

(defn song 
  "Returns a song with fields :bpm, key, :figures and :structure.
  If k-v pairs are given as params, they are added to the song and 
  might override the defaults.
  Usage: 
  (song) => default song (see example below)
  (song :bpm 120 
        :key ...)"
  [& {:as keys}]
  (let [default {:bpm default-tempo
                 :key default-key
                 :title default-title
                 :author default-composer}]
    (merge default keys)))


(defn merge-songs 
  "Merge the two songs with values of song1 being overridden."
  [song1 song2]
  (merge-with set/union song1 song2))

(defn- missing-figs 
  "Lists all figure names, that are used in structure, but do not exist."
  [song]
  (let [existing (set (keys (:figures song)))
        used (set (:structure song))
        missing (set/difference used existing)]
    (seq missing)))

(defn check
  "Checks the song for semantic errors.
  If there is an error, a list of error strings is returned.
  Else the result is nil"
  [song]
  (let [miss-figs (missing-figs song)]
    (if (empty? miss-figs)
      []
      [(str "The following figures where used, but do not exist: " miss-figs)])))

(defn title 
  "When used without string argument, it returns the current title.
  When provided, it sets the title to the given string."
  ([song]
   (:title song))
  ([song s]
   (assoc song :title s)))

(defn structure
  ([song]
   (:structure song))
  ([song seq]
   (assoc song :structure seq)))

(defn figure
  ([song s]
   (get (:figures song) s))
  ([song k v]
   (assoc-in song [:figures k] v)))

(defn tempo
  ([song]
   (:bpm song))
  ([song i]
   (assoc song :bpm i)))

(defn scale
  ([song]
   (:scale song))
  ([song {root :root mode :mode}]
   (assoc song :scale {:root root :mode mode})))

(defn string->root
  "The given string is expected to be a valid root note."
  [s] 
  (keyword (apply str (str/upper-case (first s)) (rest s))))

(def scales
  [:major :minor :ionian :aeolian :phrygian :lydian :dorian :mixolydian :locrian])

(defn string->mode
  ([s]
   (let [kw (keyword (str/lower-case s))]
     (when (some #{kw} scales)
       kw))))

(defn string->scale
  [s]
  (let [[root-str mode-str] (str/split s #"\s" 2)
        root (string->root root-str)
        mode (string->mode mode-str)]
    {:root root :mode mode}))
