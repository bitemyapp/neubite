(ns neubite.middleware
  (:use carica.core)
  (:require [neubite.models :refer [get-user-by-email]]))

(def ^:dynamic g (atom {}))
(def secret (config :secret))

(defn wrap-if [handler pred wrapper & args]
  (if pred
    (apply wrapper handler args)
    handler))

(defn put-context [key val]
  (swap! g assoc key val))

(defn context-middleware [app]
  (fn [req]
    (reset! g {})
    (app req)))

(defn user-middleware [app]
  (fn [req]
    (let [email (:user-email (:session req))
          user (if (not (= email "")) (get-user-by-email email) nil)]
      (when user
        (put-context :user user))
      (app req))))

(defn print-middleware [app]
  (fn [req]
    (println req)
    (app req)))
