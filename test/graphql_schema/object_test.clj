(ns graphql-schema.object-test
    (:require [midje.sweet :refer :all]
              [graphql-schema.object :refer :all]
              [cheshire.core :refer :all])
    (:import (clojure.lang MapEntry)))

(fact
    (id? (MapEntry. :a 123)) => false
    (id? (MapEntry. :id 123)) => true
    (id? (MapEntry. :my_id "String")) => true
    (id? (MapEntry. :id {})) => false
    (id? (MapEntry. :my_id {})) => false
    (id? (MapEntry. :my_id [])) => false)

(fact
    (parse-field (MapEntry. :a {:b 1})) => {:a (symbol "A")}
    (parse-field (MapEntry. :a [1 2 3])) => {:a (symbol "[Int]")}
    (parse-field (MapEntry. :a 1.2)) => {:a (symbol "Float")}
    (parse-field (MapEntry. :a "String")) => {:a (symbol "String")}
    (parse-field (MapEntry. :a (non-null "String"))) => {:a (symbol "String!")}
    (parse-field (MapEntry. :a (non-null ["String"]))) => {:a (symbol "[String]!")}
    (parse-field (MapEntry. :a (non-null [(non-null "String")]))) => {:a (symbol "[String!]!")})

(fact
    (clean [[['("String")]]]) => "String")

(fact
    (alter-modifiers [:list :list :non-null] [[(symbol "String")]]) => [['(String)]])