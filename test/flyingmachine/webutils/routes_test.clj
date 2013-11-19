(ns flyingmachine.webutils.routes-test
  (:require [compojure.core :refer (GET PUT POST DELETE ANY defroutes)]
            [clojure.test :refer :all]
            [flyingmachine.webutils.routes :refer :all]
            [flyingmachine.webutils.fixtures.fake-controller :as fc]
            [ring.mock.request :as req]
            [ring.middleware.params :refer :all]
            [ring.middleware.keyword-params :refer :all]
            [ring.middleware.nested-params :refer :all]))

(defn body
  [content]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body content})

(defn app
  [routes]
  (-> routes
      wrap-keyword-params
      wrap-nested-params
      wrap-params))

(defn auth-info
  [req]
  {:userid "joeschmoe"})

(deftest action-test
  (testing "Returns the correct symbol"
    (is (= (action "ns" "fn")
           'ns/fn))))

(deftest route-test
  (testing "route macro will handle ring request"
    (is (= ((route GET "/floozles" fc/query) (req/request :get "/floozles"))
           (body "fake-controller-query")))))

(deftest resource-routes-test
  (testing "default configuration works"
    (defroutes default-routes (resource-routes fc))
    (let [test-app (app default-routes)]
      (is (= (test-app (req/request :get "/fc"))
             (body "fake-controller-query")))
      (is (= (test-app (req/request :get "/fc/1"))
             (body "fake-controller-show-id-1")))
      (is (= (test-app (req/request :post "/fc" {:name "joe"}))
             (body "fake-controller-create!-name-joe")))
      (is (= (test-app (req/request :put "/fc/1"))
             (body "fake-controller-update!-id-1")))
      (is (= (test-app (req/request :delete "/fc/1"))
             (body "fake-controller-delete!-id-1")))))
  (testing "can supply action configs"
    (defroutes custom-routes
      (resource-routes fc
                       :_except [:update! :query]
                       :show {:method PUT
                              :action-name custom-show}))
    (let [test-app (app custom-routes)]
      (is (= (test-app (req/request :put "/fc/1"))
             (body "fake-controller-custom-show-id-1")))
      (is (nil? (test-app (req/request :get "/fc"))))))
  (testing "works with auth"
    (defroutes auth-routes (resource-routes fc
                                            :create! {:action-name auth-create!
                                                      :route-op authroute
                                                      :route-args [auth-info]}))
    (let [test-app (app auth-routes)]
      (is (= (test-app (req/request :post "/fc" {:name "joe"}))
             (body "fake-controller-auth-create!-name-joe-userid-joeschmoe")))))
  (testing "can define globals"
    (defroutes global-routes (resource-routes fc
                                              :route-op authroute
                                              :route-args [auth-info]
                                              :create! {:action-name auth-create!}))
    (let [test-app (app global-routes)]
      (is (= (test-app (req/request :post "/fc" {:name "joe"}))
             (body "fake-controller-auth-create!-name-joe-userid-joeschmoe"))))))