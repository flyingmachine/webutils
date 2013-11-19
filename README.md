# webutils

A kind-of, almost, not-really-at-all microframework for creating an
MVC app in Clojure.

Uses a lot of the ideas covered in
[Look, Ma! No batteries! A Clojure Web App Without Noir](http://www.flyingmachinestudios.com/programming/dissecting-gratefulplace/).

The code is all extracted from https://github.com/flyingmachine/gratefulplace , so if you're interested in real world usage you can look there.

## Usage

Add this to your project.clj:

```clojure
[com.flyingmachine/webutils "0.1.0"]
```

There are a handful of namespaces:

### Paths

Convenience methods for generating paths and generating
path-generating fns.

Examples:

```clojure
(path {:username "joe"} :username "users")
;; "/users/joe"

(path {:username "joe"} :username "users" "edit")
;; "/users/joe/edit"

;; create-path-fns generates a bunch of related path functions. For example,
;; if you run the following:
(create-path-fns "user" :username "edit" "posts" "comments" "notification-settings")
;; then it's equivalent to

(clojure.core/defn
 user-path
 [record]
 (flyingmachine.paths/path record :username "users"))

(clojure.core/defn
 user-edit-path
 [record]
 (flyingmachine.paths/path record :username "users" "edit"))

(clojure.core/defn
 user-posts-path
 [record]
 (flyingmachine.paths/path record :username "users" "posts"))

(clojure.core/defn
 user-comments-path
 [record]
 (flyingmachine.paths/path
  record
  :username
  "users"
  "comments"))

(clojure.core/defn
 user-notification-settings-path
 [record]
 (flyingmachine.paths/path
  record
  :username
  "users"
  "notification-settings"))

;; 
;; Now call the generated functions:
;;

(user-path {:username "bob"})
;; "/users/bob"

(user-comments-path {:username "bob"})
;; "/users/bob/comments"

(let [comment-id 2]
  (user-comments-path {:username "bob"} "#comment-id-" comment-id))
;; "/users/bob/comments#comment-id-2
```

### Validation

Ultimately, you'll write something like:

```clojure
(defonce validations
  {:username
   ["Your username must be between 4 and 24 characters long"
    #(and
      (>= (count %) 4)
      (<= (count %) 24))
    
    "That username is already taken"
    #(not= % "martin")]
   
   :password
   ["Your password must be at least 4 characters long"
    #(>= (count %) 4)]})

(let [params (:params res)]
  (if-valid
   params validations errors
   ;; When the params are valid, do this...
   (do
     (user/update! params)
     (res/redirect (user-path params)))
   ;; ...otherwise, do this
   (view/edit params)))
```

Check out the tests and the actual validation.clj file for more info

### Controllers

Right now there's only a macro for defining "view" macros. The purpose
of the view macro is really just to enforce a contract between
controllers and the view functions they call.

Example:

```clojure
(ns someapp.controllers.users
  (:require [someapp.views.users :as view]
            [someapp.models.user :as user]
            [cemerick.friend :as friend]))
  
(defview mainview
  {:current-auth '(friend/current-authentication)
   :errors {}
   :params 'params})

;; "view/show" will be called with one argument, a map
;; with keys :current-auth, :errors, :params, and :user
(def show-user
 [params]
 (mainview view/show-user :user (user/one params))
```

## TODO

* Allow custom resource-actions in routes
* Better naming in routes

## License

Copyright © 2012 Daniel Higginbotham

Distributed under the Eclipse Public License, the same as Clojure.
