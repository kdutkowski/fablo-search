(ns ^:figwheel-always fablo-search-task.core
  (:require [rum.core :as rum]))

(rum/defc value < rum/reactive
  [*ref]
  [:code (pr-str (rum/react *ref))])

(enable-console-print!)

(rum/defc reactive-input < rum/reactive
  [*ref]
  (let [value (rum/react *ref)]
    [:input { :type "text"
             :value value
             :on-change (fn [e] (reset! *ref (str (.. e -currentTarget -value)))) }])
  )

(rum/defc search-box [label *ref button-text] < rum/reactive []
  [:label label ": "
   (reactive-input *ref)
    [:button { :on-click (fn [e] (println (deref *ref))) } button-text]
   ]
    )

(defn el [id]
  (.getElementById  js/document id))

(defn mount-components []
  (let [*ref (atom nil)]
  (rum/mount (search-box "Search" *ref "Search") (el "main-component"))
  ))


(mount-components)