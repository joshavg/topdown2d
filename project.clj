(defproject topdown2d "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
            
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [org.clojure/clojurescript "1.9.521"]
  ]
  
  :plugins [
    [lein-cljsbuild "1.1.7"]
    [lein-figwheel "0.5.14"]
  ]
  
  :cljsbuild {
    :builds [{
      :id "default"
      :source-paths ["src/cljs"]
      :figwheel true
      :compiler {
        :main topdown2d.core
        :output-to "resources/public/js/cljsbuild-main.js"
        :output-dir "resources/public/js/out"
        :asset-path "js/out"
        :pretty-print true
      }
    }]
  }
  
  :figwheel {
    :css-dirs ["resources/css"]
  }
  
  ;:hooks [leiningen.cljsbuild]
  
  :main ^:skip-aot topdown2d.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
