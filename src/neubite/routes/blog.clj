(ns neubite.routes.blog
  (:use compojure.core
        ring.util.response
        slugify.core)
  (:require [neubite.middleware :refer [g put-context]]
            [neubite.models :refer [auth-user get-user-by-email]]
            [neubite.util :refer [dissoc-in]]
            [clabango.filters :refer [deftemplatefilter]]
            [neubite.views.common :refer [render-template]]))

(deftemplatefilter render-blog [blog]
  (let [title (:title blog)
        body (:body blog)]
    (render-template "neubite/templates/blog/post_partial.html" {})))

(deftemplatefilter slugify-post [post]
  (slugify (:title post)))

(defn blog-home []
  "blog main page"
  (render-template "neubite/templates/blog/home.html" {}))

(defroutes blog-routes
  (GET "/blog/" [] (blog-home)))
