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

(defn query-name [nm args]
    (reduce #(str %1 "_" (name %2)) (str nm "_by") (keys args)))

(defn query [name fields objects ]
    (if fields
        (let [args (m/select-keys* objects fields)]
            {name {:name (query-name name args)
                   :args (into {} args)}})
        {}))

(defn mutation [nm]
    {nm {:name (str "add_" nm)
         :args (symbol (name (i/input-object-name nm)))}})

(defn schema-map [json-map ops name]
    (let [objects (future (objects json-map))
          input-objects (future (input-objects json-map))
          query (future (query name (:query ops) @objects))
          mutations (future (mutation name))]
        {:objects @objects
         :input-objects @input-objects
         :queries @query
         :mutations @mutations}) )

