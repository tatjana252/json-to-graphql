(ns graphql-schema.value
    (:import (clojure.lang MapEntry)))


(defn object-name [key]
    (when (keyword? key)
        (keyword (str (clojure.string/upper-case
                          (subs (name key) 0 1))
                      (subs (name key) 1)))))

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

(defn remove-value-modifiers [type]
    (apply str (remove (set "[]! ") type)))

(defn null-modifiers [value]
    (if (:non-null (meta value))
        {:value (first value) :non-null :non-null}
        {:value value}))

(defn object-field
    [modifiers key]
    (let [name (object-name key)
          obj-field (add-value-modifiers modifiers name)]
        (vary-meta obj-field assoc :type name)))

(defn field-value [[key value]]
    (loop [key key value value modifiers '()]
        (let [{non-null :non-null value :value} (null-modifiers value)
              modifiers (cons  non-null modifiers)]
            (condp #(%1 %2) value
                scalar-type :>> (partial add-value-modifiers modifiers)
                map? (object-field modifiers key)
                vector? (recur key (first value) (cons :list modifiers))
                nil))))

