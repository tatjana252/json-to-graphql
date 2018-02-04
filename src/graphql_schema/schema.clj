(ns graphql-schema.schema
    (:require [cheshire.core :refer [parse-string]]
              [graphql-schema.schema-map :as sm]
              [graphql-schema.print :as p]
              [graphql-schema.object :as o]
              [map-utils.map-utils :as m])
    (:import (clojure.lang MapEntry)))

(defn apply-non-null [obj name nn-map]
    (if nn-map
        (reduce-kv
            #(update-in %1 [(keyword name) %2]
                        (partial o/alter-modifiers %3)) obj nn-map)
        obj))

(defn object-schema
    [name obj ops]
    (let [object (assoc {} (keyword name) obj)]
        (-> object
            (apply-non-null name (:non-null ops))
            (sm/object-types))))

(defn object-from-json
    ([json name ops]
        (object-from-json {} json name ops))
    ([schema json name ops]
     (let [objects (object-schema name (parse-string json true) ops)]
         (m/deep-merge schema {:objects objects}))))

(defn object-from-map
    ([obj name ops]
     (object-from-map {} obj name ops))
    ([schema obj name ops]
     (let [objects (object-schema name obj ops)]
         (m/deep-merge schema {:objects objects}))))

(defn schema
    [schema]
    (p/get-objects (:objects schema)))





