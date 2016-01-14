(ns jazzler.song
  (require [clojure.string :as str]))

;; TODO: put song constructers and changers here

(defn song [] {})

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

(defn chord
  ([root triad]
   {:chord root :triad triad}))

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
  [:major :minor :ionian :aeolian])

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
