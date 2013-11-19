(ns flyingmachine.webutils.utils
  (:require [clojure.java.io :as io]))

(defn slurp-resource
  [path]
  (-> path
      io/resource
      slurp))

(defn read-resource
  [path]
  (-> path
      slurp-resource
      read-string))

(defn ifn
  [val fun]
  (if val (fun val)))

(defn default
  [val pred default-val]
  (if (pred val)
    default-val
    val))

(defn remove-nils-from-map
  [record]
  (into {} (remove (comp nil? second) record)))

(defn reverse-by
  [key col]
  (sort-by key #(compare %2 %1) col))

(defn rest-map
  [seq]
  (apply hash-map seq))

(defn prune
  [x {:keys [except only]}]
  (let [e? (empty? except)
        o? (empty? only)]
    (if (not (or e? o?))
      (throw (java.lang.IllegalArgumentException. "You can specify :except or :only, but not both")))
    (if (and e? o?)
      x
      (if e?
        (select-keys x only)
        (apply dissoc x except)))))

(defn xml-str
 "Like clojure.core/str but escapes < > and &."
 [x]
  (-> x str (.replace "&" "&amp;") (.replace "<" "&lt;") (.replace ">" "&gt;")))

