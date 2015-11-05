(ns jazzler.overtone-format-test
  (:use midje.sweet)
  (:require [jazzler.overtone-format :refer :all]
            [overtone.music.pitch :as o]))

(def bpm120 (partial set-bpm 120))

(defn chord 
  "Quick construction for chords. Customize by adding keys as params.
  Defaults: :chord :i, :triad :major, :beat 1, :duration 1
  Usage: (chord :chord :i :beat 1) => chord"
  [& {:keys [chord triad beat duration]
      :or {chord :i triad :major beat 1 duration 1}}]
  {:chord chord :triad triad :beat beat :duration duration})

(defn mode [& {:keys [root triad] :or {root :C3 triad :major}}]
  {:root root :triad triad})

(facts "about set-bpm"
  (fact "it adds a given bpm to a new bpm field to a chord"
    (set-bpm ..bpm.. {:chord ..chord.. :triad ..triad..}) 
    => {:chord ..chord.., :triad ..triad.., :bpm ..bpm..}
    (bpm120 {}) => {:bpm 120})
  (fact "it changes an already existing bpm field"
    (bpm120 {:bpm 100}) => {:bpm 120}))

(facts "about set-offset"
  (fact "it adds a new :offset field"
    (set-offset 1/2 {}) => {:offset 1/2}))

(facts "about degree->midi-chord"
  (fact "it returns the chord as list of midi notes"
    (degree->midi-chord :i (mode)) => '(48 55 52)
    (degree->midi-chord :vii (mode)) => '(59 66 63)
    (degree->midi-chord :i (mode :root :G3)) => '(55 62 59))
  (fact "it only works with keywords"
    (degree->midi-chord "I" (mode)) 
    => (throws AssertionError)))

(facts "about string->root"
  (fact "it converts the root string into a keyword in octave 3"
    (string->root "C") => :C3)
  (fact "it works with lower-case string"
    (string->root "d") => :D3)
  (fact "it only takes string"
    (string->root :C) => (throws AssertionError)))

(facts "about convert-progression"
  (fact "it changes the key format"
    (convert-progression {:key ["C" :major]})
    => {:key (mode)})
  (fact "it requires a complete progression map"
    (convert-progression {}) => (throws AssertionError)))

(facts "about add-chord-notes"
  (fact "it adds a note field to chords"
    (add-chord-notes (mode) (chord)) 
    => (assoc (chord) :notes [48 55 52])))

(facts "about bar-playback-information"
  (fact "it adds note information to any amount of chords"
    (bar-playback-information ..key.. {:elements [..in1.. ..in2..]})
    => {:elements [..out1.. ..out2..]}
    (provided (add-chord-notes ..key.. ..in1..) => ..out1..)
    (provided (add-chord-notes ..key.. ..in2..) => ..out2..)))

(def input-prog 
  [{:bar 1 
    :elements [(chord :duration 1/2)
               (chord :chord :ii :triad :minor :beat 3 :duration 1/2)]}
   {:bar 2 :elements [(chord :chord :iii :triad :minor)]}])

(def input-song 
  {:title "Wurstbrot I"
   :author "Edgar der Vierte"
   :bpm 120
   :key (mode)
   :figures {"Intro" input-prog}
   :structure ["Intro"]})


(def output-prog 
  [{:bar 1 
    :elements [(assoc (chord :duration 1/2) :notes [48 55 52])
               (assoc (chord :chord :ii :triad :minor
                             :beat 3 :duration 1/2) :notes [50 57 54])]}
   {:bar 2 :elements [(assoc (chord :chord :iii :triad :minor) 
                             :notes [52 59 56])]}])

(def output-song (assoc input-song :figures {"Intro" output-prog}))

(def better-output-song {:bpm 120 
                  :progression output-prog})

(fact "add-pb-inf for multiple figures in song"
  (add-playback-information {:key (mode)
                             :figures {"Intro" input-prog
                                       "Outro" input-prog}})
  => {:key (mode)
      :figures {"Intro" output-prog
                "Outro" output-prog}})

(fact "integration test"
  (add-playback-information input-song) => output-song)

;; TODO process the structure, map the figure progressions and add 
;;      :progression field to song
;; TODO in player.clj, make function to playback the resulting song
;; TODO figures in chord-scope and in song-scope: maybe another name?
;; TODO key (music lingo) and key (map) are ambiguous... solution?
;; TODO remove overtone autoloading on autotest!
;; TODO key => mode? need to research
;; TODO chord: change :chord to :degree
