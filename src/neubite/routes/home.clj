(ns neubite.routes.home
  (:use compojure.core
        ring.util.response)
  (:require [neubite.models :refer [auth-user get-user-by-email]]
            [neubite.util :refer [dissoc-in]]
            [neubite.views.common :refer [render-template]]))

(defn login-page [request params]
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

(defroutes home-routes
  (GET  "/login/" {params :params :as request} (login-page request params))
  (POST "/login/" {params :params :as request} (login-page request params))
  (GET  "/logout/" [] (logout)))
