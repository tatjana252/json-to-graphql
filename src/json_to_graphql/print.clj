(ns json-to-graphql.print
    (:require [clojure.core.reducers :as r]
              [map-util.map-util :as m]
              [clojure.core.async :as a])
    (:import (clojure.lang MapEntry)))

(defn field [k v]
    (str " " (name k) ": " v "\n"))

(defn put-in-brackets [s]
    (str "\n{\n" s "}\n"))

(defn type-name [n fields]
    (->> fields
         put-in-brackets
         (str "type " (name n))))

(defn input-name [n fields]
    (->> fields
         put-in-brackets
         (str "input " (name n))))

(defn make-type [type-fn [k v]]
    (->>  v
         (reduce-kv #(str %1 (field %2 %3)) "")
         (r/fold str)
         (type-fn k)))

(def object-type (partial make-type type-name))
(def input-object-type (partial make-type input-name))

(defn make-schema
    [schema type-fn]
    (->> schema
         (r/map #(type-fn (MapEntry. %1 %2)))
         (r/fold clojure.core/str)))

(defn objects-schema
    [objects]
    (make-schema objects object-type))

(defn input-objects-schema
    [input-objects]
    (when-not (empty? input-objects)
        (make-schema input-objects input-object-type)))

(defn mutation-args [k v]
    (str (:name v)
         "("
         (clojure.string/lower-case (:args v))
         ": "
         (:args v)
         "): "
         k
         "\n"))

(defn args [args]
    (->> args
         (reduce-kv #(str %1 (name %2) ":" %3 ",") "" )
         drop-last
         (clojure.string/join "")))

(defn query [k v]
    (str (clojure.string/lower-case (:name v))
         "("
         (args (:args v))
         "): "
         k "\n" ))

(defn make-queries
    [queries]
    (when queries
        (->> queries
             (r/map #(query %1 %2))
             (r/fold str)
             put-in-brackets
             (str "type Query"))))

(defn make-mutations
    [mutations]
    (when mutations
        (->> mutations
             (r/map mutation-args)
             (r/fold str)
             put-in-brackets
             (str "type Mutation"))))

