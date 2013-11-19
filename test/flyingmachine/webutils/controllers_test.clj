(ns flyingmachine.webutils.controllers-test
  (:use clojure.test
        flyingmachine.webutils.controllers))

(defview testview
  {:constant "constant"
   :scoped   'scoped
   :scopedfn '(scopedfn)})

(deftest defview-test
  (let [scoped "scoped"
        scopedfn (fn [] "scopedfn")]
    (testing "scoped vars are available"
      (testview
       (fn [params]
         (is (= (:scoped params)
                "scoped"))
         (is (= (:scopedfn params)
                "scopedfn")))))
    
    (testing "default map is available"
      (testview
       (fn [params]
         (is (= (:constant params)
                "constant")))))
    (testing "additional pairs are available"
      (testview
       (fn [params]
         (is (= (:additional1 params)
                "present"))
         (is (= (:additional2 params)
                "also present")))     
       :additional1 "present"
       :additional2 "also present"))))
