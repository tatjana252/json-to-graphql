(ns json-to-graphql.object
    (:require [json-to-graphql.value :refer [scalar-type field-value]]
              [map-util.map-util :refer [get-nested-object cat-map]]
              [clojure.core.reducers :refer [map fold]])
    (:import (clojure.lang MapEntry)))

(defn id?
    "Returns whether field should be ID."
    [[k v]]
    (and (scalar-type v)
         (clojure.string/ends-with? k "id")))

(defn object-name
    "Returns name for object in GraphQl schema."
    [key]
    (when (keyword? key)
        (keyword (str (clojure.string/upper-case
                          (subs (name key) 0 1))
                      (subs (name key) 1)))))

(defn non-null
    "Transforms fields non-null value to list with non-null metadata."
    [str]
    (with-meta (list str) {:non-null true}))

(defn remove-modifiers
    "Removes all modifiers (list and non-null) from object."
    [object]
    (loop [result object]
        (if (or (list? result) (vector? result))
            (recur (first result))
            result)))

(defn alter-modifiers
    "Removes existing modifiers and applies new modifiers."
    [modifiers value]
    (reduce #(case %2
                 :list [%1]
                 :non-null (non-null %1)
                 nil %1) (remove-modifiers value) (reverse modifiers)))

(defn parse-field
    "Returns `field` with its parsed value.
    If `field` is object type, name of object will be created with `name-fn`."
    [field name-fn]
    (when field
        (let [k (key field)]
            (if (id? field)
                {k 'ID}
                (when-let [value (field-value field name-fn)]
                    {k value})))))

(defn parse-type
    "Parses values of all fields in `object`, and returns MapEntry that represents parsed object."
    [name-fn object]
    (if-let [val (remove-modifiers (val object))]
        (if-not (scalar-type val)
            (->> val
                 (map #(parse-field (MapEntry. %1 %2) name-fn))
                 (fold cat-map)
                 (MapEntry. (name-fn (key object))))
            (throw (Exception. "The provided map isn't object type.")))))

(defn add-objects
    "Adds all object types that occur in `fields` to `objects`."
    [fields objects]
    (->> fields
         (filter #(:type (meta (val %))))
         (into objects)))

(def object-schema
    "Returns function for creating schema for one object."
    (comp (partial parse-type object-name)
          get-nested-object))

(defn parse-object
    "Returns GraphQL schema for supplied object and for all nested objects."
    ([objects]
     (parse-object objects object-schema))

    ([objects schema-fn]
     (try
         (loop [[[k _] & rest] objects
                result {}]
             (if k
                 (let [object (schema-fn objects k)]
                     (recur (add-objects (val object) rest)
                            (merge result object)))
                 result))
         (catch Exception e (throw (Exception. "The provided type is not object."))))))
