(ns jazzler.helper)

(defn chord 
  "Quick construction for chords. Customize by adding keys as params.
  Defaults: :chord :i, :triad :major, :beat 1, :duration 1
  Usage: (chord :chord :i :beat 1) => chord"
  [& {:as keys}]
  (let [default {:chord :i :triad :major :beat 1 :duration 1}]
    (merge default keys)))

(defn mode [& {:keys [root triad] :or {root :C3 triad :major}}]
  {:root root :triad triad})

(defn bar 
  "Returns a bar with 'n-chords' amount of chords and bar number 'number'
  FIXME: very simplified beat handling, not for production!"
  [n-chords number]
  {:bar number
   :elements (map #(chord :duration (/ 1 n-chords)
                            :notes [48 55 52]
                            :beat %) (range 1 (inc n-chords)))})

(defn prog 
  "Returns a progression with n bars and m chords in each bar."
  [n m]
  (map (partial bar m) (range 1 (inc n))))

(defn song 
  "Returns a song with fields :bpm, key, :figures and :structure.
  If k-v pairs are given as params, they are added to the song and 
  might override the defaults.
  Usage: 
  (song) => default song (see example below)
  (song :bpm 120 
        :key (mode) 
        :figures {:in (prog 1 1)} 
        :structure [:in])"
  [& {:as keys}]
  (let [default {:bpm 120 
                 :key (mode)
                 :figures {:in (prog 1 1)}
                 :structure [:in]}]
    (merge default keys)))
