(ns json-to-graphql.object-test
    (:require [midje.sweet :refer :all]
              [json-to-graphql.object :refer :all]
              [cheshire.core :refer :all]
              [json-to-graphql.object :as o])
    (:import (clojure.lang MapEntry)))

(fact
    (parse-field (MapEntry. :a {:b 1}) o/object-name) => {:a (symbol "A")}
    (parse-field (MapEntry. :a [1 2 3]) o/object-name) => {:a (symbol "[Int]")}
    (parse-field (MapEntry. :a 1.2) o/object-name) => {:a (symbol "Float")}
    (parse-field (MapEntry. :a "String") o/object-name) => {:a (symbol "String")}
    (parse-field (MapEntry. :a (non-null "String")) o/object-name) => {:a (symbol "String!")}
    (parse-field (MapEntry. :a (non-null ["String"])) o/object-name) => {:a (symbol "[String]!")}
    (parse-field (MapEntry. :a (non-null [(non-null "String")])) o/object-name) => {:a (symbol "[String!]!")})

(fact
    (remove-modifiers [[['("String")]]]) => "String")

(fact
    (alter-modifiers [:list :list :non-null] [[(symbol "String")]]) => [['(String)]]
    (alter-modifiers [:non-null :list :non-null]  [(symbol "String")]) => '([(String)])
    (alter-modifiers [:list  :non-null :list] [[(symbol "String")]]) => ['([String])])

