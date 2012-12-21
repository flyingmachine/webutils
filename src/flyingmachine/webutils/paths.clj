(ns ^{:doc "convenience methods for generating paths and generating path-generating fns"}
    flyingmachine.webutils.paths)

(defn path
  [record url-string prefix & suffixes]
  ""
  (str "/"
       (apply str
              (interpose
               "/"
               (into [prefix (or (url-string record) record)] suffixes)))))

(defmacro create-path-fns
  [record-type url-id & suffixes]
  "example: 
  (create-path-fns \"user\" :username \"edit\" \"posts\" \"comments\" \"notification-settings\")

expands to:
 (do

 (clojure.core/defn
  user-path
  [record]
  (flyingmachine.paths/path record :username \"users\"))
 
 (clojure.core/defn
  user-edit-path
  [record]
  (flyingmachine.paths/path record :username \"users\" \"edit\"))
 
 (clojure.core/defn
  user-posts-path
  [record]
  (flyingmachine.paths/path record :username \"users\" \"posts\"))
 
 (clojure.core/defn
  user-comments-path
  [record]
  (flyingmachine.paths/path
   record
   :username
   \"users\"
   \"comments\"))
 
 (clojure.core/defn
  user-notification-settings-path
  [record]
  (flyingmachine.paths/path
   record
   :username
   \"users\"
   \"notification-settings\")))"

  `(do
     ~@(map
        (fn [suffix]
          (let [fn-name-suffix (if suffix
                                 (str "-" suffix "-path")
                                 "-path")
                x (gensym)
                y (gensym)
                fn-name (symbol (str record-type fn-name-suffix))]
            `(defn ~fn-name
               ([~x]
                  ;; TODO is there a briefer way of doing this?
                  ;; maybe make a suffix version and non-suffix version
                  ~(if suffix
                     `(path ~x ~url-id ~(str record-type "s") ~suffix)
                     `(path ~x ~url-id ~(str record-type "s"))))
               ([~x & ~y]
                  (str (~fn-name ~x) (apply str ~y))))))
        (conj suffixes nil))))