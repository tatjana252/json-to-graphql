(ns graphql-schema.schema-map
    (:require
        [clojure.pprint :as pp]
        [graphql-schema.object :as o]
        [map-utils.map-utils :as m]
        [clojure.core.reducers :as r]))

(defn add-objects [fields objects]
    ^:private
    (->> fields
         (filter #(:type (meta (val %))))
         (into objects)))

(def object-schema
    (comp o/object-type
          m/get-nested-object))

(defn parse-object
    [objects]
    (loop [[[k v] & rest] objects
           result {}]
        (if k
            (let [object (object-schema objects k)]
                (recur (add-objects (val object) rest)
                       (merge result object)))
            result)))

(defn object-types [objs]
    (->>
        objs
        (r/map #(parse-object {%1 %2}))
        (r/fold m/cat-map)))

(defn non-null [str]
        (vary-meta (list str) assoc :non-null true))

