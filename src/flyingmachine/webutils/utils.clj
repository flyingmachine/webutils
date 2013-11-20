(ns flyingmachine.webutils.utils
  (:require [clojure.java.io :as io]))

(defn str->int
  ([str]
     (if (string? str)
       (read-string (re-find #"^-?\d+$" str))
       str))

  ([m & keys]
     (reduce
      (fn [m k]
        (if-let [val (k m)]
          (assoc m k (str->int val))
          m))
      m keys)))

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

(defmacro defnpd
  ;; defn with default positional arguments
  [name args & body]
  (let [unpack-defaults
        (fn [args]
          (let [[undefaulted defaulted] (split-with (comp not vector?) args)
                argcount (count args)]
            (loop [defaulted defaulted
                   argset {:argnames (into [] undefaulted)
                           :application (into [] (concat undefaulted (map second defaulted)))}
                   unpacked-args [argset]
                   position (count undefaulted)]
              (if (empty? defaulted)
                unpacked-args
                (let [argname (ffirst defaulted)
                      new-argset {:argnames (conj (:argnames argset) argname)
                                  :application (assoc (:application argset) position argname)}]
                  (recur (rest defaulted) new-argset (conj unpacked-args new-argset) (inc position)))))))
        unpacked-args (unpack-defaults args)]
    
    `(defn ~name
       (~(:argnames (last unpacked-args))
        ~@body)
       ~@(map #(list (:argnames %)
                     `(~name ~@(:application %)))
              (drop-last unpacked-args)))))

(defn now
  []
  (java.util.Date.))