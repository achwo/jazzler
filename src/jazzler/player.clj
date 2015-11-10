(ns jazzler.player
  (:gen-class)
  (:use [overtone.live])
  (:require [jazzler.parsing :refer :all]
            [jazzler.overtone-format :refer :all]
            [overtone.inst.sampled-piano :refer :all]))
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

(defn play-song [{:keys [key bpm] :as song}]
  ;; take second in structure and use it as key
  ;; schedule playback, but add a delay of number of beats of first
  ;;   progression to time
  ;; etc..
  ;; song already needs to be in overtone format
  (let [prog (song->seq song)]
    (play-progression key bpm prog))
)
