(ns neubite.routes.home
  (:use compojure.core
        ring.util.response)
  (:require [neubite.middleware :refer [g put-context]]
            [neubite.models :refer [auth-user get-user-by-email]]
            [neubite.util :refer [dissoc-in]]
            [neubite.views.common :refer [render-template]]))

(defn login-page [params]
  "login handler, GET and POST"
  (let [email (:email params)
        password (:password params)
        user? (auth-user email password)
        failed? (if (and (string? email) (not user?)) "Failed to log you in." nil)]
    (if user?
      (let [resp (assoc (redirect "/") :session {:user-email email})]
        resp)
      (render-template "neubite/templates/login.html" {:error failed? :email email}))))

(defn logout []
  (dissoc-in (redirect "/") [:session :user-email]))

(defn home-page []
  "index page"
  (render-template "neubite/templates/index.html" {}))

(defn projects-page []
  "projects page"
  (render-template "neubite/templates/projects.html" {}))

(defroutes home-routes
  (GET  "/" [] (home-page))
  (GET  "/projects/" [] (home-page))
  (GET  "/login/" {params :params} (login-page params))
  (POST "/login/" {params :params} (login-page params))
  (GET  "/logout/" [] (logout)))
