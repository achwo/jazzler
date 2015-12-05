(ns jazzler.song)

;; TODO: put song constructers and changers here

(defn- song []
  ;; this is incomplete, only for documentation
  {:key {:root :C3 :triad :major}
   :bpm 80
   :structure [:progression]})

(defn title 
  "When used without string argument, it returns the current title.
  When provided, it sets the title to the given string."
  ([song]
   (:title song))
  ([song s]
   (assoc song :title s)))

(defn progression
  ([song]
   (:progression song))
  ([song s]
   (assoc song :progression s)))
