(ns flyingmachine.webutils.routes
  (:require [compojure.core :refer (GET PUT POST DELETE ANY defroutes)]
            [flyingmachine.webutils.utils :refer :all]))

(defmacro route
  [method path handler]
  `(~method ~path {params# :params}
            (~handler params#)))

(defmacro authroute
  [method path handler auth-fn]
  (let [params (quote params)]
    `(~method ~path {:keys [~params] :as req#}
              (~handler ~params (~auth-fn req#)))))

(defn resource-path
  [resource-name & suffixes]
  (str "/" (apply str (interpose "/" (into [resource-name] suffixes)))))

(defn action
  "Used to produce name of function which should be called for a route"
  [resource-name action]
  (symbol (str resource-name "/" action)))

(defn route-template
  "Example return:
   (route GET \"/widgets\" widgets/query)
   (authroute POST \"/widgets\" widgets/create! friend/auth)"
  [resource-name {:keys [route-op method action-name suffixes route-args]}]
  `(~route-op
    ~method
    ~(apply resource-path resource-name suffixes)
    ~(action resource-name action-name)
    ~@route-args))

(def resource-actions
  {:query {:route-op 'route
           :method 'GET
           :action-name 'query}
   :show {:route-op 'route
          :method 'GET
          :action-name "show"
          :suffixes [":id"]}
   :create! {:route-op 'route
             :method 'POST
             :action-name 'create!}
   :update! {:route-op 'route
             :method 'PUT
             :action-name 'update!
             :suffixes [":id"]}
   :delete! {:route-op 'route
             :method 'DELETE
             :action-name 'delete!
             :suffixes [":id"]}})

(defn- separate-options
  [options]
  (let [prune-keys [:_except :_only]
        global-keys [:route-op :method :action-name :suffixes :route-args]]
    {:prune-opts (select-keys options prune-keys)
     :action-opts (apply dissoc options (into prune-keys global-keys))
     :globals (select-keys options global-keys)}))

(defn- merge-globals
  [globals]
  (into {} (map (fn [[key opts]]
                  [key (merge opts globals)])
                resource-actions)))

(defmacro resource-routes
  [name & options]
  (let [
        {:keys [action-opts prune-opts globals]} (separate-options (rest-map options))
        actions (prune (merge-with merge (merge-globals globals) action-opts) prune-opts)]
    `(compojure.core/routes
      ~@(map (fn [[_ action-config]]
               (route-template name action-config))
             (filter second actions)))))

(defmacro defroutemacro
  [macro-name & globals]
  `(defmacro ~macro-name
     [name# & options#]
     (let [globals# (quote ~globals)]
       `(resource-routes ~name# ~@(into globals# (reverse options#))))))