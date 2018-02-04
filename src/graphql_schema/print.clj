(ns graphql-schema.print
    (:require [clojure.core.reducers :as r]
              [map-utils.map-utils :as m]
              [clojure.core.async :as a]))

(defn field [k v]
    (str " " (name k) ": " v "\n"))

(defn put-in-brackets [s]
    (str "\n{\n" s "\n}\n"))

(defn type-name [n fields]
    (->> fields
         put-in-brackets
         (str "type " (name n))))

(defn object-type [k v]
    (->> v
         (r/map #(field %1 %2))
         (r/fold str)
         (type-name k)))

(defn get-objects
    [schema]
    (->> schema
         (r/map #(object-type %1 %2))
         (r/fold str)
         put-in-brackets))