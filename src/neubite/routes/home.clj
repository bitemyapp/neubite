(ns neubite.routes.home
  (:use compojure.core)
  (:require [noir.response :refer [redirect]]
            [neubite.middleware :refer [g put-context session-put!]]
            [neubite.models :refer [auth-user get-user-by-email]]
            [neubite.views.common :refer [render-template]]))

(defn logout []
  (session-put! :user-email "")
  (redirect "/"))

(defn login-page [params]
  "login handler, GET and POST"
  (let [email (:email params)
        password (:password params)
        user? (auth-user email password)
        failed? (if (and (string? email) (not user?)) "Failed to log you in." nil)]
    (if user?
      (do
        (session-put! :user-email (:email user?))
        (redirect "/"))
      (render-template "neubite/templates/login.html" {:error failed? :email email}))))

(defn home-page []
  "index page"
  (render-template "neubite/templates/index.html" {}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/login/" {params :params} (login-page params))
  (POST "/login/" {params :params} (login-page params))
  (GET "/logout/" [] (logout)))
