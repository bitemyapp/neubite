(ns neubite.views.common
  (:use ring.util.response)
  (:require [selmer.parser :refer [render-file]]
            limit))


(defn render-template-fn [filename context]
  (let [user (:user (:request context))
        context (assoc context :user user)]
    (response (render-file filename context))))

(defmacro render-template [filename context]
  `(render-template-fn ~filename (assoc ~context :request ~'request)))
