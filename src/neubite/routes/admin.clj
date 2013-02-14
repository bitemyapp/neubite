(ns neubite.routes.admin
  (:use compojure.core
        ring.util.response)
  (:require [monger.collection :as mc]
            [clabango.filters :refer [deftemplatefilter]]
            [monger.query :as mq]
            [neubite.middleware :refer [g
                                        put-context
                                        superuser-only]]
            [neubite.models :refer [get-document-by-id
                                    create-post
                                    to-object-id
                                    update]]
            [neubite.views.common :refer [render-template]]))

(defn publish [params]
  "Delete stuff"
  (let [publish (= (:action params) "publish")
        id (:id params)]
    (if id
      (do
        (update "posts" id {:published publish})
        (redirect "/admin/"))
      "No id found")))

(defn delete [params]
  "Delete stuff"
  (let [coll (:coll params)
        id (:id params)]
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

(defn edit-post [params]
  (let [id (:id params)
        nt (:title params)
        np (:body params)
        edited? (and nt np)
        doc? (get-document-by-id "posts" id)
        title (or nt (:title doc?))
        body (or np (:body doc?))]
    (when edited?
      (update "posts" id {:title title :body body}))
    (render-template
     "neubite/templates/admin/write.html"
     {:title title :body body})))

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
  (POST "/admin/publish/posts/:id/" {params :params} (superuser-only publish params))
  (POST "/admin/delete/:coll/:id/" {params :params} (superuser-only delete params))
  (GET  "/admin/write/" {params :params} (superuser-only write params))
  (POST "/admin/write/" {params :params} (superuser-only write params))
  (GET  "/admin/edit/post/:id/" {params :params} (superuser-only edit-post params))
  (POST  "/admin/edit/post/:id/" {params :params} (superuser-only edit-post params))
  (GET  "/admin/" [] (superuser-only admin-home)))
