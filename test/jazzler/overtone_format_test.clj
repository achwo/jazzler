(ns jazzler.overtone-format-test
  (:use midje.sweet)
  (:require [jazzler.overtone-format :refer :all]
            [overtone.music.pitch :as o]))

(def bpm120 (partial set-bpm 120))

(facts "about set-bpm"
  (fact "it adds a given bpm to a new bpm field to a chord"
    (set-bpm ..bpm.. {:chord ..chord.. :triad ..triad..}) 
    => {:chord ..chord.., :triad ..triad.., :bpm ..bpm..}
    (bpm120 {}) => {:bpm 120})
  (fact "it changes an already existing bpm field"
    (bpm120 {:bpm 100}) => {:bpm 120}))

(facts "about set-offset"
  (fact "It adds a new :offset field"
    (set-offset 1/2 {}) => {:offset 1/2}))

(facts "about degree->midi-chord"
  (fact "it returns the chord for the given degree in the given key as list of midi notes"
    (degree->midi-chord :i [:C3 :major]) => '(48 55 52)
    (degree->midi-chord :vii [:C3 :major]) => '(59 66 63)
    (degree->midi-chord :i [:G3 :major]) => '(55 62 59))
  (fact "it only works with keywords"
    (degree->midi-chord "I" [:C3 :major]) => (throws AssertionError)))

(facts "about string->root"
  (fact "it converts the root string into a keyword in octave 3"
    (string->root "C") => :C3)
  (fact "it works with lower-case string"
    (string->root "d") => :D3)
  (fact "it only takes string"
    (string->root :C) => (throws AssertionError)))

(facts "about convert-progression"
  (fact "it changes the key format"
    (convert-progression {:key ["C" :major]}) => {:key [:C3 :major]})
  (fact "it requires a complete progression map"
    (convert-progression {}) => (throws AssertionError)))

(defn add-chord-notes [chord]
  (assoc chord :notes (degree->midi-chord (:chord chord) (:key chord))))

(facts "about add-chord-notes"
  (fact "it adds a note field to chords"
    (add-chord-notes {:chord :i :key [:C3 :major]}) 
    => {:chord :i :key [:C3 :major] :notes [48 55 52]}))

(defn bar-playback-information [bar]
  (map add-chord-notes bar))

(facts "about bar-playback-information"
  (fact "it adds note information to any amount of chords"
    (bar-playback-information [..in1.. ..in2..]) => [..out1.. ..out2..]
    (provided (add-chord-notes ..in1..) => ..out1..)
    (provided (add-chord-notes ..in2..) => ..out2..)))

(defn add-playback-information [prog]
  (map bar-playback-information prog))

(facts "about add-playback-information"
  (fact "it works for more than one bar"
      (add-playback-information [..in1.. ..in2..]) => [..out1.. ..out2..]
      (provided 
       (bar-playback-information ..in1..) => ..out1..
       (bar-playback-information ..in2..) => ..out2..)))


(def input-prog 
  {:progression 
   [{:bar 1, :figures [{:chord :i, :triad :major
                        :beat 1, :duration 1/2}
                       {:chord :ii :triad :minor
                        :beat 3, :duration 1/2}]}
    {:bar 2, :figures [{:chord :iii, :triad :minor
                        :beat 1, :duration 1}]}]})

(def input-song 
  {:title "Wurstbrot I"
   :author "Edgar der Vierte"
   :bpm 120
   :key {:root :C3, :triad :major}
   :figures {"Intro" {:progression input-prog}}
   :structure ["Intro"]})

(def output-bar1 {:bar 1 :figures [{:notes [1 2 3] :beat 1 :duration 1/2
                      :chord :i :triad :major}
                     {:notes [4 5 6] :beat 3 :duration 1/2
                      :chord :ii :triad :minor}]})

(def output-bar2 {:bar 2 :figures [{:notes [7 8 9] :beat 1 :duration 1
                      :chord :iii :triad :minor}]})

(def output-prog [output-bar1 output-bar2])

(def output-song {:bpm 120 :progression output-prog})
