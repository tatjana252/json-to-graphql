(ns json-to-graphql.input-object
    (:require [map-util.map-util :refer [get-nested-object]]
              [json-to-graphql.object :refer [parse-type parse-object]]))

(defn input-object-name
    "Returns name for input object based on name."
    [key]
    (keyword (str (clojure.string/upper-case (subs (name key) 0 1))
                  (subs (name key) 1) "Input")))

(def input-object-schema
    "Defines function that returns object associated with its input name."
    (comp (partial parse-type input-object-name)
          get-nested-object))

(defn parse-input-object
    "Returns input objects from objects."
    [objects]
    (parse-object objects input-object-schema))