(ns map-util.map-util
    (:import (clojure.lang MapEntry)))

(defn deep-merge [a b]
    (merge-with (fn [x y]
                    (cond (map? y) (deep-merge x y)
                          (vector? y) (deep-merge x y)
                          :else y))
                a b))

(defn remove-vector [arg]
    (loop [result arg]
        (if (vector? result)
            (recur (first result))
            result)))

(defn get-nested-object
    [m k]
    (loop [m' m]
        (when (seq m')
            (if-let [v (get m' k)]
                (MapEntry. k v)
                (recur (reduce merge
                               (map (fn [[_ v]]
                                        (cond (map? v) v
                                              (vector? v) (remove-vector v)))
                                    m')))))))


(defn cat-map
    ([xs x]
     (into xs x))
    ([] {}))

(defn select-keys* [m paths]
    (loop [[f & rest] m
           result {}]
        (if f
            (recur rest (into result (select-keys (val f) paths)))
            result)))
