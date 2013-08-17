(ns neubite.handler
  (:use [compojure.handler :only [site]]
        [noir.validation :only [wrap-noir-validation]]
        [noir.cookies :only [wrap-noir-cookies]]
        [ring.middleware.multipart-params :only [wrap-multipart-params]]
        [ring.server.standalone :only [serve]]
        neubite.middleware
        neubite.routes.admin
        neubite.routes.blog
        neubite.routes.home
        compojure.core)
  (:require [noir.util.middleware :as middleware]
            [bulwark.core :refer [protect-middleware blacklist throttle]]
            [org.httpkit.server :refer [run-server]]
            [noir.cookies :refer [wrap-noir-cookies]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [neubite.config :refer [config]]
            [ring.middleware.session.cookie :refer [cookie-store]]
            [compojure.route :as route])
  (:import [org.joda.time DateTimeZone]))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init-db []
  (DateTimeZone/setDefault DateTimeZone/UTC)
  (let [db-config (config :dburi)]
    (when-not db-config
      (println "WARNING, db-config NOT SET!"))
    (mg/connect-via-uri! db-config)
    (mc/ensure-index "users" {:email 1} {:unique true})
    (mc/ensure-index "posts" {:slug 1} {:unique true})
    (mc/ensure-index "flatpages" {:url 1} {:unique true})))

(defn init []
  (init-db))

(defn destroy []
  (println "shutting down..."))

(def all-routes [admin-routes home-routes blog-routes app-routes])
(def app (-> (apply routes all-routes)
             (user-middleware)
             (protect-middleware)
             (site {:session {:cookie-name "session"
                              :store (cookie-store
                                      {:key (config :secret)})}})
             (middleware/wrap-request-map)
             (wrap-multipart-params)))

(def war-handler (middleware/war-handler app))

(defn boot []
  (init)
  (serve #'app {:port 8080
                :open-browser? true
                :stacktraces? true
                :auto-reload? true
                :auto-refresh? nil
                :join? nil}))

(defn -main [& args]
  (init)
  (println "starting http-kit server for neubite on http://localhost:8080/")
  (run-server #'app {:port 8080}))
