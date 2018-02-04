(ns graphql-schema.object
    (:require [graphql-schema.value :as v]
              [map-utils.map-utils :as m]
              [clojure.core.reducers :as r])
    (:import (clojure.lang MapEntry)))

(defn id? [[k v]]
    (and (v/scalar-type v)
         (clojure.string/ends-with? k "id")))

(defn parse-field [field]
    (let [k (key field)]
        (if (id? field)
            {k 'ID}
            (when-let [type (v/field-value field)]
                {k type}))))

(defn non-null [str]
    (with-meta (list str) {:non-null true}))

(defn clean [object]
    (loop [result object]
        (if (or (list? result) (vector? result))
            (recur (first result))
            result)))

(defn alter-modifiers [modifiers value]
    (reduce #(case %2
                 :list [%1]
                 :non-null (non-null %1)
                 nil %1) (clean value) modifiers))

(defn parse-type
    [object]
    (->> object
         clean
         (r/map #(parse-field (MapEntry. %1 %2)))
         (r/fold m/cat-map)))

(defn object-type
    [object]
    (MapEntry. (v/object-name (key object))
               (parse-type (val object))))


