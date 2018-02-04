(ns json-to-graphql.schema
    (:require [cheshire.core :refer [parse-string]]
              [json-to-graphql.schema-map :as sm]
              [json-to-graphql.print :as p]
              [json-to-graphql.object :as o]
              [map-util.map-util :as m])
    (:import (clojure.lang MapEntry)))

(defn apply-non-null [obj name nn-map]
    (if nn-map
        (reduce-kv
            #(update-in %1 [(keyword name) %2]
                        (partial o/alter-modifiers %3)) obj nn-map)
        obj))

(defn json-map
    [name obj ops]
    (let [object (assoc {} (keyword name) obj)]
        (-> object
            (apply-non-null name (:non-null ops)))))

(defn object-from-json
    ([json name ops]
        (object-from-json {} json name ops))
    ([schema json name ops]
     (let [json-map (json-map name (parse-string json true) ops)
           objects (sm/objects json-map)
           input-objects (if (:input-object ops) (sm/input-objects json-map) {})]
         (m/deep-merge schema {:objects objects :input-objects input-objects}))))

(defn schema
    [schema]
    (str
         (p/objects-schema (:objects schema))
         (p/input-objects-schema (:input-objects schema))))

(shutdown-agents)