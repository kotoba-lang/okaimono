(ns kotoba.okaimono.export-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.string :as str]
            [kotoba.okaimono :as ok]
            [kotoba.okaimono.export :as ex]))

(def lines [(ok/line "sku-1" "Beans, dark roast" 2 900)
            (ok/line "sku-2" "Mug \"L\"" 1 1200)])

(def orders
  [(-> (ok/order "ok-1" "st-1" {} lines :cod? true)
       (ok/advance :confirmed) (ok/advance :packed))
   (ok/order "ok-2" "st-1" {} lines)])

(deftest orders-csv-test
  (let [csv (ex/orders->csv orders)]
    (is (str/starts-with? csv "order_id,store,items,total,currency,cod,dispatchable,status"))
    (is (str/includes? csv "ok-1,st-1,3,3000,JPY,yes,yes,packed"))
    (is (str/includes? csv "ok-2,st-1,3,3000,JPY,no,no,placed"))))

(deftest lines-csv-test
  (let [csv (ex/lines->csv (first orders))]
    (testing "RFC-4180 quoting for embedded commas and quotes"
      (is (str/includes? csv "\"Beans, dark roast\""))
      (is (str/includes? csv "\"Mug \"\"L\"\"\"")))
    (is (str/includes? csv "1800"))))

(deftest orders-json-test
  (let [json (ex/orders->json orders)]
    (is (str/includes? json "\"total\":3000"))
    (is (str/includes? json "\"cod\":true"))
    (is (str/includes? json "\"dispatchable\":false"))))
