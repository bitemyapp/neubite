(ns neubite.routes.admin
  (:use compojure.core)
  (:require [monger.collection :as mc]
            [clabango.filters :refer [deftemplatefilter]]
            [monger.query :as mq]
            [neubite.middleware :refer [g
                                        put-context
                                        superuser-only]]
            [neubite.models :refer [get-document-by-id
                                    create-post
                                    to-object-id]]
            [neubite.views.common :refer [render-template]]))

(defn delete [params]
  "Delete stuff"
  (let [coll (:coll params)
        id (:id params)]
    (println coll)
    (println id)
    (if (and coll id)
      (do
        (mc/remove coll {:_id (to-object-id id)})
        (redirect "/admin/"))
      "blah")))

(defn write [params]
  "Put up posts"
  (let [title (:title params)
        body (:body params)]
    (if (and title body)
      (do
        (create-post title body)
        (redirect "/admin/"))
      (render-template
       "neubite/templates/admin/write.html"
       {}))))

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
  (POST "/admin/delete/:coll/:id/" {params :params} (superuser-only delete params))
  (GET  "/admin/write/" {params :params} (superuser-only write params))
  (POST "/admin/write/" {params :params} (superuser-only write params))
  (GET  "/admin/edit/:coll/:id/" [coll id] (superuser-only edit coll id))
  (GET  "/admin/" [] (superuser-only admin-home)))
