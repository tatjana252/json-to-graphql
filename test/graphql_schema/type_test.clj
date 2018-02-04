(ns graphql-schema.type-test
    (:require [midje.sweet :refer :all]
              [graphql-schema.value :refer :all]
              [graphql-schema.schema-map :refer :all]
              [cheshire.core :refer :all])
    (:import (clojure.lang MapEntry)))

(fact
    (is-object-type? ":Object") => true
    (is-object-type? "(list :Object)") => true
    (is-object-type? "(list (non-null :Object))") => true
    (is-object-type? "(list (non-null :Object ))") => true
    (is-object-type? "(non-null (list :Object))") => true
    (is-object-type? "(non-null ( ))") => false
    (is-object-type? :Object) => true
    (is-object-type? nil) => false)

(fact
    (object-name :object) => :Object
    (object-name ":object") => nil
    (object-name "(list (non-null :Object))") => nil
    (object-name "(list (non-null ))") => nil
    (object-name "") => nil
    (object-name nil) => nil)

(fact
    (scalar-type 1) => (symbol "Int")
    (scalar-type 1.2) => (symbol "Float")
    (scalar-type -1.2) => (symbol "Float")
    (scalar-type "String") => (symbol "String")
    (scalar-type true) => (symbol "Boolean")
    (scalar-type nil) => nil
    (scalar-type []) => nil
    (scalar-type {}) => nil)

(fact
    (add-value-modifiers :Object []) => (symbol ":Object")
    (add-value-modifiers 'Int []) => (symbol "Int")
    (add-value-modifiers :Object [:non-null]) => (symbol "(non-null :Object)")
    (add-value-modifiers :Object [:non-null :list]) => (symbol "(list (non-null :Object))")
    (add-value-modifiers :Object [:list]) => (symbol "(list :Object)")
    (add-value-modifiers :Object [:list :list :non-null]) => (symbol "(non-null (list (list :Object)))")
    (add-value-modifiers :Object [:list :non-null :non-null]) => (symbol "(non-null (non-null (list :Object)))")
    (add-value-modifiers nil [:list]) => nil
    (add-value-modifiers nil []) => nil
    (add-value-modifiers "" [:list]) => nil
    (add-value-modifiers "" []) => nil)

(fact
    (null-modifiers 1) => {:value 1}
    (null-modifiers "String") => {:value "String"}
    (null-modifiers true) => {:value true}
    (null-modifiers nil) => {:value nil}
    (null-modifiers (with-meta '("String") {:non-null :non-null})) => {:value "String" :non-null :non-null}
    (null-modifiers {}) => {:value {}})

(fact
    (field-value (MapEntry. :a 1)) => (symbol "Int")
    (field-value (MapEntry. :b "String")) => (symbol "String")
    (field-value (MapEntry. :c true)) => (symbol "Boolean")
    (field-value (MapEntry. :a {:b {:c 1}})) => (symbol ":A")
    (field-value (MapEntry. :a [[2]])) => (symbol "(list (list Int))")
    (field-value (MapEntry. :a [{:b 2}])) => (symbol "(list :A)")
    (field-value (MapEntry. :a [1 2 4])) => (symbol "(list Int)")
    (field-value (MapEntry. :a ["str" "str" "str"])) => (symbol "(list String)")
    (field-value (MapEntry. :a [(non-null "str")])) => (symbol "(list (non-null String))")
    (field-value (MapEntry. :a (non-null ["str"]))) => (symbol "(non-null (list String))")
    (field-value nil) => nil)