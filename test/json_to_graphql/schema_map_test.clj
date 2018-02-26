(ns json-to-graphql.schema-map-test
    (:require [midje.sweet :refer [fact throws]]
              [cheshire.core :refer :all]
              [json-to-graphql.schema-map :refer :all]))

; {:A {:b [[B]]}, :B {:b1 Int, :c [C]}, :C {:c1 Int}}

(def json "{ \"b\" : [[{\"b1\" : 1, \"c\": [{\"c1\": 1}, {\"c1\": 1}] }]] }")
(def json-m (parse-string json true))

(fact
    (objects json-m) => {:B {:b1 'Int :c (symbol "[C]")} :C {:c1 'Int}}
    (objects {:b 1}) => (throws Exception "The provided map isn't object type.")
    (objects {:b {:b1 "String"}}) => {:B {:b1 'String}}
    (objects {:b {:b1 "String"} :c {:c1 [1 2 3]}}) => {:B {:b1 'String} :C {:c1 (symbol "[Int]")}}
    (objects {:b {:b1 {:b2 {:b3 4}}}}) => {:B {:b1 'B1} :B1 {:b2 'B2} :B2 {:b3 'Int}}
    (objects {:b {:b1 [{:b2 [[{:b3 [4]}]]}]}}) => {:B  {:b1 (symbol "[B1]")}
                                                   :B1 {:b2 (symbol "[[B2]]")}
                                                   :B2 {:b3 (symbol "[Int]")}}
    (objects {:b [1]}) => (throws Exception "The provided map isn't object type.")
    (objects {:b [{:c "String"}]}) => (objects {:b {:c "String"}}))

(def objs {:A {:b [['B]], :id 'ID} :B {:b1 'Int, :c (symbol "[C]")} :C {:c1 'Int}})

(fact
    (query [:a :id] objs) => {"A" {:name "a_by_id" :args {:id 'ID}}})


(fact
    (input-objects json-m) => {:BInput {:b1 'Int :c (symbol "[CInput]")} :CInput {:c1 'Int}}
    (input-objects {:b 1}) => (throws Exception "The provided map isn't object type.")
    (input-objects {:b {:b1 "String"}}) => {:BInput {:b1 'String}}
    (input-objects {:b {:b1 "String"} :c {:c1 [1 2 3]}}) => {:BInput {:b1 'String} :CInput {:c1 (symbol "[Int]")}}
    (input-objects {:b {:b1 {:b2 {:b3 4}}}}) => {:BInput {:b1 'B1Input} :B1Input {:b2 'B2Input} :B2Input {:b3 'Int}}
    (input-objects {:b {:b1 [{:b2 [[{:b3 [4]}]]}]}}) => {:BInput  {:b1 (symbol "[B1Input]")}
                                                   :B1Input {:b2 (symbol "[[B2Input]]")}
                                                   :B2Input {:b3 (symbol "[Int]")}}
    (input-objects {:b [1]}) => (throws Exception "The provided map isn't object type.")
    (input-objects {:b [{:c "String"}]}) => (input-objects {:b {:c "String"}}))

