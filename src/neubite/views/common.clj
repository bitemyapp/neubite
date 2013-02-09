(ns neubite.views.common
  (:require [clabango.parser :refer [render-file]]
            [neubite.middleware :refer [g]]))

(defn render-template [filename context]
  (let [template-context (merge @g context)]
    (render-file filename template-context)))
