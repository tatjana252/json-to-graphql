(ns json-to-graphql.input-object
  (:require [map-util.map-util :as m]
            [json-to-graphql.object :as o]))

(defn input-object-name [key]
  (when (keyword? key)
    (keyword (str (clojure.string/upper-case
                    (subs (name key) 0 1))
                  (subs (name key) 1) "Input"))))

(def input-object-schema
  (comp (partial o/parse-type input-object-name)
        m/get-nested-object))

(defn parse-input-object
  [objects]
  (o/parse-object objects input-object-schema))