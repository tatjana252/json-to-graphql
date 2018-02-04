(ns json-to-graphql.object
    (:require [json-to-graphql.value :as v]
              [map-util.map-util :as m]
              [clojure.core.reducers :as r])
    (:import (clojure.lang MapEntry)))

(defn id? [[k v]]
    (and (v/scalar-type v)
         (clojure.string/ends-with? k "id")))

(defn object-name [key]
    (when (keyword? key)
        (keyword (str (clojure.string/upper-case
                          (subs (name key) 0 1))
                      (subs (name key) 1)))))

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

(defn parse-field [field name-fn]
    (let [k (key field)]
        (if (id? field)
            {k 'ID}
            (when-let [type (v/field-value field name-fn)]
                {k type}))))

(defn parse-type
    [name-fn object]
    (->> (val object)
         clean
         (r/map #(parse-field (MapEntry. %1 %2) name-fn))
         (r/fold m/cat-map)
         (MapEntry. (name-fn (key object)))))

(defn add-objects [fields objects]
    ^:private
    (->> fields
         (filter #(:type (meta (val %))))
         (into objects)))

(def object-schema
    (comp (partial parse-type object-name)
          m/get-nested-object))

(defn parse-object
    ([objects]
        (parse-object objects object-schema))

    ([objects schema-fn]
        (loop [[[k v] & rest] objects
               result {}]
            (if k
                (let [object (schema-fn objects k)]
                    (recur (add-objects (val object) rest)
                           (merge result object)))
                result))))