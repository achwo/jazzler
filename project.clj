(defproject jazzler "0.1.0-SNAPSHOT"
            :description "a language for jazz music composition"
            :url "https://github.com/engineduck/jazzler"
            :license {:name "GNU General Public License, V3.0"
                      :url "http://www.gnu.org/licenses/gpl.html"}
            :dependencies [[org.clojure/clojure "1.7.0"]
                           [instaparse "1.4.1"]
                           [quantisan/overtone "0.10-SNAPSHOT"]]
            :main ^:skip-aot jazzler.repl
            :target-path "target/%s"
            :profiles {:uberjar {:aot :all}
                       :dev {:source-paths ["dev"]
                             :dependencies [[midje "1.8.2"]
                                            [rhizome "0.2.5"]]
                             :plugins [[lein-midje "3.2"]]
                             }})
