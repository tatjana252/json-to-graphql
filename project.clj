(defproject json-to-graphql "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.walmartlabs/lacinia "0.23.0"]
                 [com.walmartlabs/lacinia-pedestal "0.5.0"]
                 [cheshire "5.8.0"]
                 [org.clojure/core.async "0.4.474"]]
  :profiles {:dev {:dependencies [[midje/midje "1.9.1"]]}})
