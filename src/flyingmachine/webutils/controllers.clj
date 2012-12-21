(ns flyingmachine.webutils.controllers)

(defmacro defview
  "provides defaults for the map provided to view functions and allows
  you to provide additional key value pairs.

  example: 
  (defview mainview
    {:current-auth '(friend/current-authentication)
     :errors {}
     :params 'params})"
  
  [name defaults]
  `(defmacro ~name
     [view-fn# & keys#]
     `(~view-fn# (into ~~defaults (map vec (partition 2 ~(vec keys#)))))))