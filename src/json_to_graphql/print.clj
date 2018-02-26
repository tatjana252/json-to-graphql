(ns json-to-graphql.print
    (:require [clojure.core.reducers :refer [map fold]])
    (:import (clojure.lang MapEntry)))

(defn field
    "Returns string of field."
    [k v]
    (str " " (name k) ": " v "\n"))

(defn put-in-brackets
    "Puts string in brackets"
    [s]
    (str "\n{\n" s "}\n"))

(defn type-name
    "Returns string that represents GraphQL type."
    [type n fields]
    (->> fields
         put-in-brackets
         (str type " " (name n))))

(defn make-type
    "Returns type map created with `type-fn` for one object MapEntry."
    [type-fn [k v]]
    (->>  v
          (reduce-kv #(str %1 (field %2 %3)) "")
          (fold str)
          (type-fn k)))

(def object-type (partial make-type (partial type-name "type")))
(def input-object-type (partial make-type (partial type-name "input")))

(defn make-schema
    "Returns GraphQL schema applying `type-fn`  to `schema` map."
    [schema type-fn]
    (->> schema
         (map #(type-fn (MapEntry. %1 %2)))
         (fold clojure.core/str)))

(defn objects-schema
    "Returns GraphQl schema of objects."
    [objects]
    (make-schema objects object-type))

(defn input-objects-schema
    "Returns GraphQl schema of input objects."
    [input-objects]
    (when-not (empty? input-objects)
        (make-schema input-objects input-object-type)))

(defn mutation
    "Returns mutation query created from suppled `queries` map and `return-type`."
    [return-type queries]
    (str (:name queries)
         "("
         (clojure.string/lower-case (:args queries))
         ": "
         (:args queries)
         "): "
         return-type
         "\n"))

(defn args
    "Returns arguments for query that are created from supplied `args`."
    [args]
    (->> args
         (reduce-kv #(str %1 (name %2) ":" %3 ",") "" )
         drop-last
         (clojure.string/join "")))

(defn query
    "Returns GraphQL query based on `queries` map that returns `return-type`."
    [return-type queries]
    (str (clojure.string/lower-case (:name queries))
         "("
         (args (:args queries))
         "): "
         return-type "\n" ))

(defn make-queries
    "Returns GraphQL type Query based on supplied `queries` map."
    [queries]
    (when (not-empty queries)
        (->> queries
             (map #(query %1 %2))
             (fold str)
             put-in-brackets
             (str "type Query"))))

(defn make-mutations
    "Takes `mutations` map and returns GraphQL schema type Mutation."
    [mutations]
    (when mutations
        (->> mutations
             (map mutation)
             (fold str)
             put-in-brackets
             (str "type Mutation"))))

