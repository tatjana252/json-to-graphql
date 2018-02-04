(ns json-to-graphql.schema-map
    (:require
        [clojure.pprint :as pp]
        [json-to-graphql.object :as o]
        [json-to-graphql.input-object :as i]
        [map-util.map-util :as m]
        [clojure.core.reducers :as r]))

(defn objects [json-map]
    (->>
        json-map
        (r/map #(o/parse-object {%1 %2}))
        (r/fold m/cat-map)))

(defn input-objects [json-map]
    (->>
        json-map
        (r/map #(i/parse-input-object {%1 %2}))
        (r/fold m/cat-map)))

(defn non-null [str]
        (vary-meta (list str) assoc :non-null true))
