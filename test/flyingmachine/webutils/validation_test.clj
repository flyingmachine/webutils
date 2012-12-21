(ns flyingmachine.webutils.validation-test
  (:use clojure.test
        flyingmachine.webutils.validation))

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

(deftest error-messages-for-test
  (testing "return a seq of error messages when there are errors"
    (is (= (error-messages-for "joe" (:username validations))
           ["Your username must be between 4 and 24 characters long"])))

  (testing "return empty when there are no errors"
    (is (empty? (error-messages-for "henry" (:username validations))))))

(deftest validate-test
  (testing "given a record, return a map with errors for the record's keys"
    (let [errors (validate {:username "martin"} validations)] 
      (is (= ["That username is already taken"]
             (:username errors)))
      (is (= ["Your password must be at least 4 characters long"]
             (:password errors))))))

(deftest if-valid-test
  (testing "assigns errors to given symbol"
    (if-valid
     {:username "martin"} validations errors
     false
     (do
       (is (= ["That username is already taken"]
              (:username errors)))
       (is (= ["Your password must be at least 4 characters long"]
              (:password errors))))))
  (testing "handles valid condition correctly"
    (if-valid
     {:username "henry" :password "validpassword"} validations errors
     (is true)
     (is false))))