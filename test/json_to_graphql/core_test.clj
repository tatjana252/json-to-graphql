(ns json-to-graphql.core-test
  (:require [midje.sweet :refer [fact throws]]
            [cheshire.core :refer :all]
            [json-to-graphql.core :refer :all]
            [json-to-graphql.object :refer [object-name]])
  (:import (clojure.lang MapEntry)))

(def company "{\"id\" : 111,
                \"name\": \"Microsoft\",
                \"websites\": [\"microsoft.com\", \"msn.com\", \"hotmail.com\"],
                \"address\" : {\"street\" : \"11 Times Square\",
                               \"city\" : \"New York\"}}")

(fact
    (json->graphql company "Company" nil) => {:input-objects {:AddressInput {:city 'String :street 'String}
                                                              :CompanyInput {:address  'AddressInput
                                                                             :id       'ID
                                                                             :name     'String
                                                                             :websites (symbol "[String]")}}
                                              :mutations     {"Company" {:args 'CompanyInput :name "add_Company"}}
                                              :objects       {:Address {:city 'String :street 'String}
                                                              :Company {:address 'Address :id 'ID :name 'String :websites (symbol "[String]")}}
                                              :queries       {}}
    (json->graphql company "Company" {:input-objects {:AddressInput {:city 'String :street 'String}
                                                      :CompanyInput {:address  'AddressInput
                                                                     :id       'ID
                                                                     :name     'String
                                                                     :websites (symbol "[String]")}}
                                      :mutations     {"Company" {:args 'CompanyInput :name "add_Company"}}
                                      :objects       {:Address {:city 'String :street 'String}
                                                      :Company {:address 'Address :id 'ID :name 'String :websites (symbol "[String]")}}
                                      :queries       {"Company" {:args {:id 'ID} :name "company_by_id"}}}))

(def example "{\"a\" : {\"a2\" : 2}, \"b\" : {\"b2\" : \"String\"}}")

(fact
    (json->graphql company nil nil) => (throws Exception "The provided map isn't object type.")
    (json->graphql example nil nil) => {:input-objects {:AInput {:a2 'Int} :BInput {:b2 'String}}
                                        :mutations {"A" {:args 'AInput :name "add_a"} "B" {:args 'BInput :name "add_b"}}
                                        :objects {:A {:a2 'Int} :B {:b2 'String}}
                                        :queries {}}
    (json->graphql example "C" nil) => {:input-objects {:AInput {:a2 'Int}
                                                        :BInput {:b2 'String}
                                                        :CInput {:a 'AInput :b 'BInput}}
                                        :mutations {"C" {:args 'CInput :name "add_C"}}
                                        :objects {:A {:a2 'Int} :B {:b2 'String} :C {:a 'A :b 'B}}
                                        :queries {}})

(shutdown-agents)