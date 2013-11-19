(ns flyingmachine.webutils.fixtures.fake-controller)

(defn query
  [params]
  "fake-controller-query")

(defn show
  [params]
  (str "fake-controller-show-id-" (:id params)))

(defn custom-show
  [params]
  (str "fake-controller-custom-show-id-" (:id params)))

(defn create!
  [params]
  (str "fake-controller-create!-name-" (:name params)))

(defn update!
  [params]
  (str "fake-controller-update!-id-" (:id params)))

(defn delete!
  [params]
  (str "fake-controller-delete!-id-" (:id params)))

(defn auth-create!
  [params auth]
  (str "fake-controller-auth-create!-name-" (:name params) "-userid-" (:userid auth)))