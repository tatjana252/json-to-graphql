(ns json-to-graphql.schema
    (:require [cheshire.core :refer [parse-string]]
              [json-to-graphql.schema-map :as sm]
              [json-to-graphql.print :as p]
              [json-to-graphql.object :as o]
              [map-util.map-util :as m])
    (:import (clojure.lang MapEntry)))

(defn apply-non-null [nn-map obj ]
    (if nn-map
        (reduce-kv
            #(update-in %1 %2
                        (partial o/alter-modifiers %3)) obj nn-map)
        obj))

(defn json-map
    [obj ops name]
    (->> obj
         (apply-non-null (:non-null ops))
         (assoc {} (keyword name))))

(defn object-from-json
    ([json name ops]
        (object-from-json {} json name ops))
    ([schema json name ops]
      (-> json
          (parse-string true)
          (json-map ops name)
          (sm/schema-map ops name)
          (m/deep-merge schema))))

(defn schema
    [schema]
    (str
         (p/objects-schema (:objects schema))
         (p/input-objects-schema (:input-objects schema))
         (p/make-queries (:queries schema))
         (p/make-mutations (:mutations schema))))
