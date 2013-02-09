(ns neubite.handler
  (:use [compojure.handler :only [site]]
        [noir.validation :only [wrap-noir-validation]]
        [noir.cookies :only [wrap-noir-cookies]]
        [ring.middleware.multipart-params :only [wrap-multipart-params]]
        [ring.server.standalone :only [serve]]
        neubite.middleware
        neubite.routes.admin
        neubite.routes.home
        compojure.core)
  (:require [noir.util.middleware :as middleware]
            [noir.cookies :refer [wrap-noir-cookies]]
            [compojure.route :as route]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init []
  (println "neubite started successfully..."))

(defn destroy []
  (println "shutting down..."))

(def all-routes [admin-routes home-routes app-routes])
;; (def app (middleware/app-handler all-routes))
(def app (-> (apply routes all-routes)
             (site)
             (user-middleware)
             (context-middleware)
             (middleware/wrap-request-map)
             (wrap-noir-validation)
             (wrap-multipart-params)
             (wrap-noir-cookies)))
(def war-handler (middleware/war-handler app))

(defn boot []
  (serve #'app {:port 8080
                :open-browser? true
                :stacktraces? true
                :auto-reload? true
                :auto-refresh? nil
                :join? nil}))
