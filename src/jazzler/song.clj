(ns jazzler.song)

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
