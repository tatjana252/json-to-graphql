(ns json-to-graphql.value
    (:import (clojure.lang MapEntry)))

(defn scalar-type [value]
    (condp #(%1 %2) value
        string? (symbol "String")
        integer? (symbol "Int")
        double? (symbol "Float")
        boolean? (symbol "Boolean")
        false))

(defn add-value-modifiers [modifiers type]
    (symbol (reduce #(case %2
                         :list (str "[" %1 "]")
                         :non-null (str %1 "!")
                         nil %1) (name type) modifiers)))

(defn null-modifiers [value]
    (if (:non-null (meta value))
        {:value (first value) :non-null :non-null}
        {:value value}))

(defn object-field
    [modifiers key fn-name]
    (let [name (fn-name key)
          obj-field (add-value-modifiers modifiers name)]
        (vary-meta obj-field assoc :type name)))

(defn field-value [[key value] fn-name]
    (loop [key key value value modifiers '()]
        (let [{non-null :non-null value :value} (null-modifiers value)
              modifiers (cons non-null modifiers)]
            (condp #(%1 %2) value
                scalar-type :>> (partial add-value-modifiers modifiers)
                map? (object-field modifiers key fn-name)
                vector? (recur key (first value) (cons :list modifiers))
                nil))))
