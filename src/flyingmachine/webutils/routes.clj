(ns flyingmachine.webutils.routes
  (:require [compojure.core :refer (GET PUT POST DELETE ANY defroutes)]))

(defmacro route
  [method path handler]
  `(~method ~path {params# :params}
            (~handler params#)))

(defmacro authroute
  [method path auth-fn handler]
  (let [params (quote params)]d
    `(~method ~path {:keys [~params] :as req#}
              (~handler ~params (~auth-fn req#)))))

(defn resource-path
  [name & suffixes]
  (str "/" (apply str (interpose "/" (into [name] suffixes)))))

(defn action
  [name action]
  (symbol (str name "/" action)))

(def resource-actions
  {:index (fn [name] `(route GET ~(resource-path name ":id") ~(action name "query" )))
   :show (fn [name] `(route GET ~(resource-path name ":id") ~(action name "show")))
   :create (fn [name] `(route POST ~(resource-path name) ~(action name "create!")))
   :update! (fn [name] `(route PUT ~(resource-path name ":id") ~(action name "update!")))
   :delete! (fn [name] `(route DELETE ~(resource-path name ":id") ~(action name "delete!")))})

(defmacro resource
  [name & actions]
  (let [actions (if (empty? actions)
                  [:index :show :create :update! :delete!]
                  actions)])
  `(do ~@(map (fn [action] ((get resource-actions action) name)) actions)))