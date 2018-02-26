(ns json-to-graphql.core
    (:require [cheshire.core]
              [json-to-graphql.schema-map :refer [schema-map]]
              [json-to-graphql.print :refer [objects-schema input-objects-schema make-queries make-mutations]]
              [json-to-graphql.object :refer [alter-modifiers]]
              [map-util.map-util :refer [deep-merge]]))

(defn apply-non-null
    "Returns object with applied non-null map."
    [obj nn-map]
    (if nn-map
        (reduce-kv #(update-in %1 %2 (partial alter-modifiers %3))  obj nn-map)
        obj))

(defn add-top-level
    "Wraps all `json` fields into object with name `n`"
    [json n]
    (if n
        (str "{\"" n "\": " json "}")
        json))

(defn json->graphql
    "Returns Clojure map that represents GraphQL schema based on `json`.
    If `schema` is supplied then it will be merged into resulting schema."
    ([json name ops]
        (json->graphql {} json name ops))
    ([schema json name ops]
      (-> json
          (add-top-level name)
          (cheshire.core/parse-string true)
          (apply-non-null (:non-null ops))
          (schema-map ops)
          (deep-merge schema))))

(defn graphql-schema
    "Returns a valid GraphQL schema based on map that represents GraphQL schema."
    [schema]
    (let [o (future (objects-schema (:objects schema)))
          i (future (input-objects-schema (:input-objects schema)))
          q (future (make-queries (:queries schema)))
          m (future (make-mutations (:mutations schema)))
          res (str "{" @o @i @q @m "}")]
        (shutdown-agents)
        res))