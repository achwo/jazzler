(ns jazzler.player.player
  (:gen-class)
  (:use [overtone.live])
  (:require [jazzler.parser.system :refer :all]
            [jazzler.player.overtone-format :refer :all]
            [jazzler.song :as s]
            [overtone.inst.sampled-piano :refer :all]))

;; (use 'overtone.core)
;; (connect-external-server)

(def piano sampled-piano)

(defn play-note [metronome beat duration note]
  (dorun
   (at (metronome beat) (piano note))
   (at (- (metronome (+ (* 4 duration) beat)) 10) (kill piano))))

(defn play-chord [metronome key bar {:keys [chord duration beat notes]}]
  (let [playback-beat (+ beat (* 4 (dec bar)))]
    (dorun (map (partial play-note 
                         metronome 
                         playback-beat 
                         duration) notes))))

(defn play-bar [metronome key {:keys [bar elements]}]
  (dorun (map (partial play-chord metronome key bar) elements)))

(defn play-progression [key bpm bars]
  (dorun (map (partial play-bar (metronome bpm) key) bars)))

(defn play-song [song]
  ;; take second in structure and use it as key
  ;; schedule playback, but add a delay of number of beats of first
  ;;   progression to time
  ;; etc..
  ;; song already needs to be in overtone format
  (let [
        full-song (merge (s/song) song)
        key (:key full-song)
        bpm (:bpm full-song)
        with-notes (add-notes full-song)
        with-rhythm (apply-rhythm quarters with-notes)
        prog (song->seq with-rhythm)]
    (when-not (nil? (:structure full-song))
      (println with-notes)
      (play-progression key bpm prog))))
