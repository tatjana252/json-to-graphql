(ns json-to-graphql.object-test
  (:require [midje.sweet :refer [fact throws]]
            [cheshire.core :refer :all]
            [json-to-graphql.object :refer :all])
  (:import (clojure.lang MapEntry)))

(fact
    (parse-field (MapEntry. :b 1) object-name) => {:b 'Int}
    (parse-field (MapEntry. :d {:d1 12}) object-name) => {:d 'D}
    (parse-field (MapEntry. :b {:b1 123}) object-name) => {:b 'B}
    (parse-field (MapEntry. :b 1) object-name) => {:b 'Int}
    (parse-field (MapEntry. :b "String") object-name) => {:b 'String}
    (parse-field (MapEntry. :b 2.54) object-name) => {:b 'Float}
    (parse-field (MapEntry. :b true) object-name) => {:b 'Boolean}
    (parse-field (MapEntry. :b [1]) object-name) => {:b (symbol "[Int]")}
    (parse-field (MapEntry. :b [{:b1 [{:b2 5}]}]) object-name) => {:b (symbol "[B]")}
    (parse-field nil nil) => nil)

(fact (parse-type object-name (MapEntry. :a {:b 1 :c "String" :d {:d1 12}}) ) => (MapEntry. :A {
                                                                                     :b 'Int
                                                                                     :c 'String
                                                                                     :d 'D})
      (parse-type object-name (MapEntry. :a 1) ) => (throws Exception "The provided map isn't object type.")
      (parse-type nil nil) =>  (throws NullPointerException))

(fact (parse-object {:a {:a1 5 :a2 "String" :b {:b1 [2.25] :b2 true}}})  => {:A {:a1 'Int :a2 'String :b 'B}
                                                                              :B {:b1 (symbol "[Float]") :b2 'Boolean}}
      (parse-object (MapEntry. :a 1)) => (throws Exception "The provided type is not object.")
      (parse-object nil nil) => {})





