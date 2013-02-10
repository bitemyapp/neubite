(ns neubite.routes.admin
  (:use compojure.core)
  (:require [monger.collection :as mc]
            [clabango.filters :refer [deftemplatefilter]]
            [monger.query :as mq]
            [neubite.middleware :refer [g put-context superuser-only]]
            [neubite.models :refer [get-document-by-id]]
            [neubite.views.common :refer [render-template]]))

(defn write [params]
  (render-template
   "neubite/templates/admin/write.html"
   {}))

(defn edit [coll id]
  (let [doc? (get-document-by-id coll id)
        fields (map name (keys doc?))
        values (vals doc?)]
    (render-template
     "neubite/templates/admin/edit.html"
     {:fields fields :values values})))

(defn admin-home []
  "admin home page"
  (let [users (mq/with-collection "users"
                (mq/find {}))
        posts (mq/with-collection "posts"
                (mq/find {})
                (mq/sort (sorted-map :date_created -1)))]
    (render-template
     "neubite/templates/admin/index.html"
     {:users users :posts posts})))

(defroutes admin-routes
  (GET  "/admin/write/" {params :params} (superuser-only write params))
  (POST "/admin/write/" {params :params} (superuser-only write params))
  (GET  "/admin/edit/:coll/:id/" [coll id] (superuser-only edit coll id))
  (GET  "/admin/" [] (superuser-only admin-home)))
