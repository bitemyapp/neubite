(ns neubite.routes.admin
  (:use compojure.core
        ring.util.response)
  (:require [monger.collection :as mc]
            [monger.query :as mq]
            [neubite.middleware :refer [superuser-only]]
            [neubite.models :refer [get-document-by-id
                                    create-flatpage
                                    get-flatpage-by-url
                                    create-post
                                    to-object-id
                                    update]]
            [neubite.views.common :refer [render-template]]))

(defn make-flatpage [request params]
  "Make a flatpage"
  (let [url (:url params)
        content (:content params)
        css (:css params)
        js (:js params)
        edit (:edit params)]
    (if (and url (not edit))
      (let [extant (get-flatpage-by-url url)]
        (if extant
          (update "flatpages" (:_id extant) (merge extant {:url url :content content :css css :js js}))
          (create-flatpage url content css js))
        (redirect "/admin/"))
      (render-template "neubite/templates/admin/flatpage.html" {:url url
                                                                :content content
                                                                :css css
                                                                :js js}))))

(defn edit-flatpage [request params]
  "Edit a flatpage"
  (let [id (:id params)
        flatpage (get-document-by-id "flatpages" id)]
    (make-flatpage request (merge {:edit true} flatpage))))

(defn publish [request params]
  "Publish stuff"
  (let [publish (= (:action params) "publish")
        coll (:coll params)
        id (:id params)]
    (if (and id coll)
      (do
        (update coll id {:published publish})
        (redirect "/admin/"))
      "No id found")))

(defn delete [request params]
  "Delete stuff"
  (let [coll (:coll params)
        id (:id params)]
    (if (and coll id)
      (do
        (mc/remove coll {:_id (to-object-id id)})
        (redirect "/admin/"))
      "blah")))

(defn write [request params]
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

(defn edit-post [request params]
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

(defn admin-home [request params]
  "admin home page"
  (let [users (mq/with-collection "users"
                (mq/find {}))
        posts (mq/with-collection "posts"
                (mq/find {})
                (mq/sort (sorted-map :date_created -1)))
        flatpages (mq/with-collection "flatpages"
                    (mq/find {})
                    (mq/sort (sorted-map :date_created -1)))]
    (render-template
     "neubite/templates/admin/index.html"
     {:users users :posts posts :flatpages flatpages})))

(defroutes admin-routes
  (GET "/admin/edit/page/:id/" {params :params :as request} (superuser-only edit-flatpage request params))
  (POST "/admin/edit/page/:id/" {params :params :as request} (superuser-only edit-flatpage request params))
  (GET "/admin/write/flatpage/" {params :params :as request} (superuser-only make-flatpage request params))
  (POST "/admin/write/flatpage/" {params :params :as request} (superuser-only make-flatpage request params))
  (POST "/admin/publish/:coll/:id/" {params :params :as request} (superuser-only publish request params))
  (POST "/admin/delete/:coll/:id/" {params :params :as request} (superuser-only delete request params))
  (GET  "/admin/write/" {params :params :as request} (superuser-only write request params))
  (POST "/admin/write/" {params :params :as request} (superuser-only write request params))
  (GET  "/admin/edit/post/:id/" {params :params :as request} (superuser-only edit-post request params))
  (POST  "/admin/edit/post/:id/" {params :params :as request} (superuser-only edit-post request params))
  (GET  "/admin/" {params :params :as request} (superuser-only admin-home request params)))
