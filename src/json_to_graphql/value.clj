(ns json-to-graphql.value)

(defn scalar-type
    "Returns type of `value` if it's scalar"
    [value]
    (condp #(%1 %2) value
        string? (symbol "String")
        integer? (symbol "Int")
        double? (symbol "Float")
        boolean? (symbol "Boolean")
        false))

(defn add-value-modifiers
    "Adds value modifiers to type."
    [modifiers type]
    (symbol (reduce #(case %2
                         :list (str "[" %1 "]")
                         :non-null (str %1 "!")
                         nil %1) (name type) modifiers)))

(defn null-modifiers
    "If `value` has non-null metadata it returns map with :value and :non-null"
    [value]
    (if (:non-null (meta value))
        {:value (first value) :non-null :non-null}
        {:value value}))

(defn object-field
    "Creates object field with fn-name and metadata of type"
    [modifiers key fn-name]
    (let [name (fn-name key)
          obj-field (add-value-modifiers modifiers name)]
        (vary-meta obj-field assoc :type name)))

(defn field-value
    "Returns field map {:name type} of schema"
    [[key value] fn-name]
    (loop [key key value value modifiers '()]
        (let [{non-null :non-null value :value} (null-modifiers value)
              modifiers (cons non-null modifiers)]
            (condp #(%1 %2) value
                scalar-type :>> (partial add-value-modifiers modifiers)
                map? (object-field modifiers key fn-name)
                vector? (recur key (first value) (cons :list modifiers))
                nil))))
