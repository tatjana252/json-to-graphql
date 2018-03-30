(defproject json-to-graphql "0.1.0"
  :description "Clojure library for generating GraphQL schema from JSON."
  :url "https://github.com/tatjana252/json-to-graphql"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [cheshire "5.8.0"]]
  :profiles {:dev {:dependencies [[midje/midje "1.9.1"]]}}
  :plugins [[lein-codox "0.10.3"]]
  :codox {:output-path "docs"})


