(ns neubite.routes.blog
  (:use compojure.core
        ring.util.response
        slugify.core)
  (:require [neubite.middleware :refer [g put-context]]
            [neubite.models :refer [auth-user
                                    get-most-recent-posts
                                    get-post-by-slug
                                    get-user-by-email]]
            [neubite.util :refer [dissoc-in]]
            [clabango.filters :refer [deftemplatefilter]]
            [neubite.views.common :refer [render-template]]))

(defn post-single [slug]
  "blog main page"
  (let [post (get-post-by-slug slug)]
    (render-template "neubite/templates/blog/single.html" {:post post})))

(defn blog-home []
  "blog main page"
  (let [posts (get-most-recent-posts)]
    (render-template "neubite/templates/blog/home.html" {:posts posts})))

(defroutes blog-routes
  (GET "/post/:slug/" [slug] (post-single slug))
  (GET "/" [] (blog-home)))
