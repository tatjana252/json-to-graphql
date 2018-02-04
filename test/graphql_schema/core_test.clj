(ns graphql-schema.core-test
  (:require [midje.sweet :refer :all]
            [graphql-schema.schema-map :refer :all]
            [cheshire.core :refer :all]))

(def schema {:objects
               {:A {:fields {:id {:type 'ID}
                             :a1 {:type 'String}
                             :a2 {:type 'Float}
                             :a3 {:type 'Boolean}
                             :a4 {:type 'Int}}}}})

(def json-obj "{\"A\":{\"id\":123,
\"a1\": \"A1\", \"a2\": 1.25, \"a3\": true, \"a4\": 3}}")

(def json-obj2 "{\"A\":{\"id\":123,
\"a1\": \"A1\", \"a2\": 1.25, \"a3\": true, \"a4\": 3}}")


(def asd (parse-string json-obj true))

(fact
    (#'make-object-key :object) => :Object
    (#'make-object-key nil) => nil)