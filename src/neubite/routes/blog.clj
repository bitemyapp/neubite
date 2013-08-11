(ns neubite.routes.blog
  (:use compojure.core
        ring.util.response
        slugify.core)
  (:require [neubite.models :refer [auth-user
                                    get-flatpage-by-url
                                    get-most-recent-posts
                                    get-post-by-slug
                                    get-user-by-email]]
            [neubite.util :refer [dissoc-in]]
            [neubite.views.common :refer [render-template]]))

(defn flatpage [request params]
  "Render flatpage"
  (let [url (:flatpage params)
        flatpage (get-flatpage-by-url url)
        content (:content flatpage)
        js (:js flatpage)
        css (:css flatpage)]
    (if (and flatpage content)
      (render-template "neubite/templates/flatpages/main.html" {:content content
                                                                :js js
                                                                :css css})
      (redirect "/"))))

(defn post-single [request slug]
  "blog main page"
  (let [post (get-post-by-slug slug)]
    (render-template "neubite/templates/blog/single.html" {:post post})))

(defn blog-home [request]
  "blog main page"
  (let [posts (get-most-recent-posts)]
    (render-template "neubite/templates/blog/home.html" {:posts posts})))

(defroutes blog-routes
  (GET "/post/:slug/" [slug :as request] (post-single request slug))
  (GET "/" request (blog-home request))
  (GET "/:flatpage/" {params :params :as request} (flatpage request params)))
