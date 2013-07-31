(ns neubite.views.common
  (:use ring.util.response)
  (:require ;; [clabango.parser :refer [render-file]]
            [selmer.parser :refer [render-file]]
            [neubite.middleware :refer [g]]))


(defn render-template [filename context]
  (let [template-context (merge @g context)]
    (response (render-file filename template-context))))
