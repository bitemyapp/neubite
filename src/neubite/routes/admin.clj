(ns neubite.routes.admin
  (:use compojure.core)
  (:require [noir.response :refer [redirect]]
            [neubite.middleware :refer [g put-context session-put!]]
            [neubite.models :refer [auth-user get-user-by-email]]
            [neubite.views.common :refer [render-template]]))

(defn admin-home []
  "admin home page"
  (render-template "neubite/templates/admin/index.html" {:users [] :tasks []}))

(defroutes admin-routes
  (GET "/admin/" [] (admin-home)))
