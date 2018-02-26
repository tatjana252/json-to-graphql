(ns map-util.map-util
    (:import (clojure.lang MapEntry)))

(defn deep-merge [a b]
    (merge-with (fn [x y]
                    (cond (map? y) (deep-merge x y)
                          (vector? y) (deep-merge x y)
                          :else y))
                a b))

(defn return-map [arg]
    (if (vector? arg)
        (first arg)
        arg))

(defn get-nested-object
    [m k]
    (->> (tree-seq map? vals m)
         (filter map?)
         (some k)
         return-map
         (MapEntry. k)))

(defn cat-map ([xs x] (into xs x)) ([] {}))

(defn select-keys* [m paths]
    (loop [[f & rest] m
           result {}]
        (if f
            (recur rest (into result (select-keys (val f) paths)))
            result)))
