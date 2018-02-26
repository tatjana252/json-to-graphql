(ns json-to-graphql.value-test
  (:require [midje.sweet :refer [fact throws]]
            [cheshire.core :refer :all]
            [json-to-graphql.value :refer :all]
            [json-to-graphql.object :refer [object-name]])
  (:import (clojure.lang MapEntry)))

(fact
    (field-value nil nil) => nil
    (field-value [] nil) => nil
    (field-value (MapEntry. :a 1) object-name) => 'Int
    (field-value (MapEntry. :a {:b 3}) object-name) => 'A
    (field-value (MapEntry. :a {}) object-name) => 'A
    (field-value (MapEntry. :a []) object-name) => nil
    (field-value (MapEntry. :a [1 2 3]) object-name) => (symbol "[Int]"))


