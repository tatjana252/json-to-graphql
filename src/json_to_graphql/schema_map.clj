(ns json-to-graphql.schema-map
    (:require
        [json-to-graphql.object :refer [parse-object object-name]]
        [json-to-graphql.input-object :refer [parse-input-object input-object-name]]
        [map-util.map-util :refer [cat-map select-keys*]]
        [clojure.core.reducers :refer [map fold]])
    (:import (java.util.concurrent ExecutionException)))

(defn objects [json-map]
    "Parses all objects in provided `json-map`."
    (->>
        json-map
        (map #(parse-object {%1 %2}))
        (fold cat-map)))

(defn input-objects [json-map]
    (->>
        json-map
        (map #(parse-input-object {%1 %2}))
        (fold cat-map)))

(defn query-name [nm args]
    (reduce #(str %1 "_" (name %2)) (str nm "_by") (keys args)))

(defn query [fields objects]
    (let [args (select-keys* objects fields)
          return (first fields)]
        (if (and (contains? objects (object-name return)) (not-empty args))
            {(name (object-name return)) {:name (query-name (name return) args)
                                          :args (into {} args)}}
            {})))

(defn mutation
    "Add's mutation map for object `o` to a existing map `m`."
    [m o]
    (assoc m (name (object-name o)) {:name (str "add_" (name o))
                                     :args (symbol (name (input-object-name (name o))))}))


(defn schema-map [json-map ops]
    (let [objects (future (objects json-map))
          input-objects (future (input-objects json-map))
          query (future (query (:query ops) @objects))
          mutations (future (reduce mutation {} (keys json-map)))]
        (try
            {:objects       @objects
             :input-objects @input-objects
             :queries       @query
             :mutations     @mutations}
            (catch ExecutionException e (throw (Exception. "The provided map isn't object type."))))))

