(ns ^:figwheel-always fablo-search-task.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [rum.core :as rum]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(enable-console-print!)

(rum/defc query-input < rum/reactive
  [*ref]
    [:input { :type "text"
              :on-change (fn [e] (reset! *ref (str (.. e -currentTarget -value)))) }]
  )

(defn query-fablo [*query-text *results-list]
  (fn [e] (go (let [response (<! (http/get "https://api.fablo.pl/api/2/frisco.pl/products/query?"
                                           {:with-credentials? false
                                            :query-params {"results" 12
                                                           "search-string" (deref *query-text)}}))]
                (reset! *results-list (:results (:products (:body response))))
                ))))

(defn clear-results [*results-list]
  (fn [e] (reset! *results-list [])))

(rum/defc search-button < rum/reactive
  [*query-text *results-list button-text]
  (let []
    [:button {:on-click (if-not (clojure.string/blank? (deref *query-text))
                          (query-fablo *query-text *results-list)
                          (clear-results *results-list))
              } button-text])
  )

(rum/defcs search-box < (rum/local "" ::key)
  [state *results-list button-text]
  (let [local-atom (::key state)]
  [:div (query-input local-atom)
    (search-button local-atom *results-list button-text)]
    ))

(rum/defc list-item < rum/static [item]
  [:li [:span (:name item)]])

(rum/defc results-list < rum/reactive [*results-list]
  (let [items (rum/react *results-list)]
  [:ul
   (for [item items]
     (rum/with-key (list-item item) (:id item)))])
  )

(defn get-element-by-id [id]
  (.getElementById  js/document id))

(defn mount-components []
  (let [*results-list (atom nil)]
  (rum/mount (search-box *results-list "Search") (get-element-by-id "main-component"))
  (rum/mount (results-list *results-list) (get-element-by-id "results-list"))
  ))


(mount-components)