(ns jazzler.overtone-format-test
  (:use midje.sweet)
  (:require [jazzler.overtone-format :refer :all]
            [jazzler.helper :refer [chord mode] :as h]
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
    (convert-key {:key ["C" :major]})
    => {:key (mode)})
  (fact "it requires a complete progression map"
    (convert-key {}) => (throws AssertionError)))

(facts "about add-notes"
  (fact "it adds a note field to chords"
    (add-notes (mode) (chord)) 
    => (chord :notes [48 55 52])))

(def input-prog 
  [{:bar 1 
    :elements [(chord :duration 1/2)
               (chord :chord :ii :triad :minor :beat 3 :duration 1/2)]}
   {:bar 2 :elements [(chord :chord :iii :triad :minor)]}])

(def output-prog 
  [{:bar 1 :elements [(chord :duration 1/2  :notes [48 55 52])
                      (chord :chord :ii :triad :minor :beat 3 
                             :duration 1/2 :notes [50 57 54])]}
   {:bar 2 :elements [(chord :chord :iii :triad :minor 
                                     :notes [52 59 56])]}])

(fact "add-notes adds notes to every chord"
  (add-notes
   {:key (mode) :figures {"Intro" input-prog "Outro" input-prog}})
  => {:key (mode) :figures {"Intro" output-prog "Outro" output-prog}})

(def input-song 
  {:title "Wurstbrot I"
   :author "Edgar der Vierte"
   :bpm 120
   :key (mode)
   :figures {"Intro" input-prog}
   :structure ["Intro"]})

(def output-song (assoc input-song :figures {"Intro" output-prog}))

(fact "integration test"
  (add-notes input-song) => output-song)

(facts "about song->seq"
  (facts "it returns a seq from a song with"
    (fact "one chord"
      (song->seq (h/song)) => (h/prog 1 1))
    (fact "two chords"
      (song->seq (h/song :figures {:in (h/prog 1 2)})) => (h/prog 1 2))
    (fact "more than one bar"
      (song->seq (h/song :figures {:in (h/prog 2 1)})) => (h/prog 2 1))
    (fact "more than one element in structure"
      (song->seq (h/song :structure [:in :in])) => (h/prog 2 1))
    (fact "more complex structure"
      (song->seq (h/song 
                  :structure [:in :in :out]
                  :figures {:in (h/prog 1 2)
                            :out (h/prog 2 2)})) => (h/prog 4 2))))

(facts "about count-up-bars"
  (fact "it numbers bars consecutively"
    (#'jazzler.overtone-format/count-up-bars [{:bar 1} {:bar 1}]) 
    => [{:bar 1} {:bar 2}]))

(facts "about each-chord"
  (fact "it takes a song and a function"
    (each-chord identity (h/song)))

  (fact "it adds a field to every chord in every figure"
    (each-chord #(assoc % :test 1)
                {:figures {:i [{:elements [{} {}]}]
                           :j [{:elements [{} {}]}]}}) 
    => {:figures {:i [{:elements [{:test 1} {:test 1}]}]
                  :j [{:elements [{:test 1} {:test 1}]}]}}))

(facts "about each-figure"
  (fact "it edits one figure"
    (each-figure (fn [_] :roflcopter) {:figures {:i [:wurstbrot]}})
    => {:figures {:i :roflcopter}})
  (fact "it edits several figures"
    (each-figure (fn [_] :done) {:figures {:i [:wurstbrot] :ii 2}})
    => {:figures {:i :done :ii :done}})
  (fact "it can change stuff in a meaningful way"
    (each-figure 
     #(map quarters %)
     {:figures {:fig [{:elements [{}]}]}})
    => {:figures {:fig [{:elements [{:beat 1 :duration 1/4}
                                    {:beat 2 :duration 1/4}
                                    {:beat 3 :duration 1/4}
                                    {:beat 4 :duration 1/4}]}]}}))

(facts "about each-bar"
  (fact "it applies the function on the bars of one figure"
    (each-bar (fn [_] :done) [{} {}])
    => [:done :done])
  (fact "quarters"
    (each-bar quarters [{:bar 1 :elements [(h/chord)]}])
    => [{:bar 1 :elements [(h/chord :beat 1 :duration 1/4)
                           (h/chord :beat 2 :duration 1/4)
                           (h/chord :beat 3 :duration 1/4)
                           (h/chord :beat 4 :duration 1/4)]}]))

(facts "about quarter rhythm strategy"
  (fact "a single chord is translated to 4 beats of this chord"
    (quarters {:elements [(h/chord)]})
    => {:elements [(h/chord :beat 1 :duration 1/4)
                   (h/chord :beat 2 :duration 1/4)
                   (h/chord :beat 3 :duration 1/4)
                   (h/chord :beat 4 :duration 1/4)]})
  (fact "two chords are translated to beats of each chord"
    (quarters {:elements [(h/chord :chord :i)
                          (h/chord :chord :ii)]}) 
    => {:elements [(h/chord :chord :i :beat 1 :duration 1/4)
                   (h/chord :chord :i :beat 2 :duration 1/4)
                   (h/chord :chord :ii :beat 3 :duration 1/4)
                   (h/chord :chord :ii :beat 4 :duration 1/4)]})
  (fact "in case of three chords, expand first to be repeated"
    (quarters {:elements [(h/chord :chord :i)
                          (h/chord :chord :ii)
                          (h/chord :chord :iii)]})
    => {:elements [(h/chord :chord :i :beat 1 :duration 1/4)
                   (h/chord :chord :i :beat 2 :duration 1/4)
                   (h/chord :chord :ii :beat 3 :duration 1/4)
                   (h/chord :chord :iii :beat 4 :duration 1/4)]})
  (fact "four chords equal lengths"
    (quarters {:elements (repeat 4 (h/chord))}) 
    => {:elements [(h/chord :beat 1 :duration 1/4)
                   (h/chord :beat 2 :duration 1/4)
                   (h/chord :beat 3 :duration 1/4)
                   (h/chord :beat 4 :duration 1/4)]}))
(fact "apply-rhythm works for one bar"
  (apply-rhythm quarters (h/song))
  => {:bpm 120, 
       :figures 
      {:in [{:bar 1 
             :elements [{:beat 1, :chord :i, :duration 1/4 
                         :notes [48 55 52], :triad :major}
                        {:beat 2, :chord :i, :duration 1/4
                         :notes [48 55 52], :triad :major}
                        {:beat 3, :chord :i, :duration 1/4 
                         :notes [48 55 52], :triad :major}
                        {:beat 4, :chord :i, :duration 1/4
                         :notes [48 55 52], :triad :major}]}]} 
      :key {:root :C3, :mode :major}, :structure [:in]}) 
